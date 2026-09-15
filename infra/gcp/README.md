# OpenTofu / Terraform — GCP

## Classification

| Item | Type |
| --- | --- |
| This tree | **Skeleton** (Phase 5) |
| Pub/Sub emulator | **LOCAL EMULATOR** (`PUBSUB_EMULATOR_HOST`) — not this provider |
| GCS | **REAL_CLOUD** or FS **APPROX** in the app |
| `tofu apply` | **REAL CLOUD** |

## Usage

```bash
cd infra/gcp
cp terraform.tfvars.example terraform.tfvars
tofu init -backend=false
tofu validate
```
