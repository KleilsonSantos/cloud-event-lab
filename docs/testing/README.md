# Testing

| Layer | Status |
| --- | --- |
| Unit (domain) | ✅ |
| API integration (MockMvc + H2 + in-memory bus) | ✅ |
| API Postgres IT (Testcontainers; skipped if Docker unavailable) | ✅ Phase 1 |
| Portability (same behavior aws/azure/gcp adapters) | Phase 2–3 |
| E2E browser | optional later |
| Infra validation | Phase 5 |

```bash
cd apps/api && mvn -B test
# Postgres IT requires Docker; without it the Testcontainers test is skipped.
```
