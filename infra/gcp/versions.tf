terraform {
  required_version = ">= 1.6.0"

  required_providers {
    google = {
      source  = "hashicorp/google"
      version = "~> 6.0"
    }
  }
}

provider "google" {
  project = var.project_id
  region  = var.region
  # REAL_CLOUD only — Pub/Sub emulator is env-based (PUBSUB_EMULATOR_HOST), not this provider.
}

# Phase 5 skeleton: no managed resources yet.
