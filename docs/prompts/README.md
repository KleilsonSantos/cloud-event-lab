# Agent runbooks (`docs/prompts`)

Optional, versioned markdown runbooks for coding agents. **Not** application runtime config.

## Rules

- Prefer ADRs + architecture docs over these prompts when they conflict.
- Keep prompts short; point to SSOT paths instead of duplicating policy.
- Name files `by-domain/<domain>/<slug>.vN.md`.
- These runbooks are the lab’s intentional **Prompt → Loop** layer for agents (Owner Cadence). They are **not** an in-app LangGraph/multi-agent runtime — see [inspirations-and-non-goals.md](../architecture/inspirations-and-non-goals.md).

## Index

| Slug | Domain | File |
| --- | --- | --- |
| local-preflight | ops | [local-preflight.v1.md](./by-domain/ops/local-preflight.v1.md) |
| emulator-honesty | multi-cloud | [emulator-honesty.v1.md](./by-domain/multi-cloud/emulator-honesty.v1.md) |
| pr-sandbox-flow | git | [pr-sandbox-flow.v1.md](./by-domain/git/pr-sandbox-flow.v1.md) |
