# Testing

| Layer | Status |
| --- | --- |
| Unit (domain) | ✅ |
| API integration (MockMvc + H2 + in-memory bus) | ✅ |
| API Postgres IT (Testcontainers; skipped if Docker unavailable) | ✅ Phase 1 |
| ObjectStorage portability (FS + LocalStack S3 + Azurite; skip without Docker) | ✅ Phase 2 |
| MessageBus portability | Phase 3 |
| E2E browser | optional later |
| Infra validation | Phase 5 |

```bash
cd apps/api && mvn -B test
# Postgres IT + S3/Azurite contract tests require Docker; without it those Testcontainers tests are skipped.
```
