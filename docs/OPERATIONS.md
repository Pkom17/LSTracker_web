# Guide d'opérations — LSTracker Web

> Procédures à exécuter pendant la vie de l'app (post-déploiement initial). Voir `DEPLOYMENT.md` pour le déploiement initial.

## Sommaire

1. [Backups DB](#backups-db)
2. [Restauration DB](#restauration-db)
3. [Mise à jour de version](#mise-à-jour-de-version)
4. [Rotation des secrets](#rotation-des-secrets)
5. [Monitoring quotidien](#monitoring-quotidien)
6. [Gestion des logs](#gestion-des-logs)
7. [Troubleshooting](#troubleshooting)

---

## Backups DB

### Backup manuel ponctuel

```bash
cd /opt/lstracker
./scripts/backup-db.sh prod
# → /opt/lstracker/backups/prod/lstracker-prod-YYYYMMDD-HHMMSS.sql.gz
```

### Backup automatique (cron)

```bash
crontab -e
```

Ajouter :

```cron
# DB PROD chaque jour à 03h00 (heure serveur)
0 3 * * * /opt/lstracker/scripts/backup-db.sh prod >> /opt/lstracker/logs/backup.log 2>&1

# DB DEMO chaque dimanche à 04h00 (moins critique)
0 4 * * 0 /opt/lstracker/scripts/backup-db.sh demo >> /opt/lstracker/logs/backup.log 2>&1
```

Vérifier que le cron tourne :

```bash
tail -f /opt/lstracker/logs/backup.log
```

### Rétention

Par défaut : **30 derniers backups** par env (modifiable via `RETENTION_DAYS=N`).

```bash
ls -lh /opt/lstracker/backups/prod/ | head -5
du -sh /opt/lstracker/backups/    # taille totale
```

### Sauvegarde off-site (recommandé)

Les backups locaux ne protègent **pas** d'un crash disque serveur. Mirror vers un stockage externe :

```bash
# Exemple : rsync vers un autre serveur ou NAS
# Ajouter au cron (après le backup quotidien) :
30 3 * * * rsync -avz /opt/lstracker/backups/ backup-user@nas:/backups/lstracker/
```

Ou rclone vers S3/Azure Blob/Google Drive :

```bash
# Voir https://rclone.org pour la configuration initiale
30 3 * * * rclone sync /opt/lstracker/backups/ remote:lstracker-backups
```

---

## Restauration DB

### Depuis un backup

```bash
cd /opt/lstracker

# Lister les backups disponibles
ls -lh backups/prod/

# Restaurer (demande confirmation)
./scripts/restore-db.sh prod backups/prod/lstracker-prod-20250612-030000.sql.gz

# Redémarrer l'app pour vider les caches éventuels
docker compose --env-file .env.prod -f docker-compose.prod.yml restart tracker_app
```

### Tester la restauration sur DEMO d'abord

**Bonne pratique** : régulièrement (1×/mois), restaurer un backup PROD dans la DEMO pour valider que les backups sont utilisables.

```bash
# Copier le backup prod vers le dossier demo
cp backups/prod/lstracker-prod-LATEST.sql.gz backups/demo/

# Restaurer dans la demo (ne touche pas la prod)
./scripts/restore-db.sh demo backups/demo/lstracker-prod-LATEST.sql.gz

# Vérifier sur l'UI demo que les données sont là
curl -fsS http://localhost:9201/actuator/health
```

---

## Mise à jour de version

### Workflow standard

1. **Sur ton poste** : tu push un tag `v*` sur ITECH-CI → GitHub Actions build et push l'image vers ghcr.io.

   ```bash
   cd ~/dev/dnoApp/lstracker_web
   git tag -a v2.3.0 -m "Release v2.3.0 — features X, Y"
   git push origin v2.3.0
   ```

   Vérifier sur https://github.com/ITECH-CI/LSTracker_web/actions que le workflow réussit.

2. **Sur le serveur DEMO d'abord** :

   ```bash
   cd /opt/lstracker
   sed -i 's|^APP_IMAGE=.*|APP_IMAGE=ghcr.io/itech-ci/labsampletracker:2.3.0|' .env.demo
   docker compose --env-file .env.demo -f docker-compose.demo.yml pull
   docker compose --env-file .env.demo -f docker-compose.demo.yml up -d tracker_app

   # Suivre logs
   docker logs lst_demo_app -f --tail 50
   ```

3. **Tester sur DEMO** (24-72h selon la criticité du changement).

4. **Si DEMO OK → PROD** :

   ```bash
   # Backup PRÉ-upgrade obligatoire
   ./scripts/backup-db.sh prod
   ls -lh backups/prod/ | head -3   # confirmer présence

   # Bump version PROD
   sed -i 's|^APP_IMAGE=.*|APP_IMAGE=ghcr.io/itech-ci/labsampletracker:2.3.0|' .env.prod
   docker compose --env-file .env.prod -f docker-compose.prod.yml pull
   docker compose --env-file .env.prod -f docker-compose.prod.yml up -d tracker_app

   # Vérifier
   docker logs lst_prod_app -f --tail 100
   curl -fsS http://localhost:9200/actuator/health
   ```

### Si la mise à jour échoue → rollback

Cf. `DEPLOYMENT.md` section [Rollback](DEPLOYMENT.md#rollback).

---

## Rotation des secrets

À faire au moins **1 fois par an**, ou immédiatement si une fuite est suspectée.

```bash
cd /opt/lstracker

# Rotation DEMO (tester d'abord)
./scripts/rotate-secrets.sh demo

# Vérifier que la demo fonctionne (login + actions)
curl -fsS http://localhost:9201/actuator/health

# Rotation PROD (les utilisateurs seront déconnectés)
./scripts/rotate-secrets.sh prod

# Le script gère: backup .env, ALTER USER en BD, update .env, restart app, healthcheck.
# En cas d'échec, rollback automatique.
```

Voir `scripts/rotate-secrets.sh` pour le détail.

---

## Monitoring quotidien

### Statut rapide (à mettre en alias)

```bash
# ~/.bashrc
alias lst-status='docker ps --filter "name=lst_" --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"'
alias lst-health-prod='curl -fsS http://localhost:9200/actuator/health'
alias lst-health-demo='curl -fsS http://localhost:9201/actuator/health'
alias lst-logs-prod='docker logs lst_prod_app -f --tail 50'
alias lst-stats='docker stats --no-stream lst_prod_app lst_prod_db lst_demo_app lst_demo_db'
```

### Métriques clés à surveiller

```bash
# Resource usage
docker stats --no-stream
# Si lst_prod_app dépasse 90% mémoire en continu → fuite ou sous-dimensionné

# Disk usage
df -h /opt/lstracker        # volumes Docker, backups, logs
du -sh /opt/lstracker/backups/   # total backups

# Erreurs récentes
docker logs lst_prod_app --since 1h 2>&1 | grep -iE 'error|exception|fatal' | tail -20
```

### Monitoring externe (uptime)

Recommandé : configurer un monitor externe sur `http://<ip-publique>:9200/actuator/health`. Options gratuites/freemium :

- [UptimeRobot](https://uptimerobot.com) — 50 monitors gratuits, check toutes les 5 min
- [Healthchecks.io](https://healthchecks.io) — orienté cron, gratuit jusqu'à 20 checks
- [Better Uptime](https://betteruptime.com) — UI moderne, gratuit jusqu'à 10 monitors

Alerter via email / Slack / SMS si DOWN > 2 minutes.

---

## Gestion des logs

### Logs applicatifs

Les containers utilisent un driver `json-file` avec rotation automatique :
- DEMO : 20 MB max × 3 fichiers
- PROD : 100 MB max × 10 fichiers

Total max sur disque : ~1 GB pour PROD, ~60 MB pour DEMO.

```bash
# Voir les logs depuis X heures
docker logs lst_prod_app --since 24h

# Suivre en temps réel
docker logs lst_prod_app -f --tail 50

# Exporter pour analyse
docker logs lst_prod_app --since 24h > /tmp/lst-prod-$(date +%Y%m%d).log
```

### Logs fichiers (montés via volume)

L'app écrit aussi dans `./logs/<env>/tracker-app.log` (rotation gérée par Logback).

```bash
ls -lh /opt/lstracker/logs/prod/
tail -f /opt/lstracker/logs/prod/tracker-app.log
```

### Purge si saturation disque

```bash
# Forcer la rotation des logs Docker (libère espace)
docker exec lst_prod_app sh -c 'find /app/logs -name "*.log.*" -mtime +7 -delete'

# Si le disque est plein à cause des backups, nettoyer les plus anciens
find /opt/lstracker/backups -name "*.sql.gz" -mtime +60 -delete
```

---

## Troubleshooting

### L'app ne démarre pas

```bash
docker logs lst_prod_app --tail 100

# Erreurs courantes :
# 1. "Failed to determine a suitable driver class" → SPRING_DATASOURCE_URL mal formé
# 2. "Connection refused" → tracker_db pas encore healthy (attendre 30-60s)
# 3. "FATAL: password authentication failed" → POSTGRES_PASSWORD désaligné DB/env
# 4. "JwtException: Invalid key" → JWT_SECRET manquant (vérifier .env.prod)
```

Solution générique :

```bash
# Voir la config envoyée au container
docker exec lst_prod_app env | grep -E 'SPRING_|JWT_|POSTGRES_|JAVA_'

# Vérifier la santé DB en isolation
docker exec lst_prod_db pg_isready -U $POSTGRES_USER -d $POSTGRES_DB
```

### L'app crashe en boucle (restart loop)

```bash
docker ps -a --filter "name=lst_prod_app"
# Voir le STATUS — si "Restarting (1) X seconds ago" → crash boot

# Capture les derniers logs avant crash
docker logs lst_prod_app --tail 200 > /tmp/crash.log
docker compose --env-file .env.prod -f docker-compose.prod.yml stop tracker_app
# Investiguer crash.log avant redémarrer
```

### Postgres refuse les connexions

```bash
# Container UP ?
docker ps --filter "name=lst_prod_db"

# Logs Postgres
docker logs lst_prod_db --tail 100

# Tester depuis l'host
docker exec -it lst_prod_db psql -U $POSTGRES_USER -d $POSTGRES_DB -c "\l"

# Si "FATAL: the database system is starting up" → attendre
# Si "FATAL: password authentication failed" → mismatch env/DB, cf. rotation manuelle
```

### Performance dégradée

```bash
# Top requêtes SQL lentes
docker exec lst_prod_db psql -U $POSTGRES_USER -d $POSTGRES_DB -c "
  SELECT pid, now() - query_start AS duration, state, query
  FROM pg_stat_activity
  WHERE state != 'idle' AND query NOT ILIKE '%pg_stat%'
  ORDER BY duration DESC LIMIT 10;"

# Stats heap JVM (besoin actuator/metrics — si exposé)
# Sinon dump heap manuel :
docker exec lst_prod_app jcmd 1 GC.heap_info
```

### Container hors mémoire (OOMKilled)

```bash
docker ps -a --filter "name=lst_prod_app" --format "{{.Status}}"
# Si "Exited (137)" → OOMKill

# Augmenter la limite dans docker-compose.prod.yml :
#   deploy.resources.limits.memory: 22G  (au lieu de 18G)
# Puis :
docker compose --env-file .env.prod -f docker-compose.prod.yml up -d tracker_app
```

### Disque plein

```bash
df -h /
# Identifier le coupable
du -sh /var/lib/docker /opt/lstracker/* | sort -h

# Nettoyage Docker (images orphelines, containers stoppés)
docker system prune -af --volumes
# ⚠️ --volumes supprime aussi les volumes non utilisés — vérifier d'abord :
docker volume ls -f dangling=true
```
