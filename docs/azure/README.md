# Azure lab (Azurite + optional Service Bus emulator)

## Classification

| Item | Type |
| --- | --- |
| Azurite Blob / Queue / Table | **LOCAL EMULATOR** (Microsoft official) |
| Service Bus emulator (Docker) | **LOCAL EMULATOR** — **dev/test only** |
| Key Vault | **N/A local** (no official MS emulator) → env secrets **APPROX** |
| Real Azure subscription | **REAL CLOUD** |

## Official references

- Azurite: https://learn.microsoft.com/en-us/azure/storage/common/storage-use-azurite
- Service Bus emulator: https://learn.microsoft.com/en-us/azure/service-bus-messaging/overview-emulator
- Functions Core Tools: https://learn.microsoft.com/en-us/azure/azure-functions/functions-run-local

## Compose

```bash
lsof -nP -iTCP:10000,10001,10002 -sTCP:LISTEN

cd deploy/compose
docker compose --profile azure up -d
```

Default Azurite ports: blob `10000`, queue `10001`, table `10002`.

## App profile

```bash
cd apps/api
./mvnw spring-boot:run -Dspring-boot.run.profiles=azure,local
```

Adapters Azure SDK = Phase 2/3.
