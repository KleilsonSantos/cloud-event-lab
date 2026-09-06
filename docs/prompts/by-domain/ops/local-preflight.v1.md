# Runbook: local preflight (v1)

Before starting compose profiles or Spring Boot:

1. Run `./scripts/preflight.sh` (non-destructive).
2. If ports `8080`, `5173`, `5433`, `4566`, `10000–10002`, `8085`, `4317` are taken, stop and report — do not kill unrelated containers unless the human asks.
3. Prefer existing lab Postgres on `5433` over creating a second database container.
4. Auth local: see `.env.example` (`lab` / `lab-change-me` defaults).

SSOT: `README.md`, `deploy/compose/docker-compose.yml`, ADR-003.
