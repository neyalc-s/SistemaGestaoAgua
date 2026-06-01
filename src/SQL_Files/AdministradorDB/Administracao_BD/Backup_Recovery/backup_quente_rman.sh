#!/bin/bash

# Hot backup com RMAN
# Usar apenas se a BD estiver em ARCHIVELOG

set -e

DB_NAME="AdministradorDB"

DATA=$(date +%Y%m%d_%H%M%S)
BACKUP_DIR="$HOME/backups_oracle/$DB_NAME/hot_backup_rman"
RMAN_FILE="/tmp/rman_hot_${DB_NAME}_$$.rcv"

mkdir -p "$BACKUP_DIR"

LOG_MODE=$(su -s /bin/bash oracle -c "printf 'set heading off feedback off;\nselect log_mode from v\$database;\nexit;\n' | sqlplus -s / as sysdba" | tr -d '[:space:]')

if [ "$LOG_MODE" != "ARCHIVELOG" ]; then
    echo "A BD nao esta em ARCHIVELOG."
    echo "Hot backup nao sera executado."
    echo "Use o cold backup."
    exit 1
fi

cat > "$RMAN_FILE" <<EOF
BACKUP DATABASE FORMAT '$BACKUP_DIR/${DB_NAME}_hot_%U.bkp';
BACKUP CURRENT CONTROLFILE FORMAT '$BACKUP_DIR/${DB_NAME}_control_%U.bkp';
EXIT;
EOF

su -s /bin/bash oracle -c "rman target / @$RMAN_FILE"

rm -f "$RMAN_FILE"

echo "Hot backup RMAN terminado em: $BACKUP_DIR"
