#!/usr/bin/env bash
set -euo pipefail
echo "== docker =="
docker ps
docker ps -a | head -30
docker network ls | head -20
docker volume ls | head -20
echo "== listening ports (lab) =="
lsof -nP -iTCP:8080,5173,5433,4566,10000,10001,10002,8085,4317 -sTCP:LISTEN || true
