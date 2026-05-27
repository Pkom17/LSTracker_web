# Guide d'opérations — LSTracker Web

> Procédures à exécuter pendant la vie de l'app (post-déploiement). Voir [DEPLOYMENT.md](DEPLOYMENT.md) pour le déploiement initial et [NGINX.md](NGINX.md) pour le reverse proxy.

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

Les scripts sont dans le dossier `scripts/` du bundle extrait. Les chemins ci-dessous supposent que le bundle est extrait dans `/opt/lstracker/lstracker-deploy-<version>/`.

### Backup manuel

```bash
cd /opt/lstracker/lstracker-deploy-2.2.0
./scripts/backup-db.sh prod
# → /opt/lstracker/backups/prod/lstracker-prod-YYYYMMDD-HHMMSS.sql.gz
```

### Backup automatique (cron)

```bash
sudo mkdir -p /opt/lstracker/backups/{demo,prod} /opt/lstracker/logs
crontab -e
```

Ajouter :

```cron
# DB PROD chaque jour à 03h00
0 3 * * * /opt/lstracker/lstracker-deploy-2.2.0/scripts/backup-db.sh prod >> /opt/lstracker/logs/backup.log 2>&1

# DB DEMO chaque dimanche à 04h00
0 4 * * 0 /opt/lstracker/lstracker-deploy-2.2.0/scripts/backup-db.sh demo >> /opt/lstracker/logs/backup.log 2>&1
```

> Quand tu mettras à jour vers une nouvelle version, les scripts sont identiques d'une release à l'autre — tu peux soit pointer le cron vers le nouveau dossier, soit garder l'ancien (les scripts ne dépendent pas du code applicatif).

Vérifier :

```bash
tail -f /opt/lstracker/logs/backup.log
ls -lh /opt/lstracker/backups/prod/
```

### Rétention

Par défaut : **30 derniers backups** par env. Modifiable via env var `RETENTION_DAYS=N`.

### Sauvegarde off-site (recommandé)

Les backups locaux ne protègent pas d'un crash disque. Mirror externe via cron :

```cron
# rsync vers NAS/autre serveur
30 3 * * * rsync -avz /opt/lstracker/backups/ backup-user@nas:/backups/lstracker/
```

Ou rclone vers S3/Azure/Google Drive :

```cron
30 3 * * * rclone sync /opt/lstracker/backups/ remote:lstracker-backups
```

---

## Restauration DB

### Depuis un backup local

```bash
cd /opt/lstracker/lstracker-deploy-2.2.0

# Lister les backups
ls -lh /opt/lstracker/backups/prod/

# Restaurer (demande confirmation interactive)
./scripts/restore-db.sh prod /opt/lstracker/backups/prod/lstracker-prod-20260520-030000.sql.gz

# Redémarrer l'app pour vider les caches éventuels
docker compose --env-file .env.prod -f docker-compose.prod.yml restart tracker_app
```

### Tester la restauration sur DEMO d'abord

Bonne pratique : 1×/mois, restaurer un backup PROD dans la DEMO pour valider que les backups sont utilisables.

```bash
cp /opt/lstracker/backups/prod/lstracker-prod-LATEST.sql.gz \
   /opt/lstracker/backups/demo/

./scripts/restore-db.sh demo \
   /opt/lstracker/backups/demo/lstracker-prod-LATEST.sql.gz
```

---

## Mise à jour de version

Le workflow tire profit du bundle de release : pas de modification manuelle des composes, juste télécharger le nouveau bundle et basculer.

### 1. Build et publication (toi, depuis ton poste de dev)

```bash
cd ~/dev/dnoApp/lstracker_web
git tag -a v2.3.0 -m "Release v2.3.0 — features X, Y"
git push origin v2.3.0
```

GitHub Actions build l'image **et** génère le bundle. Suivre sur https://github.com/ITECH-CI/LSTracker_web/actions.

### 2. Récupération du bundle (sur le serveur)

```bash
NEW_VERSION=2.3.0
URL=https://github.com/ITECH-CI/LSTracker_web/releases/download/v${NEW_VERSION}

cd /opt/lstracker
curl -fsSLO ${URL}/lstracker-deploy-${NEW_VERSION}.tar.gz
curl -fsSLO ${URL}/lstracker-deploy-${NEW_VERSION}.tar.gz.sha256
sha256sum -c lstracker-deploy-${NEW_VERSION}.tar.gz.sha256
tar -xzf lstracker-deploy-${NEW_VERSION}.tar.gz
```

### 3. Reprendre les `.env` de la version précédente

