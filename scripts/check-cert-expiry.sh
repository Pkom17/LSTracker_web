#!/usr/bin/env bash
# =====================================================================
# check-cert-expiry.sh — alerte si un cert SSL expire bientôt
#
# Usage:
#   ./scripts/check-cert-expiry.sh
#
# Configuration via env vars (optionnel):
#   THRESHOLD_DAYS=30        # alerte si expiration < N jours
#   ALERT_EMAIL=ops@example  # destinataire mail (vide = pas d'email, juste exit code)
#
# À planifier:
#   0 6 * * * /opt/lstracker/scripts/check-cert-expiry.sh
# =====================================================================

set -euo pipefail

THRESHOLD_DAYS="${THRESHOLD_DAYS:-30}"
ALERT_EMAIL="${ALERT_EMAIL:-}"

# Seul le cert wildcard itech-civ est local (le cert lstracker.org est géré par
# le fournisseur de domaine via CDN externe — pas de fichier sur ce serveur).
# Override possible via env: CERTS="/path/to/cert1.pem /path/to/cert2.pem"
DEFAULT_CERTS=(
  "/home/itech/ssl/itech-civ.org/fullchain.pem"
)
if [[ -n "${CERTS_OVERRIDE:-}" ]]; then
  read -r -a CERTS <<< "$CERTS_OVERRIDE"
else
  CERTS=("${DEFAULT_CERTS[@]}")
fi

NOW_EPOCH=$(date +%s)
WARNINGS=()

for cert in "${CERTS[@]}"; do
  if [[ ! -f "$cert" ]]; then
    WARNINGS+=("MISSING: $cert not found")
    continue
  fi

  END_DATE=$(openssl x509 -noout -enddate -in "$cert" | cut -d= -f2)
  END_EPOCH=$(date -d "$END_DATE" +%s 2>/dev/null || date -j -f "%b %d %T %Y %Z" "$END_DATE" +%s 2>/dev/null)

  if [[ -z "$END_EPOCH" ]]; then
    WARNINGS+=("PARSE ERROR: $cert (date: $END_DATE)")
    continue
  fi

  DAYS_LEFT=$(( (END_EPOCH - NOW_EPOCH) / 86400 ))

  if (( DAYS_LEFT < 0 )); then
    WARNINGS+=("EXPIRED ($((-DAYS_LEFT)) days ago): $cert")
  elif (( DAYS_LEFT < THRESHOLD_DAYS )); then
    WARNINGS+=("EXPIRES SOON (in $DAYS_LEFT days): $cert")
  fi
done

if (( ${#WARNINGS[@]} == 0 )); then
  echo "[$(date -Iseconds)] All certs OK (threshold ${THRESHOLD_DAYS}d)"
  exit 0
fi

# Print warnings
echo "[$(date -Iseconds)] CERT WARNINGS:"
printf '  - %s\n' "${WARNINGS[@]}"

# Send email if configured
if [[ -n "$ALERT_EMAIL" ]] && command -v mail >/dev/null; then
  {
    echo "LSTracker — SSL certificate expiry warning"
    echo "Threshold: ${THRESHOLD_DAYS} days"
    echo ""
    printf '%s\n' "${WARNINGS[@]}"
    echo ""
    echo "Host: $(hostname)"
    echo "Time: $(date -Iseconds)"
  } | mail -s "[LSTracker] SSL cert expiry alert" "$ALERT_EMAIL"
fi

exit 1
