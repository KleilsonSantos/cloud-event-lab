# GCP lab (Pub/Sub emulator)

## Classification

| Item | Type |
| --- | --- |
| Pub/Sub emulator (`gcloud`) | **LOCAL EMULATOR** |
| Firestore emulator | **LOCAL EMULATOR** (optional; not MVP) |
| Cloud Storage | **REAL CLOUD** or FS **APPROX** (no complete official server emulator for apps) |
| Secret Manager | **REAL CLOUD** / env **APPROX** |

## Official references

- Pub/Sub emulator: https://cloud.google.com/pubsub/docs/emulator
- Java client + emulator channel: same doc (env alone may be insufficient for Java)

## Start emulator

```bash
lsof -nP -iTCP:8085 -sTCP:LISTEN

gcloud beta emulators pubsub start --host-port=0.0.0.0:8085
export PUBSUB_EMULATOR_HOST=localhost:8085
```

## App profile

```bash
cd apps/api
./mvnw spring-boot:run -Dspring-boot.run.profiles=gcp,local
```

Adapters GCP = Phase 3.
