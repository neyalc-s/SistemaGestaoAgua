#!/bin/bash

# Restore e recovery com RMAN
# Uso: ./restore_rman_backup.sh /caminho/completo/do/backup.bkp

set -e

if [ -z "$1" ]; then
    echo "Uso: ./restore_rman_backup.sh /caminho/completo/do/backup.bkp"
    exit 1
fi

BACKUP_FILE="$1"

if [[ "$BACKUP_FILE" != /* ]]; then
    echo "Indique o caminho completo do ficheiro de backup."
    echo "Exemplo: ./restore_rman_backup.sh /home/oracle/backups/backup_hot_abc.bkp"
    exit 1
fi

if [ ! -f "$BACKUP_FILE" ]; then
    echo "Ficheiro nao encontrado: $BACKUP_FILE"
    exit 1
fi

BACKUP_DIR=$(dirname "$BACKUP_FILE")
RMAN_FILE="/tmp/rman_restore_$$.rcv"

cat > "$RMAN_FILE" <<EOF
STARTUP MOUNT FORCE;
CATALOG START WITH '$BACKUP_DIR/' NOPROMPT;
RESTORE DATABASE;
RECOVER DATABASE;
ALTER DATABASE OPEN;
EXIT;
EOF

su -s /bin/bash oracle -c "lsnrctl stop" || true

su -s /bin/bash oracle -c "rman target / @$RMAN_FILE"

rm -f "$RMAN_FILE"

su -s /bin/bash oracle -c "lsnrctl start" || true

echo "Restore e recovery RMAN terminado."
