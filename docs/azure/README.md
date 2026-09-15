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
./scripts/preflight.sh
# or: lsof -nP -iTCP:10000,10001,10002 -sTCP:LISTEN

cd deploy/compose
docker compose --profile azure up -d
```

Default Azurite ports: blob `10000`, queue `10001`, table `10002`.

## Auth / endpoint (Azurite)

| Knob | Lab default | Notes |
| --- | --- | --- |
| Connection string | `UseDevelopmentStorage=true` (`lab.azure.storage-connection`) | Expanded in-adapter to Azurite’s **public** `devstoreaccount1` credentials (documented by Microsoft; allowlisted in `.gitleaks.toml`) |
| Blob endpoint | `http://127.0.0.1:10000/devstoreaccount1` (`lab.azure.blob.endpoint`) | Matches compose host ports |
| Container | `cloud-event-lab` | Created on adapter startup if missing |

Do **not** commit real Azure account keys. Emulator credentials are public by design; REAL_CLOUD uses env / secret store only.

## App profile

Object storage uses the `azure` profile (`AzureBlobObjectStorageAdapter`). Keep `local` active until Phase 3 for the in-memory bus/worker:

```bash
cd apps/api
./mvnw spring-boot:run -Dspring-boot.run.profiles=azure,local
```

`lab.cloud.provider=azure` selects Blob over the filesystem adapter even when `local` is also active.

Adapters for Queue / Service Bus = Phase 3.
