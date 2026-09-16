# Inspirations and non-goals

How community signals enrich this lab **without** diluting multi-cloud honesty (ADR-003, [multi-cloud-matrix](./multi-cloud-matrix.md)).

## In scope (enrichment)

| Idea | Source flavor | What we take | What we do **not** take |
| --- | --- | --- | --- |
| Lightweight MIT AWS emulator (e.g. [Floci](https://github.com/floci-io/floci)) | OSS AWS local emulator | Optional Compose profile `aws-floci` — same app endpoint shape (`localhost:4566`) as LocalStack for S3/SQS wiring | Claiming Floci = AWS; swapping Azure/GCP stacks; trusting marketing “floci-az / floci-gcp” until **validated here** |
| System-design pillars (arch, scale, security, data, monitoring, reliability) | Common architecture posters | Gap checklist vs lab reality (Postgres ≠ Dynamo; Basic auth ≠ Entra; OTel ≠ CloudWatch; kind ≠ EKS) | Turning the lab into a full “production system design” product |
| Prompt → Loop → Graph (agent orchestration) | AI-engineering narratives | Owner Cadence + `docs/prompts` as intentional **loops/runbooks** for agents | Embedding LangGraph/multi-agent runtime into the event platform |

## Explicit non-goals (do not couple)

| Idea | Why out of core |
| --- | --- |
| Vision browser agents (e.g. Magnitude) | Automates consoles/UI — orthogonal to SDK/adapter ports; does not improve emulator fidelity |
| Generic “10 coding skills” packs | Useful for an IDE agent; not multi-cloud capability or honesty |
| Mega multi-cloud single emulator | Violates ADR-007 (separate trees) and invents parity the matrix forbids |

## Emulator stance (unchanged)

1. Local tools exercise **SDK/API wiring** for chosen capabilities (object storage + messaging).  
2. Console layouts and day-to-day CLI UX of AWS/Azure/GCP **will diverge** — we do not simulate portals.  
3. Production truth remains **REAL_CLOUD** (Phase 6 / owner-gated).  
4. Always label: **LOCAL EMULATOR** | **OPEN SOURCE ALTERNATIVE** | **APPROXIMATION** | **REAL CLOUD** | **N/A**.

## Compose profiles (AWS)

| Profile | Image | Classification | Notes |
| --- | --- | --- | --- |
| `aws` (default lab) | LocalStack | **LOCAL EMULATOR** | Documented Hobby/licensing constraints — revalidate on use date |
| `aws-floci` (optional spike) | `floci/floci` | **LOCAL EMULATOR** / **OPEN SOURCE ALTERNATIVE** | MIT; **exclusive** with `aws` (both bind `:4566`) |

App profile stays `spring.profiles.active=aws` either way — only the emulator behind `lab.aws.endpoint` changes.
