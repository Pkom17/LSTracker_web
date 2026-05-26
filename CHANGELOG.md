# Changelog — LabSampleTracker (web + backend)

## 2026-05-25 — Audit cohérence + refonte UI dashboard + rapports

### Sécurité

- **Rapports Jasper appliquent désormais l'UserScope** (correctif critique) : un utilisateur scopé région/district recevait jusqu'ici des PDF avec des données nationales si aucun filtre region/district n'était explicitement coché. `accessibleSiteIds` est désormais propagé du `ReportController` jusqu'aux 9 requêtes SQL de `ReportJdbcRepository`.

### Bugs de cohérence des chiffres (audit)

- **Filtres géographiques cassés dans les séries temporelles du dashboard** : dans `DashboardNativeRepository.tsCollected/tsDeposited/tsAnalysed/tsDelivered`, les conditions `region/district/site/lab/accessibleSiteIds` étaient accrochées au `ON` d'un `LEFT JOIN`, donc silencieusement inopérantes. Réécriture en `generate_series LEFT JOIN (subquery)` qui place les filtres dans un `WHERE` correctement scopé.
- **`SampleRepository.rawSummaryWithStatus`** : requête fausse (jointure `site.id = sample_retrieving_id` mélangeait deux clés étrangères), inutilisée. Supprimée.
- **`DashboardNativeRepository.summary` : COALESCE faux sur `at_lab`** : la métrique utilisait `COALESCE(deliver_at_lab_date, deliver_at_hub_date)` ce qui faisait que tous les samples passés par hub étaient comptés dans « Au labo », rendant `at_hub = at_lab`. Corrigé : `at_lab` filtre uniquement sur `deliver_at_lab_date`.
- **« Au hub » durci** avec `hub_id IS NOT NULL` côté dashboard et rapports — exclut les samples avec date de hub mais sans hub_id (donnée dégradée).

### Refonte de la définition des métriques (déf C « activité fenêtre »)

Toutes les surfaces (dashboard summary, dashboard funnel, rapports Jasper) utilisent désormais la même définition par métrique :

- **Total collectés** : `collection_date BETWEEN`
- **En transit** : snapshot global `status = 'ON_TRANSIT'` (non lié à la période)
- **Au hub** : `hub_id IS NOT NULL AND deliver_at_hub_date BETWEEN`
- **Au labo** : `deliver_at_lab_date BETWEEN`
- **Analysés** : `analysis_released_date BETWEEN`
- **Résultats collectés** : `result_collection_date BETWEEN`
- **Résultats livrés** : `result_delivery_date BETWEEN`
- **Non-conformités** : `status = 'NON_CONFORM' AND rejection_date BETWEEN`
- **Échecs d'analyse** : `status = 'ANALYSIS_FAILED' AND analysis_completed_date BETWEEN`
- **TAT canonique** : médiane(`result_delivery_date - collection_date`) sur les samples livrés. Sauf le widget « Labos les plus lents » qui garde `analysis_released_date - deliver_at_lab_date` (segment labo seul, justifié) mais passe en médiane.

### Rapports Jasper

- **`lab_kind` mutuellement exclusif** dans `receivedByTypeAndLabKind` :
  - RELAIS : `hub_id IS NOT NULL AND deliver_at_lab_date IS NULL`
  - DISTRICT / CAT / BM : déposé au lab final, lecture du `lab.lab_type`
  - Somme des 4 colonnes = total reçus dans la fenêtre.
- **Mise en page** : 4 rapports (conveyor, district, lab, region) — police des titres de section réduite (10 → 8 pt), zone « Observations » repositionnée au même niveau que « Nom et prénoms, Date et Signature », libellé « BM » remplacé par « Laboratoire de référence (Biologie Moléculaire) ».
- Recompilation des 6 `.jasper`.

### Dashboard web — UI

- **Refonte des KPI cards** en style « icône en bulle » (icône colorée dans une pastille à gauche, valeur saillante, couleurs respectant le contraste WCAG AA).
- **Cards réorganisées sur 2 rangées** : 7 cards de parcours en haut (auto-fit), 3 cards d'incident + performance en bas (3 colonnes égales).
- **Zone funnel supprimée** : devenue redondante avec les cards depuis l'alignement déf C.
  - Note : un bug de double-comptage avait été identifié dans le JS funnel (`at_hub + at_lab + analysed + result_collected + delivered` re-cumulait des valeurs déjà distinctes). Le funnel SQL renvoyait des bons chiffres ; c'est le JS qui les inflatait.
- **« Rejets » scindé en 2 cards** : « Non-conformités » et « Échecs d'analyse » (aligné sur le modèle mobile).
- **`delivered` du summary corrigé** : était calculé par `allCount - inTransit` (faux conceptuellement). Devient `result_on_site` directement.
- **Documentation contextuelle** : bandeaux explicatifs au-dessus de chaque zone du dashboard ; tooltips actifs (`data-tip` + CSS instantané) sur chaque carte avec la formule métier précise.

### Pages /sample, /home — UI

- Layout `/sample` : filtre avancé collapsable, table sortable côté serveur, tooltips, etc. (avant l'audit).
- Filtres dashboard alignés sur le même style que `/sample`.

### Misc

- Favicon `/report` : version font-awesome corrigée (5.1.0 → 5.15.4, version inexistante en webjars).
