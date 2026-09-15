# Infrastructure (OpenTofu / Terraform)

## Classification

| Path | Type |
| --- | --- |
| `infra/aws`, `infra/azure`, `infra/gcp` | **Skeleton only** — not applied by default |
| LocalStack / Azurite / Pub/Sub emulator | **LOCAL EMULATOR** (compose) — separate from this tree |
| Real cloud apply | **REAL CLOUD** — manual, owner-gated, never CI-default |

## Rules

1. Do **not** wire LocalStack endpoints as the default provider config (avoids accidental “fake AWS” deploys).
2. Do **not** commit credentials or account IDs.
3. Shared modules stay under `infra/modules/` only for naming/tags — no fake multi-cloud mega-module (ADR-007).
4. Prefer **OpenTofu** (`tofu`) or Terraform ≥ 1.6; configs are HCL-compatible.

## Layout

```text
infra/
  aws/     # provider aws — skeleton
  azure/   # provider azurerm — skeleton
  gcp/     # provider google — skeleton
  kubernetes/  # notes → deploy/kind (Phase 4)
  modules/ # optional shared helpers
```

## Local validation (no cloud)

```bash
cd infra/aws
tofu init -backend=false
tofu validate
```

Repeat for `azure` / `gcp` after installing the matching provider plugins (first `init` downloads them).

## Apply (REAL CLOUD — optional)

Only with explicit credentials and a reviewed plan. This lab does **not** auto-apply from GitHub Actions in Phase 5.
