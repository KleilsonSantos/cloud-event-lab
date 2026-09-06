# cloud-event-lab

> Laboratório **cloud-native multi-cloud** (AWS · Azure · GCP · Kubernetes · DevSecOps · Observability)  
> Lab técnico autônomo — não um “suporta as três clouds” de marketing.

| | |
| --- | --- |
| **Licença** | MIT |
| **Domínio** | Cloud Event Platform — ingestão REST → processamento assíncrono → persistência + object storage |
| **Estilo** | Modular monolith (Java 21 / Spring Boot) + React / TypeScript |

## O que este projeto é

```text
Mesma aplicação (domínio + API)
        │
        ├── profile local     → Postgres + FS + bus in-memory   ✅ scaffold
        ├── profile aws       → LocalStack (emulação)           🟡 adapters Phase 2/3
        ├── profile azure     → Azurite (+ SB emulator opc.)    🟡 adapters Phase 2/3
        ├── profile gcp       → Pub/Sub emulator (gcloud)       🟡 adapters Phase 3
        └── profile kubernetes    → kind                              ⚪ Phase 4
```

## O que este projeto NÃO é

- Paridade 100% com produção AWS/Azure/GCP  
- Substituição de cloud real por emulador em produção pública  
- Microservices / service mesh / Kafka “por checklist”

## Documentação (comece aqui)

1. [ARCHITECTURE PROPOSAL](docs/architecture/ARCHITECTURE_PROPOSAL.md)  
2. [Capability matrix](docs/architecture/multi-cloud-matrix.md)  
3. [IMPLEMENTATION PLAN](docs/architecture/IMPLEMENTATION_PLAN.md)  
4. ADRs em [`docs/adr/`](docs/adr/)  
5. [Git workflow](docs/guides/git-workflow.md) · [SECURITY](SECURITY.md) · [AGENTS](AGENTS.md)

## Quick start (profile `local`)

```bash
# 0) Preflight — não derruba nada
./scripts/preflight.sh

# 1) Postgres do lab (host 5433 — 5432 já pode estar ocupado)
cd deploy/compose && docker compose up -d postgres && cd ../..

# 2) API
cd apps/api && mvn spring-boot:run

# 3) Web (outro terminal)
cd apps/web && npm install && npm run dev
```

Auth local: `lab` / `lab-change-me` (HTTP Basic). Altere via `.env` (veja `.env.example`).

```bash
curl -u lab:lab-change-me -H 'Content-Type: application/json' \
  -d '{"type":"order.created","source":"curl","idempotencyKey":"demo-1","payloadJson":"{\"ok\":true}"}' \
  http://localhost:8080/api/events
```

Labs AWS/Azure: `docker compose --profile aws|azure up -d` — ver `docs/aws`, `docs/azure`, `docs/gcp`.

## Status (honestidade)

| Capacidade | Status |
| --- | --- |
| Arquitetura + ADRs + matriz | ✅ |
| Scaffold API (domínio, ports, local adapters, REST, security) | ✅ |
| Scaffold web (dashboard lab) | ✅ |
| Compose Postgres (+ profiles LocalStack/Azurite) | ✅ |
| Testes unit + API integration | ✅ |
| AWS / Azure / GCP SDK adapters | ⚪ Phase 2–3 |
| CI security (Gitleaks / Trivy / CodeQL) + agent surface | ✅ |
| K8s / IaC / OTel / SCA Maven endurecido / live demo | ⚪ Phase 4–6 |

## Portas

| Serviço | Porta host |
| --- | --- |
| API | `8080` |
| Web | `5173` |
| Postgres | `5433` → 5432 container |
| LocalStack | `4566` |
| Azurite | `10000` / `10001` / `10002` |
| GCP Pub/Sub emulator | `8085` |
| OTel collector (futuro) | `4317` |

## Licença

MIT — ver [LICENSE](LICENSE).
