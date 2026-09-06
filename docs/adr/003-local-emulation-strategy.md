# ADR-003: Local emulation strategy

- **Status:** Accepted  
- **Date:** 2026-09-06  

## Context

Precisamos de labs locais reproduzíveis. Nem todo serviço tem emulador oficial.

## Decision

| Cloud | Ferramentas locais adotadas no lab |
| --- | --- |
| AWS | LocalStack (S3, SQS; outros sob demanda) — verificar licensing Hobby/non-commercial |
| Azure | Azurite (Blob/Queue); Service Bus emulator opcional (Docker) |
| GCP | Pub/Sub emulator (`gcloud`); Firestore só se necessário depois |
| Secrets | env/file no `local`; cloud secrets = REAL_CLOUD até existir opção oficial |

Sempre classificar: **LOCAL EMULATOR** | **OPEN SOURCE ALTERNATIVE** | **APPROXIMATION** | **REAL CLOUD** | **N/A**.

## Alternatives

1. Só cloud free tier remota — rejeitado (lento, custo, flaky CI).  
2. Mocks puros sem emulador — rejeitado (não exercita SDK/wiring real).

## Consequences

+ Labs honestos.  
− CI pode precisar tokens LocalStack / imagens extras.  
− Key Vault / GCS / Secret Manager documentados como gap.
