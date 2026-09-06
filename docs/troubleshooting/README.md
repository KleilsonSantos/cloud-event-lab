# Troubleshooting

## Port conflicts

```bash
lsof -nP -iTCP:8080,5173,5433,4566,10000,10001,10002,8085 -sTCP:LISTEN
```

Host `5432` may already be in use — lab Postgres maps **5433→5432**.

## Do not

- `docker volume rm` without confirmation
- Kill unrelated containers
- Commit `.env` or cloud credentials

## API won't start

1. Is Postgres healthy? `docker compose -f deploy/compose/docker-compose.yml ps`
2. JDBC URL uses port `5433`
3. Profile: `local` default
