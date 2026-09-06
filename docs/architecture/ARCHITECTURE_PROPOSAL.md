# ARCHITECTURE PROPOSAL — cloud-event-lab

**Status:** Proposed (Phase 0)  
**Data:** 2026-09-06  
**Autor:** Kleilson dos Santos  

## 1. Problema

Demonstrar engenharia cloud-native **multi-cloud** com implementação verificável (código + labs locais + IaC + testes), sem:

- três apps desconexas;
- equivalências inventadas entre AWS/Azure/GCP;
- complexidade artificial (mesh, dezenas de serviços).

## 2. Domínio

**Cloud Event Platform** — API recebe um evento, persiste metadados, opcionalmente armazena payload em object storage, publica em um broker, worker processa de forma assíncrona (idempotente).

```text
Client → REST API → Event Application Service
                         │
            ┌────────────┼────────────┐
            ▼            ▼            ▼
      EventStore    ObjectStorage  MessageBus
      (Postgres)    (port)         (port)
                                      │
                                      ▼
                                   Worker
                                      │
                                      ▼
                              ProcessedEventStore
```

## 3. Estilo de aplicação

**Modular monolith** (Spring Boot 3.x / Java 21):

| Módulo | Responsabilidade |
| --- | --- |
| `domain` | Event, status, regras de idempotência |
| `application` | Use cases (ingest, process, query) |
| `adapters-web` | REST + security |
| `adapters-persistence` | JPA/Postgres |
| `adapters-aws` | S3 / SQS / (opcional) Secrets via SDK |
| `adapters-azure` | Blob / Queue (Azurite) |
| `adapters-gcp` | Pub/Sub emulator (+ GCS só REAL_CLOUD) |
| `adapters-local` | FS + in-process/RabbitMQ opcional |
| `bootstrap` | Spring Boot app + profiles |

**Por que não microservices no MVP:** o objetivo é portabilidade de **infra adapters**, não decomposição prematura. Um deploy unitário facilita kind + compose + demo.

## 4. Abstrações (somente onde há benefício)

Ports (interfaces no `application`):

- `ObjectStoragePort` — put/get/delete bytes  
- `MessageBusPort` — publish / subscribe (ou poll)  
- `SecretStorePort` — get secret by name  

**Não** abstrair Postgres no MVP (uma DB relacional portátil basta).  
**Não** criar “CloudProvider” god-interface.

Ativação por Spring profile: `local` | `aws` | `azure` | `gcp` | `test`.

## 5. Labs locais (estratégia)

| Profile | Stack local | Fonte |
| --- | --- | --- |
| `local` | Postgres `5433`, FS `./data`, messaging in-memory ou container leve | OSS |
| `aws` | LocalStack em `4566` (auth/Hobby — ver docs/aws) | [LocalStack](https://docs.localstack.cloud/) |
| `azure` | Azurite `10000–10002`; Service Bus emulator opcional `5672` | [Azurite](https://learn.microsoft.com/en-us/azure/storage/common/storage-use-azurite), [SB emulator](https://learn.microsoft.com/en-us/azure/service-bus-messaging/overview-emulator) |
| `gcp` | `gcloud beta emulators pubsub` / `PUBSUB_EMULATOR_HOST` | [Pub/Sub emulator](https://cloud.google.com/pubsub/docs/emulator) |
| `kubernetes` | kind + manifests | [kind](https://kind.sigs.k8s.io/) |

## 6. Frontend

React + TypeScript (Vite): dashboard simples — criar evento, listar status, health da API. Sem overdesign.

## 7. IaC

**OpenTofu/Terraform** com pastas `infra/aws|azure|gcp|kubernetes` (não um módulo único forçado). Providers oficiais HashiCorp/OpenTofu.

## 8. DevSecOps / Observability (MVP)

- CI: lint, test, Trivy (fs/image), Gitleaks, (opcional) Semgrep  
- App: OpenTelemetry API/SDK → collector local (traces/metrics/logs)  
- Cloud backends de telemetria = REAL_CLOUD / futuro

## 9. Deploy público (diferente do lab completo)

| Camada | Candidato | Nota |
| --- | --- | --- |
| Site/docs demo | Cloudflare Pages / GitHub Pages | Verificar política atual no momento do deploy |
| API mínima | Cloudflare Workers / container free tier | **Não** embutir LocalStack/Azurite/emuladores em produção |

Live demo = API slim + UI; labs multi-cloud = **local/CI**.

## 10. Riscos e limitações assumidas

1. LocalStack licensing/auth mudou (2026) — documentar Hobby/non-commercial.  
2. Key Vault / GCP Secret Manager sem emulador oficial adequado → env local.  
3. GCS sem emulador server-side oficial completo → adapter REAL_CLOUD ou skip no MVP.  
4. Java Pub/Sub client + emulator exige wiring explícito ([docs GCP](https://cloud.google.com/pubsub/docs/emulator)).  
5. Service Bus emulator: dev/test only, sem suporte oficial de produto.

## 11. Definition of Done (rastreio)

Ver README + [IMPLEMENTATION_PLAN](./IMPLEMENTATION_PLAN.md). Phase 0 (este documento + matriz + ADRs + scaffold) é pré-requisito de código de adapters.
