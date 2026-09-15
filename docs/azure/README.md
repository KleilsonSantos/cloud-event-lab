# Azure lab (Azurite + optional Service Bus emulator)

## Classification

| Item | Type |
| --- | --- |
| Azurite Blob / Queue / Table | **LOCAL EMULATOR** (Microsoft official) |
| Service Bus emulator (Docker) | **LOCAL EMULATOR** — **dev/test only** (not used by Phase 3 Queue adapter) |
| Key Vault | **N/A local** → env secrets **APPROX** |
| Real Azure subscription | **REAL CLOUD** |

## Official references

- Azurite: https://learn.microsoft.com/en-us/azure/storage/common/storage-use-azurite
- Service Bus emulator: https://learn.microsoft.com/en-us/azure/service-bus-messaging/overview-emulator

## Compose

```bash
./scripts/preflight.sh
cd deploy/compose
docker compose --profile azure up -d
```

Ports: blob `10000`, queue `10001`, table `10002`.

## Auth / endpoint (Azurite)

| Knob | Lab default | Notes |
| --- | --- | --- |
| Connection | `UseDevelopmentStorage=true` | Expanded in-adapter to public `devstoreaccount1` credentials (allowlisted in `.gitleaks.toml`) |
| Blob endpoint | `http://127.0.0.1:10000/devstoreaccount1` | |
| Queue endpoint | `http://127.0.0.1:10001/devstoreaccount1` | |
| Container / queue | `cloud-event-lab` / `cloud-events` | Created on demand |

## Messaging honesty (Azure Queue)

| Behavior | Classification |
| --- | --- |
| Send / receive / delete on success | Exercised against Azurite Queue |
| Visibility-timeout retry on handler failure | Supported (APPROX of poison handling) |
| Service Bus dead-letter queue | **APPROX / N/A** in this adapter — use real Service Bus or emulator later if needed |

## App profile

```bash
cd apps/api
mvn spring-boot:run -Dspring-boot.run.profiles=azure
```

`lab.cloud.provider=azure` selects Blob + Queue. Do **not** commit real Azure account keys.
