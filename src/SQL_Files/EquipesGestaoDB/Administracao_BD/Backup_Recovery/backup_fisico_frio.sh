#!/bin/bash

# Cold backup fisico
# Para o listener, encerra a BD, copia ficheiros e reinicia tudo

set -e

DB_NAME="EquipesGestaoDB"

DATA=$(date +%Y%m%d_%H%M%S)
BACKUP_DIR="$HOME/backups_oracle/$DB_NAME/cold_backup"
BACKUP_FILE="$BACKUP_DIR/${DB_NAME}_cold_$DATA.tar.gz"

mkdir -p "$BACKUP_DIR"

reiniciar_oracle() {
    su -s /bin/bash oracle -c "printf 'startup;\nexit;\n' | sqlplus / as sysdba" || true
    su -s /bin/bash oracle -c "lsnrctl start" || true
}

trap reiniciar_oracle EXIT

su -s /bin/bash oracle -c "lsnrctl stop" || true

su -s /bin/bash oracle -c "printf 'shutdown immediate;\nexit;\n' | sqlplus / as sysdba"

ORACLE_BASE_PATH="${ORACLE_BASE:-/usr/lib/oracle/xe/app/oracle}"
ORACLE_HOME_PATH="${ORACLE_HOME:-/usr/lib/oracle/xe/app/oracle/product/10.2.0/server}"

PATHS_TO_BACKUP=(
    "$ORACLE_BASE_PATH/oradata"
    "/usr/lib/oracle/xe/oradata"
    "$ORACLE_BASE_PATH/admin"
    "$ORACLE_HOME_PATH/dbs"
    "/etc/oratab"
)

EXISTING_PATHS=()

for path in "${PATHS_TO_BACKUP[@]}"; do
    if [ -e "$path" ]; then
        EXISTING_PATHS+=("$path")
    fi
done

if [ ${#EXISTING_PATHS[@]} -eq 0 ]; then
    echo "Nenhum caminho de Oracle encontrado para backup."
    exit 1
fi

tar -czvf "$BACKUP_FILE" "${EXISTING_PATHS[@]}"

trap - EXIT
reiniciar_oracle

echo "Cold backup terminado em: $BACKUP_FILE"
