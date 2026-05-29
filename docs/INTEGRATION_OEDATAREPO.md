# Intégration oedatarepo (OpenELIS consolidé)

> LSTracker récupère le statut et les dates d'analyse depuis le serveur OpenELIS consolidé (`oedatarepo`), à partir du **numéro de laboratoire** (`labNumber`) saisi lors de la réception de l'échantillon au labo.

## Sommaire

1. [Vue d'ensemble](#vue-densemble)
2. [Contrat de l'API oedatarepo](#contrat-de-lapi-oedatarepo)
3. [Mapping des statuts](#mapping-des-statuts)
4. [Configuration côté LSTracker](#configuration-côté-lstracker)
5. [Prérequis côté oedatarepo](#prérequis-côté-oedatarepo)
6. [Fonctionnement (job + refresh)](#fonctionnement-job--refresh)
7. [Test bout en bout](#test-bout-en-bout)
8. [Troubleshooting](#troubleshooting)

---

## Vue d'ensemble

```
  LSTracker (Spring)                         oedatarepo (OpenELIS consolidé)
  ─────────────────                          ───────────────────────────────
  Job @Scheduled (30 min)  ──┐
  ou refresh à la demande    │  POST /auth/token (Basic → JWT)
                             ├──────────────────────────────────►
                             │  GET /api/v1/order-analysis/{labno}
                             │      Authorization: Bearer <jwt>
                             ◄──────────────────────────────────┘
                                { sampleStatus, startedDate, completedDate,
                                  releasedDate, resultReady, failed,
                                  lstrackerStatus, analysisCount }
  Applique dates + statut
  sur le Sample (labNumber)
```

**Sens du flux** : LSTracker INTERROGE oedatarepo (pull). oedatarepo n'a pas connaissance de LSTracker.

**Déclenchement** : job périodique (échantillons au labo en attente de résultat) + endpoint de refresh à la demande.

---

## Contrat de l'API oedatarepo

`GET /api/v1/order-analysis/{labno}` — sécurisé JWT (chaîne `/api/**`), produit du JSON.

Réponse 200 :
```json
{
  "labno": "0000000001127",
  "sampleStatus": "Finalized",
  "startedDate": "2024-06-21T00:00:00",
  "completedDate": "2024-06-21T00:00:00",
  "releasedDate": "2025-02-19T00:00:00",
  "resultReady": true,
  "failed": false,
  "lstrackerStatus": "RESULT_READY",
  "analysisCount": 1
}
```
404 si le `labno` est inconnu côté oedatarepo.

**Consolidation côté oedatarepo** (à partir de `order_synced` + `analysis`) :
- `startedDate` = min(`analysis_started_date`), `completedDate` = max(`analysis_completed_date`), `releasedDate` = max(`analysis_released_date`).
- `lstrackerStatus` décidé par priorité :
  1. `RESULT_READY` si `sample_status = Finalized` (échantillon globalement validé → résultat délivrable ; un rejet partiel d'analyse n'empêche pas la délivrance).
  2. `NON_CONFORM` si `sample_status = NonConforming` OU une analyse `NonConforming`.
  3. `ANALYSIS_FAILED` si annulation (sample) OU une analyse `Technical Rejected` / `Biologist Rejection`.
  4. `null` si en cours (`Test Entered` / `Testing Started` / `Testing finished`).

---

## Mapping des statuts

| `lstrackerStatus` (oedatarepo) | `ESampleStatus` (LSTracker) | Effet |
|---|---|---|
| `RESULT_READY` | `ANALYSIS_DONE` ("ANALYSE TERMINEE") | Résultat prêt |
| `ANALYSIS_FAILED` | `ANALYSIS_FAILED` ("ECHEC ANALYSE") | Échec à remonter au site |
| `NON_CONFORM` | `NON_CONFORM` ("NON CONFORME") | Non conforme / recollecte |
| `null` | *(aucun changement)* | Analyse en cours |

Les 3 dates (`analysis_started_date`, `analysis_completed_date`, `analysis_released_date`) sont toujours mises à jour sur le Sample si fournies, même quand le statut ne change pas.

---

## Configuration côté LSTracker

Variables d'environnement (cf. `.env.example`, `.env.demo.example`, `.env.prod.example`) :

| Variable | Rôle | Défaut |
|---|---|---|
| `OEDATAREPO_ENABLED` | Active l'intégration (job + endpoint) | `false` |
| `OEDATAREPO_URL` | URL de base du serveur oedatarepo | *(vide)* |
| `OEDATAREPO_USER` | Login du compte technique (auth Basic) | *(vide)* |
| `OEDATAREPO_PASSWORD` | Mot de passe du compte technique | *(vide)* |

Réglages avancés (optionnels, dans `application.properties`) :

| Propriété | Rôle | Défaut |
|---|---|---|
| `lstracker.oedatarepo.sync.interval-ms` | Intervalle entre deux passages du job | `1800000` (30 min) |
| `lstracker.oedatarepo.sync.initial-delay-ms` | Délai avant le 1er passage après démarrage | `60000` (1 min) |
| `lstracker.oedatarepo.sync.batch-size` | Nombre max d'échantillons par passage | `200` |

**Important** : tant que `OEDATAREPO_ENABLED=false`, l'intégration est totalement inactive (le job n'est pas instancié, l'endpoint répond 503). Aucun impact sur le reste de LSTracker.

---

## Prérequis côté oedatarepo

1. **Un compte technique** dédié à LSTracker, avec un rôle accepté par la chaîne `/api/**` (l'API utilise `@PreAuthorize hasAnyRole('PUSHER','ADMIN')` — décoratif car method-security non activée, mais le compte doit pouvoir s'authentifier via `/auth/token`).
2. L'endpoint `GET /api/v1/order-analysis/{labno}` déployé (cf. commit API côté oedatarepo).
3. Réseau : LSTracker (serveur) doit pouvoir joindre oedatarepo en HTTPS.

---

## Fonctionnement (job + refresh)

### Job périodique
`OeAnalysisSyncJob` — actif si `OEDATAREPO_ENABLED=true`. Toutes les 30 min (configurable), récupère les échantillons éligibles :
- `lab_number` renseigné
- statut NON terminal (pas déjà `ANALYSIS_DONE`/`NON_CONFORM`/`ANALYSIS_FAILED`/`RESULT_COLLECTED`/`RESULT_ON_SITE`)

Pour chacun, interroge oedatarepo et applique dates + statut. Un échec sur un échantillon n'arrête pas le lot.

### Refresh à la demande
`POST /api/tracker/oedatarepo/refresh/{sampleId}` (chaîne web, authentifié + CSRF) :
- Charge le Sample, interroge oedatarepo via son `labNumber`, applique le résultat.
- Réponse : `{ sampleId, labNumber, updated, sampleStatusId }`.
- 503 si l'intégration n'est pas activée/configurée ; 404 si sample inconnu ; 400 si pas de `labNumber`.

---

## Test bout en bout

### 1. Démarrer oedatarepo en local (avec l'API)
```bash
cd /chemin/vers/oedatarepo
./mvnw spring-boot:run -Dspring-boot.run.profiles=local   # écoute sur :8085
```

### 2. Configurer LSTracker pour pointer vers oedatarepo local
Dans le `.env` de LSTracker :
```
OEDATAREPO_ENABLED=true
OEDATAREPO_URL=http://127.0.0.1:8085
OEDATAREPO_USER=<email-compte-technique-oedatarepo>
OEDATAREPO_PASSWORD=<mdp>
# Job plus rapide pour tester :
# (ou laisser le défaut et utiliser le refresh à la demande)
```

### 3. Préparer un échantillon LSTracker avec un labNumber existant côté oedatarepo
Sur la DB LSTracker, mettre `lab_number` d'un sample = un `labno` réel d'oedatarepo (ex: un `Finalized`).

### 4. Tester le refresh à la demande
```bash
# Récupérer un JWT LSTracker (session web) ou utiliser l'UI.
# Depuis l'UI web authentifiée, POST sur :
#   /api/tracker/oedatarepo/refresh/<sampleId>
# Vérifier que le sample passe à ANALYSIS_DONE et que les dates sont remplies.
```

### 5. Tester le job
Mettre `lstracker.oedatarepo.sync.initial-delay-ms=5000` et `interval-ms=60000` temporairement, démarrer LSTracker, observer les logs :
```
Sync oedatarepo : N échantillon(s) examinés, M mis à jour
```

---

## Troubleshooting

| Symptôme | Cause probable | Solution |
|---|---|---|
| Endpoint refresh → 503 | `OEDATAREPO_ENABLED=false` ou URL/credentials vides | Configurer le `.env` et redémarrer |
| Logs "Échec authentification oedatarepo" | Mauvais compte technique / mdp | Vérifier `OEDATAREPO_USER`/`PASSWORD` ; tester `curl -u user:mdp -X POST <url>/auth/token` |
| Logs "labno inconnu (404)" | Le `labNumber` du sample n'existe pas côté oedatarepo | Normal si l'échantillon n'est pas encore dans OpenELIS ; vérifier la saisie du labNumber |
| Statut "introuvable dans sample_status" | Référentiel `sample_status` incomplet | Vérifier que ANALYSIS_DONE/NON_CONFORM/ANALYSIS_FAILED existent dans la table |
| Le job ne tourne pas | Intégration désactivée | `OEDATAREPO_ENABLED=true` requis pour instancier le job |
| Réponse XML au lieu de JSON (côté oedatarepo) | content negotiation | L'API force déjà `produces=JSON` ; si souci, ajouter `Accept: application/json` |
