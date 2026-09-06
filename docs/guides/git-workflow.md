# Git workflow — cloud-event-lab

## Branches

| Branch | Role |
| --- | --- |
| `main` | Stable, releasable tip |
| `sandbox` | Integration / staging for the lab |
| `feature/*`, `fix/*` | Work branches |

## Flow

```text
Issue
  → branch from sandbox (`feature/*` | `fix/*` | `docs/*` | `ci/*`)
  → comment on Issue with branch name
  → semantic commits by domain scope
  → PR → sandbox (`Refs #N`)
  → PR sandbox → main (`Closes #N`)
```

- Do **not** push feature work directly to `main` or commit on `sandbox`/`main`.
- Conventional Commits with **domain scopes**:

| Type | Scope examples | Use for |
| --- | --- | --- |
| `feat` | `api`, `web`, `deploy` | Product / lab capability |
| `docs` | `architecture`, `adr`, `readme`, `guides` | Documentation |
| `ci` | `github`, `security` | Actions, scanners, Dependabot |
| `chore` | `repo`, `deps` | Bootstrap, tooling, deps |
| `fix` / `refactor` / `test` / `security` | same scopes | As needed |

Examples: `feat(api): add local ingest endpoint`, `ci(github): add CodeQL and Gitleaks`, `docs(architecture): accept ADR-008`.

- One Issue → one work branch → one or more commits **in that domain**; avoid mixing unrelated domains in the same commit.
- Commit only when the human asks (agents included).

## Author identity

- Content commits: **Author and Committer** `Kleilson Santos <kdsdesign1@gmail.com>`
- **Never** `Co-authored-by: Cursor` / `cursoragent@cursor.com` / other IDE agent trailers.
- PR bodies must **not** include “Made with Cursor”; attribution is the human author, not the tool.
- `gh pr merge --merge` may author merge commits as the GitHub noreply identity; CI allows those only when the subject starts with `merge:`.
- This clone uses local `user.name` / `user.email` / `core.hooksPath=.githooks`. Fresh clones need that local trio once — `gh` auth does not set Git commit identity.
- `.githooks/commit-msg` strips IDE trailers; CI runs `scripts/check-commit-messages.sh` on PRs.

## Dependabot

- Version update PRs target **`sandbox`**.
- Promote dependency bumps with the same sandbox → main flow.
- Security auto-PRs (if enabled in Settings) may target `main` — see `SECURITY.md`.

## CI

Workflow: `.github/workflows/ci.yml`

| Job | Purpose |
| --- | --- |
| `api` | Maven tests |
| `web` | npm build (+ soft npm audit) |
| `security` | Gitleaks, Trivy fs, ShellCheck |
| `codeql` | SAST Java + JS/TS |
