# Testing

| Layer | Status |
| --- | --- |
| Unit (domain) | ✅ |
| API integration (MockMvc + H2 + in-memory bus) | ✅ |
| API Postgres IT (Testcontainers; skipped if Docker unavailable) | ✅ Phase 1 |
| ObjectStorage portability (FS + LocalStack S3 + Azurite; skip without Docker) | ✅ Phase 2 |
| MessageBus portability (in-memory + SQS + Azure Queue + Pub/Sub; skip without Docker) | ✅ Phase 3 |
| kind manifests + OTel profile (manual / script bootstrap) | ✅ Phase 4 |
| E2E browser | optional later |
| Infra validation | ✅ Phase 5 (OpenTofu skeletons + `tofu validate` local; no CI apply) |

```bash
cd apps/api && mvn -B test
# Postgres IT + storage/messaging emulator contracts require Docker; without it those tests are skipped.
```
