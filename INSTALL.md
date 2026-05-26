# LSTracker — Installation rapide

> Si tu lis ce fichier, tu as extrait le bundle `lstracker-deploy-v<version>.tar.gz` quelque part sur un serveur Linux. Suis les étapes ci-dessous pour démarrer la DEMO ou la PROD.

## Prérequis serveur

- Linux x86_64 (Ubuntu 22.04+ recommandé)
- Docker Engine 24+ et Docker Compose plugin v2
- Accès internet (pour pull l'image depuis ghcr.io/itech-ci/)
- 8 GiB RAM minimum pour la demo seule, 96 GiB pour demo + prod

Vérifier :

```bash
docker --version          # Docker version 24+
docker compose version    # v2.x+
```

Si Docker n'est pas installé :

```bash
curl -fsSL https://get.docker.com | sudo sh
sudo usermod -aG docker $USER
newgrp docker
```

## Déployer la DEMO

```bash
# 1. Préparer .env.demo
cp .env.demo.example .env.demo

# Générer un mot de passe DB fort et un secret JWT
echo "POSTGRES_PASSWORD=$(openssl rand -base64 48 | tr -dc 'A-Za-z0-9' | head -c 32)"
echo "JWT_SECRET=$(openssl rand -hex 64)"

# Éditer .env.demo et remplacer toutes les valeurs CHANGE_ME_*
nano .env.demo

# Vérifier qu'il n'y a plus aucun CHANGE_ME
grep CHANGE_ME .env.demo && echo "ENCORE DU CHANGE_ME — corriger" || echo "OK"

# Protéger le fichier
chmod 600 .env.demo

# 2. Démarrer (l'image est pull automatiquement)
docker compose --env-file .env.demo -f docker-compose.demo.yml up -d

# 3. Vérifier
docker ps --filter "name=lst_demo_"
docker logs lst_demo_app -f --tail 50
# Attendre "Started LabSampleTrackerApplication" puis Ctrl+C

curl -fsS http://127.0.0.1:9201/actuator/health
# {"status":"UP"}
```

L'app demo écoute sur `127.0.0.1:9201` (loopback uniquement). Pour y accéder publiquement, configurer nginx en reverse proxy (voir [docs/NGINX.md](docs/NGINX.md)).

**Accès temporaire sans nginx :** utiliser l'override `docker-compose.demo.public.yml` qui expose le port publiquement :

```bash
docker compose --env-file .env.demo \
               -f docker-compose.demo.yml \
               -f docker-compose.demo.public.yml \
               up -d

# Puis : http://<ip-serveur>:9201
```

⚠️ L'override `public.yml` est destiné à la **phase de validation uniquement**. Pour exposer la demo en production sur internet, utiliser nginx + HTTPS (voir docs/NGINX.md).

## Déployer la PROD

Voir [docs/DEPLOYMENT.md](docs/DEPLOYMENT.md) pour la procédure complète (validation, migration, backups).

Résumé express :

```bash
cp .env.prod.example .env.prod
nano .env.prod   # secrets DIFFÉRENTS de la demo
chmod 600 .env.prod

docker compose --env-file .env.prod -f docker-compose.prod.yml up -d
```

## Documentation complète

| Fichier | Contenu |
|---|---|
| [docs/DEPLOYMENT.md](docs/DEPLOYMENT.md) | Runbook complet (provisioning → DEMO → validation → PROD) |
| [docs/NGINX.md](docs/NGINX.md) | Reverse proxy nginx (vhosts, certs, migration apache2) |
| [docs/OPERATIONS.md](docs/OPERATIONS.md) | Backups, restauration, rotation secrets, monitoring, troubleshooting |
| [scripts/](scripts/) | Scripts d'automatisation à utiliser après l'installation |
