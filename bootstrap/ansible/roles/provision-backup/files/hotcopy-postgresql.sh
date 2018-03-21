#!/bin/sh

USER=postgres
COMMAND="pg_dumpall --clean --column-inserts"
TARGET_DIR=/var/backup.d/preliminary
TARGET_FILE=pg_dumpall.sql

echo "creating PostgreSQL backup"
sudo -u postgres ${COMMAND} > ${TARGET_DIR}/${TARGET_FILE}