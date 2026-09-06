# Multi-cloud capability matrix

> Fontes: documentação oficial LocalStack, Microsoft Learn (Azurite, Functions, Service Bus emulator), Google Cloud (Pub/Sub emulator, Firestore emulator).  
> Legenda de implementação local: **DIRECT** · **APPROX** · **REAL_CLOUD** · **N/A local** · **NÃO VALIDADO**

| Capability | AWS (produção) | Azure (produção) | GCP (produção) | Local implementation | Tipo |
| --- | --- | --- | --- | --- | --- |
| Serverless compute | Lambda | Azure Functions | Cloud Functions / Cloud Run | LocalStack Lambda; Azure Functions Core Tools; Cloud Run = container local | APPROX (parity limitada) |
| Object storage | S3 | Blob Storage | Cloud Storage | LocalStack S3; **Azurite** Blob ([docs](https://learn.microsoft.com/en-us/azure/storage/common/storage-use-azurite)); GCS sem emulador oficial completo para apps server → **fake FS / REAL_CLOUD** | DIRECT (S3/Blob) · APPROX/N/A (GCS) |
| Queue | SQS | Queue Storage / Service Bus Queue | Pub/Sub (pull) / Cloud Tasks | LocalStack SQS; Azurite Queue; **Service Bus emulator** (Docker, [docs](https://learn.microsoft.com/en-us/azure/service-bus-messaging/overview-emulator)); Pub/Sub emulator | DIRECT / APPROX |
| Pub/Sub | SNS + SQS / EventBridge | Service Bus Topics / Event Grid | **Pub/Sub** | LocalStack SNS/EventBridge; Service Bus emulator; **gcloud Pub/Sub emulator** ([docs](https://cloud.google.com/pubsub/docs/emulator)) | DIRECT / APPROX |
| NoSQL | DynamoDB | Cosmos DB | Firestore / Datastore | LocalStack DynamoDB; Cosmos **emulator** existe (Windows-centric historicamente — macOS: **NÃO VALIDADO** neste lab); Firestore via gcloud/Firebase emulator | MIXED |
| Secrets | Secrets Manager | Key Vault | Secret Manager | LocalStack Secrets Manager (coverage parcial); Key Vault **sem emulador oficial Microsoft** → env/file local ou community (**APPROX**); GCP SM → REAL_CLOUD / env | APPROX |
| Containers | ECS / App Runner | Container Apps | Cloud Run | Docker Compose (local) | APPROX (runtime container ≠ managed service) |
| Kubernetes | EKS | AKS | GKE | **kind** (cluster local) | APPROX (control plane local ≠ managed K8s cloud) |
| Observability | CloudWatch | Application Insights | Cloud Logging / Trace | **OpenTelemetry** → collector local (Prometheus/Grafana/logs) — portátil | DIRECT (OTel) · cloud backends = REAL_CLOUD |
| IAM | IAM | Entra ID / RBAC | IAM | Emulação IAM incompleta / diferente por cloud | APPROX / REAL_CLOUD |

## Notas críticas (não omitir)

### LocalStack (AWS)

- Documentação de serviços: [docs.localstack.cloud/aws/services](https://docs.localstack.cloud/aws/services/).  
- Em 2026, distribuição Community legacy arquivada; execução atual é **account-based** (Hobby free **non-commercial** segundo [pricing](https://localstack.cloud/pricing)).  
- **Não** tratar LocalStack como AWS produção.  
- Features avançadas / K8s executor: planos pagos ([docs K8s](https://docs.localstack.cloud/aws/customization/kubernetes/)).

### Azure

- **Azurite**: Blob, Queue, Table — oficial ([MS Learn](https://learn.microsoft.com/en-us/azure/storage/common/storage-use-azurite)).  
- **Functions Core Tools**: runtime local oficial; storage via Azurite (`UseDevelopmentStorage=true`).  
- **Service Bus emulator**: oficial via Docker; **só dev/test**, sem SLA/suporte oficial de produto ([overview](https://learn.microsoft.com/en-us/azure/service-bus-messaging/overview-emulator)).  
- **Key Vault**: sem emulador oficial Microsoft → secrets via env/file no profile `local` (**NÃO VALIDADO** como paridade).  
- **Event Grid / Cosmos**: avaliar sob demanda; não forçar no MVP.

### GCP

- **Pub/Sub emulator**: oficial via `gcloud` ([docs](https://cloud.google.com/pubsub/docs/emulator)); Java client exige config explícita de canal (não só env em todos os casos).  
- **Firestore emulator**: oficial via gcloud ([docs](https://cloud.google.com/firestore/native/docs/emulator)) — in-memory, não produção.  
- **Cloud Storage / Secret Manager / Cloud Run managed**: sem emulador completo equivalente → REAL_CLOUD ou APPROX (FS / env / container).

### Equivalência

| Par | Veredito |
| --- | --- |
| S3 ↔ Blob ↔ GCS | Conceito similar (object storage); APIs **não** idênticas |
| SQS ↔ Queue Storage ↔ Pub/Sub | Semântica de fila/pub-sub **diferente** |
| SNS/EventBridge ↔ Service Bus/Event Grid ↔ Pub/Sub | **Não** equivalentes 1:1 |
| DynamoDB ↔ Cosmos ↔ Firestore | Modelos de dados diferentes |

## Escopo MVP deste lab (mínimo honesto)

| Capability | Escolha MVP |
| --- | --- |
| Persistência de metadados de evento | PostgreSQL (portátil; não é “NoSQL cloud”) |
| Object payload | Port `ObjectStorage` → FS local · S3 (LocalStack) · Blob (Azurite) · GCS adapter só REAL_CLOUD |
| Messaging | Port `MessageBus` → Spring/local · SQS · Azure Queue ou Service Bus emulator · Pub/Sub emulator |
| Secrets | Port `SecretStore` → env/file (local); cloud adapters documentados como REAL_CLOUD quando necessário |
| Observability | OpenTelemetry SDK na app (independente de cloud) |
| Kubernetes | kind manifests da API (+ Postgres) |
