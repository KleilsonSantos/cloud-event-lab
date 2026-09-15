# OpenTofu / Terraform — Azure

## Classification

| Item | Type |
| --- | --- |
| This tree | **Skeleton** (Phase 5) |
| Azurite | **LOCAL EMULATOR** via compose — separate from this tree |
| `tofu apply` | **REAL CLOUD** |

## Usage

```bash
cd infra/azure
cp terraform.tfvars.example terraform.tfvars
tofu init -backend=false
tofu validate
```

Separate tree from AWS/GCP (ADR-007).
