# Intégration oedatarepo (OpenELIS consolidé)

> LSTracker récupère le statut et les dates d'analyse depuis le serveur OpenELIS consolidé (`oedatarepo`), à partir du **numéro de laboratoire** (`labNumber`) saisi lors de la réception de l'échantillon au labo.

## Sommaire

1. [Vue d'ensemble](#vue-densemble)
2. [Contrat de l'API oedatarepo](#contrat-de-lapi-oedatarepo)
3. [Mapping des statuts](#mapping-des-statuts)
4. [Machine d'état du suivi (outcomes)](#machine-détat-du-suivi-outcomes)
5. [Page d'administration `/sync-openelis`](#page-dadministration-sync-openelis)
6. [Configuration côté LSTracker](#configuration-côté-lstracker)
7. [Tables de suivi](#tables-de-suivi)
8. [Prérequis côté oedatarepo](#prérequis-côté-oedatarepo)
9. [Fonctionnement (job + refresh)](#fonctionnement-job--refresh)
10. [Test bout en bout](#test-bout-en-bout)
11. [Troubleshooting](#troubleshooting)

---

## Vue d'ensemble

```
  LSTracker (Spring)                         oedatarepo (OpenELIS consolidé)
  ─────────────────                          ───────────────────────────────
  Job @Scheduled (30 min)  ──┐
  refresh à la demande       │  POST /auth/token (Basic → JWT)
  page admin /sync-openelis  ├──────────────────────────────────►
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

**Déclenchements** : (1) job périodique sur les échantillons au labo en attente de résultat, (2) refresh à la demande par échantillon, (3) page d'administration `/sync-openelis` (état, prévisualisation, lancement manuel, historique).

---

## Contrat de l'API oedatarepo

`GET /api/v1/order-analysis/{labno}` — sécurisé JWT (chaîne `/api/**`), produit du JSON.

Réponse 200 :
```json
{
  "labno": "0000000001127",
  "sampleStatus": "Testing finished",
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

**Consolidation côté oedatarepo** (à partir de `order_synced` + `analysis`, décidée **au niveau des analyses**, pas du `sample_status`) :
- `startedDate` = min(`analysis_started_date`), `completedDate` = max(`analysis_completed_date`), `releasedDate` = max(`analysis_released_date`).
- Analyses « actives » = hors `Test Canceled` et `Not Tested`.
- `lstrackerStatus` décidé par priorité :
  1. `NON_CONFORM` si `sample_status = NonConforming` OU une analyse `NonConforming`.
  2. `ANALYSIS_FAILED` si annulation (sample `Test Canceled`/`SampleCanceled`) OU une analyse `Technical Rejected` / `Biologist Rejection`.
  3. `RESULT_READY` s'il existe ≥1 analyse active ET que **toutes** les analyses actives sont `Finalized` avec un `released_date`.
  4. `null` si en cours (au moins une analyse active pas encore finalisée).

> Les rejets priment et mettent le labno en échec : côté OpenELIS, un rejet est repris sous un **autre** labno, donc un labno ne mélange jamais rejet et reprise réussie.

---

## Mapping des statuts

| `lstrackerStatus` (oedatarepo) | `ESampleStatus` (LSTracker) | Effet |
|---|---|---|
| `RESULT_READY` | `ANALYSIS_DONE` ("ANALYSE TERMINEE") | Résultat prêt |
| `ANALYSIS_FAILED` | `ANALYSIS_FAILED` ("ECHEC ANALYSE") | Échec à remonter au site |
| `NON_CONFORM` | `NON_CONFORM` ("NON CONFORME") | Non conforme / recollecte |
| `null` | *(aucun changement de statut)* | Analyse en cours |

Les dates (`analysis_completed_date`, `analysis_released_date`) sont toujours mises à jour sur le Sample si fournies, même quand le statut ne change pas. *(Note : `analysis_started_date` est exposé par l'API mais n'est pas persisté côté LSTracker — champ non utilisé / non affiché.)*

---

## Machine d'état du suivi (outcomes)

Chaque tentative de synchronisation d'un échantillon produit un **outcome**, enregistré dans `oedatarepo_sample_sync` (cf. [Tables de suivi](#tables-de-suivi)). L'outcome pilote un **compteur de tentatives** qui sert à cesser d'interroger en boucle les labno absents d'oedatarepo.

| Outcome | Signification | Effet sur `attempts` |
|---|---|---|
| `UPDATED` | Statut et/ou dates modifiés et persistés | remis à `0` |
| `PENDING` | labno présent côté oedatarepo mais analyse encore en cours (`lstrackerStatus` null) | remis à `0` (auto-guérison) |
| `NOT_FOUND` | labno absent d'oedatarepo (404), ou labno non interrogeable (contient `/`, `\`, `?`, `#`, `%`) | **+1** (seul cas qui mène à l'épuisement) |
| `ERROR` | erreur transitoire (réseau, 5xx, authentification) | inchangé |
| `SKIPPED` | client inactif ou labno vide | aucune écriture de suivi |

**Épuisement** : un échantillon est exclu des passages automatiques **et** son refresh ciblé est bloqué (HTTP 409) lorsque `attempts >= max-attempts` ET `last_outcome = NOT_FOUND`. Comme `UPDATED`/`PENDING` remettent le compteur à zéro, un labno qui apparaît plus tard dans oedatarepo réintègre automatiquement le cycle. Un échantillon coincé en NOT_FOUND est réintroduit manuellement via le bouton **Réinitialiser** de la page admin.

---

## Page d'administration `/sync-openelis`

Réservée aux rôles **ADMIN / SUPER_ADMIN** (menu *Administration → Interopérabilité → Suivi Synchro. OpenELIS*). Trois panneaux :

1. **État de l'intégration** — activée/désactivée, URL cible, état de connexion (token OK/KO), exécution en cours, dernier passage (déclencheur, examinés/mis à jour), prochain passage approximatif, et les réglages (`batch-size` / `interval` / `max-attempts`). Bouton **Lancer maintenant** pour exécuter un lot immédiatement.
2. **Échantillons éligibles** — table paginée (filtrable par numéro de labo) montrant statut, dates, nombre de tentatives, dernier résultat, et un marqueur « épuisé ». Actions par ligne :
   - **Prévisualiser** — interroge oedatarepo et affiche *statut actuel vs proposé* + dates, **sans rien persister** ;
   - **Rafraîchir** — applique réellement (refresh ciblé) ;
   - **Réinitialiser** — remet le compteur à zéro (réintroduit un labno épuisé).
3. **Historique des exécutions** — derniers passages (début, fin, déclencheur `SCHEDULED` / `MANUAL:<login>`, examinés, mis à jour, erreurs, durée).

> Le déclenchement manuel respecte la même règle d'épuisement que le job : un échantillon épuisé doit être réinitialisé avant de pouvoir être rafraîchi.

---

## Configuration côté LSTracker

Variables d'environnement (cf. `.env.example`, `.env.demo.example`, `.env.prod.example`) :

| Variable | Rôle | Défaut |
|---|---|---|
| `OEDATAREPO_ENABLED` | Active l'intégration (job + endpoints + page) | `false` |
| `OEDATAREPO_URL` | URL de base du serveur oedatarepo | *(vide)* |
| `OEDATAREPO_USER` | Login du compte technique (auth Basic) | *(vide)* |
| `OEDATAREPO_PASSWORD` | Mot de passe du compte technique | *(vide)* |
| `OEDATAREPO_BATCH_SIZE` | Nombre max d'échantillons par passage | `200` |
| `OEDATAREPO_INTERVAL_MS` | Intervalle entre deux passages du job | `1800000` (30 min) |
| `OEDATAREPO_MAX_ATTEMPTS` | Essais NOT_FOUND avant épuisement d'un labno | `5` |

Propriétés correspondantes dans `application.properties` (avec les mêmes défauts en repli) :
`lstracker.oedatarepo.{enabled,base-url,username,password}` et
`lstracker.oedatarepo.sync.{batch-size,interval-ms,initial-delay-ms,max-attempts}`.

**Important** : tant que `OEDATAREPO_ENABLED=false`, l'intégration est totalement inactive (le job n'est pas instancié, les endpoints répondent 503, la page d'état indique « désactivée »). Aucun impact sur le reste de LSTracker.

**Dimensionnement** : le job traite les `batch-size` échantillons éligibles les plus anciens (`lastupdated_at`) par passage. Si le nombre d'éligibles dépasse `batch-size`, plusieurs passages sont nécessaires pour tout couvrir — ajuster `batch-size`/`interval-ms` en conséquence (un déclenchement manuel reste possible depuis la page).

---

## Tables de suivi

Créées par le changeset Liquibase `db/changelog/changes/2.x/create-oedatarepo-tables.xml`.

**`oedatarepo_sample_sync`** — une ligne par échantillon suivi :

| Colonne | Rôle |
|---|---|
| `sample_id` | FK vers `sample` (unique) |
| `attempts` | compteur de tentatives NOT_FOUND |
| `last_at` | horodatage du dernier essai |
| `last_outcome` | dernier résultat (`UPDATED`/`PENDING`/`NOT_FOUND`/`ERROR`) |
| `last_error` | message court de la dernière erreur (le cas échéant) |

**`oedatarepo_sync_run`** — historique des exécutions :

| Colonne | Rôle |
|---|---|
| `started_at` / `finished_at` | bornes de l'exécution |
| `triggered_by` | `SCHEDULED` ou `MANUAL:<login>` |
| `examined` / `updated` / `errors` | compteurs du passage |
| `duration_ms` | durée |

---

## Prérequis côté oedatarepo

1. **Un compte technique** dédié à LSTracker, capable de s'authentifier via `/auth/token` (l'API utilise `@PreAuthorize hasAnyRole('PUSHER','ADMIN')` — décoratif car method-security non activée côté oedatarepo, c'est donc le JWT seul qui protège la chaîne `/api/**`).
2. L'endpoint `GET /api/v1/order-analysis/{labno}` déployé (cf. doc API côté oedatarepo).
3. Réseau : LSTracker (serveur) doit pouvoir joindre oedatarepo en HTTPS.

---

## Fonctionnement (job + refresh)

La logique de lot est centralisée dans `OeAnalysisBatchService`, partagée par le job planifié et le déclenchement manuel (parité garantie + garde de concurrence : deux exécutions ne se chevauchent pas dans une même instance).

### Job périodique
`OeAnalysisSyncJob` — actif si `OEDATAREPO_ENABLED=true`. À l'intervalle configuré, récupère les échantillons éligibles :
- `lab_number` renseigné ;
- statut NON terminal (pas déjà `ANALYSIS_DONE`/`NON_CONFORM`/`ANALYSIS_FAILED`/`RESULT_COLLECTED`/`RESULT_ON_SITE`) ;
- non épuisés (`attempts < max-attempts` ou dernier outcome ≠ `NOT_FOUND`).

Pour chacun, interroge oedatarepo et applique dates + statut. Un échec sur un échantillon n'arrête pas le lot ; chaque passage écrit une ligne dans `oedatarepo_sync_run`.

### Refresh à la demande (API REST)
`POST /api/tracker/oedatarepo/refresh/{sampleId}` — chaîne web, authentifié + CSRF. Accessible à tout utilisateur authentifié (volontairement non restreint au rôle admin, pour un usage hors page d'administration). Réponse : `{ sampleId, labNumber, updated, sampleStatusId }`.

### Endpoints de la page admin (ADMIN/SUPER_ADMIN)
Sous `/sync-openelis` : `GET /state`, `GET /eligible/data`, `GET /runs/data`, `POST /preview/{id}` (sans persistance), `POST /refresh/{id}` (respecte l'épuisement), `POST /run-now` (lot), `POST /reset/{id}`.

---

## Test bout en bout

### 1. Démarrer oedatarepo en local (avec l'API)
```bash
cd /chemin/vers/oedatarepo
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev   # écoute sur :8085
```

### 2. Configurer LSTracker pour pointer vers oedatarepo local
Dans le `.env` de LSTracker :
```
OEDATAREPO_ENABLED=true
OEDATAREPO_URL=http://127.0.0.1:8085
OEDATAREPO_USER=<email-compte-technique-oedatarepo>
OEDATAREPO_PASSWORD=<mdp>
# Pour tester l'épuisement rapidement : OEDATAREPO_MAX_ATTEMPTS=2
```

### 3. Préparer un échantillon avec un labNumber existant côté oedatarepo
Sur la DB LSTracker, mettre `lab_number` d'un sample = un `labno` réel d'oedatarepo (idéalement un cas `RESULT_READY`).

### 4. Vérifier via la page admin
Se connecter en ADMIN → *Administration → Interopérabilité → Suivi Synchro. OpenELIS* :
- **Prévisualiser** un échantillon → vérifier le statut proposé sans qu'il soit appliqué ;
- **Rafraîchir** → vérifier que le sample passe au bon statut + dates remplies, et que l'historique s'incrémente ;
- **Lancer maintenant** → un passage `MANUAL:<login>` apparaît dans l'historique.

### 5. Vérifier le job planifié
Le job se déclenche ~1 min après le démarrage puis à l'intervalle configuré. Observer les logs :
```
Sync oedatarepo (SCHEDULED) : N échantillon(s) examinés, M mis à jour, E erreur(s)
```

---

## Troubleshooting

| Symptôme | Cause probable | Solution |
|---|---|---|
| Endpoint/page → 503 ou « désactivée » | `OEDATAREPO_ENABLED=false` ou URL/credentials vides | Configurer le `.env` et redémarrer |
| Connexion « KO » sur le panneau d'état | Compte technique / mdp invalide, oedatarepo injoignable | Tester `curl -u user:mdp -X POST <url>/auth/token` ; vérifier le réseau |
| labno toujours `NOT_FOUND` | labno absent d'OpenELIS, ou format non interrogeable (contient `/`, espace douteux…) | Normal si non encore dans OpenELIS ; vérifier la saisie du `labNumber` |
| Refresh ciblé → 409 « épuisé » | `attempts >= max-attempts` en NOT_FOUND | Cliquer **Réinitialiser** sur la ligne, puis rafraîchir |
| Statut « introuvable dans sample_status » | Référentiel `sample_status` incomplet | Vérifier que ANALYSIS_DONE/NON_CONFORM/ANALYSIS_FAILED existent |
| Le job ne tourne pas | Intégration désactivée | `OEDATAREPO_ENABLED=true` requis pour instancier le job |