```bash
# Copier les .env existants dans le nouveau dossier
OLD_VERSION=2.2.0
cp /opt/lstracker/lstracker-deploy-${OLD_VERSION}/.env.{demo,prod} \
   /opt/lstracker/lstracker-deploy-${NEW_VERSION}/

chmod 600 /opt/lstracker/lstracker-deploy-${NEW_VERSION}/.env.*
```

### 4. Tester sur DEMO d'abord

```bash
cd /opt/lstracker/lstracker-deploy-${NEW_VERSION}
docker compose --env-file .env.demo -f docker-compose.demo.yml up -d
docker logs lst_demo_app -f --tail 50
# Attendre "Started LabSampleTrackerApplication"

curl -fsS http://127.0.0.1:9201/actuator/health
```

Tester pendant 24-72h selon la criticité du changement.

### 5. Si DEMO OK → appliquer la PROD

```bash
# Backup PRÉ-upgrade obligatoire
./scripts/backup-db.sh prod
ls -lh /opt/lstracker/backups/prod/ | head -3

# Bascule
docker compose --env-file .env.prod -f docker-compose.prod.yml up -d
docker logs lst_prod_app -f --tail 100
curl -fsS http://127.0.0.1:9200/actuator/health
```

### 6. Nettoyage (après stabilité confirmée)

```bash
# Supprimer l'ancien dossier après ~1 semaine sans incident
rm -rf /opt/lstracker/lstracker-deploy-${OLD_VERSION}
rm -f  /opt/lstracker/lstracker-deploy-${OLD_VERSION}.tar.gz*
```

> Tip : si tu veux pouvoir rollback en 30 secondes, **garde au moins l'ancien dossier**. Tu peux toujours faire `cd lstracker-deploy-${OLD_VERSION} && docker compose --env-file .env.prod -f docker-compose.prod.yml up -d`.

---

## Rotation des secrets

À faire au moins **1 fois par an**, ou immédiatement si une fuite est suspectée.

Les scripts sont dans le bundle :

```bash
cd /opt/lstracker/lstracker-deploy-2.2.0

# Rotation DEMO (tester d'abord)
./scripts/rotate-secrets.sh demo

# Vérifier que la demo fonctionne
curl -fsS http://127.0.0.1:9201/actuator/health

# Rotation PROD (les utilisateurs seront déconnectés)
./scripts/rotate-secrets.sh prod
```

Le script gère : backup `.env`, `ALTER USER` en BD, update `.env`, restart app, healthcheck. Rollback automatique en cas d'échec.

---

## Monitoring quotidien

### Aliases pratiques (à mettre dans `~/.bashrc`)

```bash
alias lst-status='docker ps --filter "name=lst_" --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"'
alias lst-health-prod='curl -fsS http://127.0.0.1:9200/actuator/health'
alias lst-health-demo='curl -fsS http://127.0.0.1:9201/actuator/health'
alias lst-logs-prod='docker logs lst_prod_app -f --tail 50'
alias lst-logs-demo='docker logs lst_demo_app -f --tail 50'
alias lst-stats='docker stats --no-stream lst_prod_app lst_prod_db lst_demo_app lst_demo_db'
```

### Métriques clés

```bash
# Resource usage
docker stats --no-stream
# Si lst_prod_app dépasse 90% mémoire en continu → fuite ou sous-dimensionné

# Disk usage
df -h /
du -sh /opt/lstracker/backups/   # total backups
du -sh /var/lib/docker            # volumes Docker

# Erreurs récentes
docker logs lst_prod_app --since 1h 2>&1 | grep -iE 'error|exception|fatal' | tail -20
```

### Monitoring externe (uptime)

Recommandé : configurer un monitor sur `https://lstracker.org/`. Options gratuites :

