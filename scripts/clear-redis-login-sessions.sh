#!/usr/bin/env bash
set -euo pipefail

REDIS_HOST="${REDIS_HOST:-127.0.0.1}"
REDIS_PORT="${REDIS_PORT:-6379}"
TOKEN_PREFIX="${TOKEN_PREFIX:-login:token}"

keys=$(redis-cli -h "$REDIS_HOST" -p "$REDIS_PORT" --scan --pattern "${TOKEN_PREFIX}*")

if [ -z "$keys" ]; then
  echo "No login session keys found for prefix ${TOKEN_PREFIX}"
  exit 0
fi

echo "$keys" | while IFS= read -r key; do
  if [ -n "$key" ]; then
    redis-cli -h "$REDIS_HOST" -p "$REDIS_PORT" del "$key" >/dev/null
    echo "Deleted $key"
  fi
done
