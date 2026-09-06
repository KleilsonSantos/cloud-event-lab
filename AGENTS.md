# AGENTS.md — coding agents in this repository

Lightweight entry point for AI tools that auto-load `AGENTS.md`.
This lab is **standalone**: do not invent couplings to other personal repos unless the human explicitly requests them.

## Mission

**cloud-event-lab** — multi-cloud Cloud Event Platform lab (AWS · Azure · GCP · Kubernetes · DevSecOps · Observability) with honest emulator classification and verifiable adapters.

## Source order

1. **Code** — `apps/api`, `apps/web`, `deploy/`, `infra/`, `scripts/`
2. **Architecture** — `docs/architecture/`, `docs/adr/`
3. **Cloud honesty** — `docs/architecture/multi-cloud-matrix.md`, `docs/aws|azure|gcp|kubernetes/`
4. **Delivery** — `docs/architecture/IMPLEMENTATION_PLAN.md`, `.github/`, `docs/guides/`
5. **Agent runbooks** — `docs/prompts/` (optional; never override 1–3)
6. **GitHub agent personas** — `.github/agents/` (task helpers only)

If a summary conflicts with an ADR or the architecture proposal, the ADR/proposal wins until a new ADR changes it.

## Task routing

| Task | Start here |
| --- | --- |
| Architecture / ports / profiles | ADRs 001–007, `ARCHITECTURE_PROPOSAL.md` |
| Git / PR / CI | `docs/guides/git-workflow.md`, `.github/pull_request_template.md`, `.github/workflows/ci.yml` |
| Security / secrets / scans | ADR-006, `SECURITY.md`, `docs/security/` |
| Emulators / local labs | ADR-003, `scripts/preflight.sh`, `deploy/compose/` |
| AI assistance boundaries | ADR-008 |
| Runbooks for agents | `docs/prompts/` |

## Hard constraints

- No secrets in Git; use `.env.example` patterns only.
- Classify infra correctly: **LOCAL EMULATOR** | **OPEN SOURCE ALTERNATIVE** | **APPROXIMATION** | **REAL CLOUD** | **N/A**.
- Do **not** claim multi-cloud support without the portability criterion in `IMPLEMENTATION_PLAN.md`.
- Do **not** deploy LocalStack/Azurite/emulators as public “production cloud”.
- Inspect before install: run `scripts/preflight.sh` before spawning duplicate containers/ports.
- Prefer small, phased changes aligned to the implementation plan.
- Commit only when the human asks (or after `ok` / `prossegue` in Owner Cadence).
- Git author/committer: **Kleilson Santos \<kdsdesign1@gmail.com\>** only — never `Co-authored-by: Cursor` / `cursoragent@cursor.com` / “Made with Cursor” on PRs.
- Do not reference or couple this repo to external personal projects unless the human explicitly asks.

## Owner Cadence

| Signal | Meaning |
| --- | --- |
| `next` | **Proposal only** — plan the slice; do not implement, commit, push, or merge |
| `ok` / `prossegue` | **Implement** the accepted slice (Issue → branch → semantic commits → PR → `sandbox`) |
| `green` | **Promote** validated `sandbox` → `main` (and close Issues with `Closes #N`) |

Before every `next`, provide:

1. Trajectory
2. What
3. Why now
4. Analogy (optional, short)
5. Trade-off
6. Wait for `ok`

Do not treat casual conversation as `ok`. Do not treat `ok` as `green`.