- [UptimeRobot](https://uptimerobot.com) — 50 monitors gratuits, check 5 min
- [Healthchecks.io](https://healthchecks.io) — orienté cron, gratuit
- [Better Uptime](https://betteruptime.com) — UI moderne, 10 monitors gratuits

Alerte si DOWN > 2 minutes via email/Slack/SMS.

### Cert SSL demo (alerte expiration)

```bash
sudo crontab -e
```

```cron
# Alerte si cert wildcard *.itech-civ.org expire < 30 jours
0 6 * * * /opt/lstracker/lstracker-deploy-2.2.0/scripts/check-cert-expiry.sh
```

---

## Gestion des logs

### Logs Docker

Rotation auto via driver `json-file` :
- DEMO : 20 MB max × 3 fichiers
- PROD : 100 MB max × 10 fichiers

```bash
# Voir les logs récents
docker logs lst_prod_app --since 24h | less
docker logs lst_prod_app -f --tail 50

# Export pour analyse
docker logs lst_prod_app --since 24h > /tmp/lst-prod-$(date +%Y%m%d).log
```

### Logs nginx (si en place)

```bash
sudo tail -f /var/log/nginx/lstracker-prod.access.log
sudo tail -f /var/log/nginx/lstracker-prod.error.log
sudo tail -f /var/log/nginx/lstracker-demo.access.log
```

### Logs applicatifs fichier

L'app écrit aussi dans `./logs/<env>/tracker-app.log` (rotation Logback).

```bash
ls -lh /opt/lstracker/lstracker-deploy-2.2.0/logs/prod/
```

---

## Troubleshooting

### L'app ne démarre pas

```bash
docker logs lst_prod_app --tail 100
```

Erreurs courantes :

| Symptôme | Cause | Solution |
|---|---|---|
| `Failed to determine a suitable driver class` | `SPRING_DATASOURCE_URL` mal formé | Vérifier `.env.prod` |
| `Connection refused` | DB pas encore healthy | Attendre 30-60s, sinon vérifier `docker logs lst_prod_db` |
| `FATAL: password authentication failed` | `POSTGRES_PASSWORD` désaligné entre `.env` et la DB | Voir section [rotation manuelle](#rotation-manuelle-du-password-bd) |
| `JwtException: Invalid key` | `JWT_SECRET` vide | Vérifier `.env.prod` |

Diagnostic générique :

```bash
docker exec lst_prod_app env | grep -E 'SPRING_|JWT_|POSTGRES_|JAVA_'
docker exec lst_prod_db pg_isready -U $POSTGRES_USER -d $POSTGRES_DB
```

### App crashe en boucle (restart loop)

```bash
docker ps -a --filter "name=lst_prod_app"
# STATUS "Restarting (1) X seconds ago" → crash boot

docker logs lst_prod_app --tail 200 > /tmp/crash.log
docker compose --env-file .env.prod -f docker-compose.prod.yml stop tracker_app
# Investiguer crash.log avant redémarrer
```

### Postgres refuse les connexions

```bash
docker ps --filter "name=lst_prod_db"
docker logs lst_prod_db --tail 100
docker exec -it lst_prod_db psql -U $POSTGRES_USER -d $POSTGRES_DB -c "\l"
```

### Container OOMKilled

```bash
docker ps -a --filter "name=lst_prod_app" --format "{{.Status}}"
# "Exited (137)" → OOMKill

# Augmenter la limite dans docker-compose.prod.yml :
#   deploy.resources.limits.memory: 22G  (au lieu de 18G)
# Reload :
docker compose --env-file .env.prod -f docker-compose.prod.yml up -d tracker_app
```

### Disque plein

```bash
df -h /
du -sh /var/lib/docker /opt/lstracker/* | sort -h

# Nettoyage Docker
docker system prune -af
# Si besoin, aussi les volumes orphelins (CHECK avant !) :
docker volume ls -f dangling=true
docker volume prune
```

### Performance dégradée

```bash
# Top requêtes SQL lentes
docker exec lst_prod_db psql -U $POSTGRES_USER -d $POSTGRES_DB -c "
  SELECT pid, now() - query_start AS duration, state, query
  FROM pg_stat_activity
  WHERE state != 'idle' AND query NOT ILIKE '%pg_stat%'
  ORDER BY duration DESC LIMIT 10;"

# JVM heap info
docker exec lst_prod_app jcmd 1 GC.heap_info
```

### Rotation manuelle du password BD

Si jamais le script `rotate-secrets.sh` a échoué et laissé un désalignement :

```bash
# Récupérer le password actuel du .env
NEW_PWD=$(grep '^POSTGRES_PASSWORD=' .env.prod | cut -d= -f2-)

# Trouver l'ancien (depuis un backup .env.prod.backup.*)
OLD_PWD=$(grep '^POSTGRES_PASSWORD=' .env.prod.backup.YYYYMMDD | cut -d= -f2-)

# Re-aligner Postgres avec le .env
docker exec -e PGPASSWORD="$OLD_PWD" lst_prod_db \
  psql -U $POSTGRES_USER -d $POSTGRES_DB \
  -c "ALTER USER \"$POSTGRES_USER\" WITH PASSWORD '$NEW_PWD';"

# Redémarrer l'app
docker compose --env-file .env.prod -f docker-compose.prod.yml up -d tracker_app
```
