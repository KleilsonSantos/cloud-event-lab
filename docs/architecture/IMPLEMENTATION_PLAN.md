# IMPLEMENTATION PLAN — cloud-event-lab

Ordem: **entrega incremental**. Cada fase termina com checklist verificável.

## Phase 0 — Foundation

- [x] Nome do repo e README honesto  
- [x] Matriz multi-cloud  
- [x] ARCHITECTURE PROPOSAL  
- [x] ADRs 001–007  
- [x] Scaffold API (Spring) + Web (Vite) + compose Postgres  
- [x] `.env.example` sem secrets  
- [x] CI básico (Maven test + web build)
- [x] CI security lattice (Gitleaks, Trivy, CodeQL, ShellCheck)
- [x] GitHub flow docs + PR template + Dependabot → sandbox
- [x] Agent surface (`AGENTS.md`, ADR-008, `.github/agents/`, `docs/prompts/`)

## Phase 1 — Local profile (core business)

- [x] Ingest `POST /api/events`  
- [x] Persistência Postgres  
- [x] Processamento assíncrono local (idempotency key)  
- [x] `GET /api/events/{id}` + `GET /health`  
- [x] Testes unit + integration (H2) + Testcontainers Postgres  
- [x] UI mínima (criar/listar eventos, idempotency replay)  

## Phase 2 — Object storage port

- [x] `ObjectStoragePort` + adapter FS  
- [x] Adapter S3 (LocalStack) — documentar auth LocalStack  
- [x] Adapter Azure Blob (Azurite)  
- [x] Teste de portabilidade: mesmo contrato nos 3 adapters disponíveis  

## Phase 3 — Messaging port

- [ ] `MessageBusPort` + adapter local  
- [ ] Adapter SQS (LocalStack)  
- [ ] Adapter Azure Queue (Azurite) e/ou Service Bus emulator  
- [ ] Adapter Pub/Sub (gcloud emulator)  
- [ ] DLQ/retry onde o broker permitir; senão documentar APPROX  

## Phase 4 — Kubernetes + observability

- [ ] kind cluster + Deployment/Service/probes/resources  
- [ ] OpenTelemetry instrumentation  
- [ ] docker-compose profile `otel`  

## Phase 5 — IaC + DevSecOps

- [ ] OpenTofu/Terraform skeletons `infra/aws|azure|gcp`  
- [x] GitHub Actions: build, test, Gitleaks, Trivy, CodeQL  
- [x] Docs de security scanning (`SECURITY.md`, `docs/security/`)  
- [ ] Maven dependency SCA endurecido (fail-closed)  
- [ ] Trivy image scan no Dockerfile da API  

## Phase 6 — Public demo

- [ ] Decisão REAL_CLOUD free tier **revalidada na data do deploy**  
- [ ] API slim + Pages  
- [ ] Link LIVE DEMO no README (só se existir instância real)  

## Fora de escopo (explícito)

- EKS/AKS/GKE gerenciados no free lab  
- Service mesh  
- Kafka cluster (usar brokers já justificados pelos adapters)  
- Paridade IAM completa  
- Emular Key Vault / GCP SM como se fossem oficiais  

## Critério de “multi-cloud demonstrado”

Um teste (ou script) que:

1. sobe profile A;  
2. executa ingest → process → query;  
3. repete em profile B;  
4. asserta o **mesmo resultado de negócio** (status `PROCESSED`, payload recuperável).

Sem isso, não afirmar suporte multi-cloud.
