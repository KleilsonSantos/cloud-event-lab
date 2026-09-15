# GCP lab (Pub/Sub emulator)

## Classification

| Item | Type |
| --- | --- |
| Pub/Sub emulator (`gcloud` / compose profile `gcp`) | **LOCAL EMULATOR** |
| Firestore emulator | **LOCAL EMULATOR** (optional; not MVP) |
| Cloud Storage | **REAL CLOUD** or FS **APPROX** (no complete official server emulator for apps) |
| Secret Manager | **REAL CLOUD** / env **APPROX** |

## Official references

- Pub/Sub emulator: https://cloud.google.com/pubsub/docs/emulator
- Java client + emulator channel: same doc (env alone is insufficient for Java — lab wires plaintext gRPC)

## Compose

```bash
./scripts/preflight.sh
cd deploy/compose
docker compose --profile gcp up -d
```

Host port `8085`. Alternative without compose:

```bash
gcloud beta emulators pubsub start --host-port=0.0.0.0:8085
```

## Messaging honesty (Pub/Sub)

| Behavior | Classification |
| --- | --- |
| Topic + pull subscription create / publish / pull / ack | Exercised against emulator |
| Skip ack on handler failure → redelivery | Supported via ack deadline |
| Dead-letter topic | **APPROX** on emulator — not configured in this lab |

## App profile

```bash
cd apps/api
mvn spring-boot:run -Dspring-boot.run.profiles=gcp
```

`lab.cloud.provider=gcp` selects Pub/Sub messaging and keeps **FS APPROX** for object storage. Set `lab.gcp.pubsub-emulator-host` (default `localhost:8085`).
