#!/usr/bin/env bash
set -euo pipefail

DATE_TAG=$(date +"%Y%m%d_%H%M%S")
BACKUP_DIR=${BACKUP_DIR:-"./backups"}
DB_HOST=${DB_HOST:-"localhost"}
DB_PORT=${DB_PORT:-"3306"}
DB_NAME=${DB_NAME:-"gestion_activos"}
DB_USER=${DB_USER:-"root"}
DB_PASSWORD=${DB_PASSWORD:-"admin123"}

mkdir -p "$BACKUP_DIR"
OUTPUT_FILE="$BACKUP_DIR/${DB_NAME}_${DATE_TAG}.sql"

mysqldump -h "$DB_HOST" -P "$DB_PORT" -u "$DB_USER" -p"$DB_PASSWORD" "$DB_NAME" > "$OUTPUT_FILE"
gzip -f "$OUTPUT_FILE"

echo "Backup generado: ${OUTPUT_FILE}.gz"
