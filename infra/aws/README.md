# OpenTofu / Terraform — AWS

## Classification

| Item | Type |
| --- | --- |
| This tree | **Skeleton** (Phase 5) |
| LocalStack | **LOCAL EMULATOR** via `deploy/compose` — not the default provider target |
| `tofu apply` to a real account | **REAL CLOUD** |

## Usage

```bash
cd infra/aws
cp terraform.tfvars.example terraform.tfvars   # local only
tofu init -backend=false
tofu validate
# tofu plan   # REAL_CLOUD credentials required
```

Provider endpoints for LocalStack belong in **environment overrides**, never as committed defaults — so a mistaken apply cannot look like “production AWS” against an emulator.
