# Runbook: emulator honesty (v1)

When editing docs or adapters:

1. Label each dependency as LOCAL EMULATOR, OPEN SOURCE ALTERNATIVE, APPROXIMATION, REAL CLOUD, or N/A.
2. Never present LocalStack / Azurite / Pub/Sub emulator as production AWS / Azure / GCP.
3. Update `docs/architecture/multi-cloud-matrix.md` if capability status changes.
4. Do not claim multi-cloud until the portability criterion in `IMPLEMENTATION_PLAN.md` is met.

SSOT: ADR-001, ADR-003, multi-cloud matrix.
