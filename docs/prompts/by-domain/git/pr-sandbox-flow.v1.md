# Runbook: PR sandbox flow (v1)

1. Branch from `sandbox` (`feature/*` or `fix/*`).
2. Open PR → `sandbox` using `.github/pull_request_template.md`.
3. After validation, open PR `sandbox` → `main`.
4. Conventional commits; commit only when the human asks.
5. Author/committer: `Kleilson Santos <kdsdesign1@gmail.com>` only — never `Co-authored-by: Cursor`.
6. Dependabot PRs land on `sandbox` — promote deliberately.

SSOT: `docs/guides/git-workflow.md`, `SECURITY.md`, `.githooks/commit-msg`.
