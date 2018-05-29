#!/usr/bin/env bash

set -e

# Claim Store database
if [ -z "$FEATURES_DB_PASSWORD" ]; then
  echo "ERROR: Missing environment variables. Set value for 'FEATURES_DB_PASSWORD'."
  exit 1
fi

psql -v ON_ERROR_STOP=1 --username postgres --set USERNAME=featuretoggler --set PASSWORD=${FEATURES_DB_PASSWORD} <<-EOSQL
  CREATE USER :USERNAME WITH PASSWORD ':PASSWORD';
  CREATE DATABASE features
    WITH OWNER = :USERNAME
    ENCODING = 'UTF-8'
    CONNECTION LIMIT = -1;
EOSQL
