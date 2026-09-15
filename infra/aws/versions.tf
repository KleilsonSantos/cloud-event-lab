terraform {
  required_version = ">= 1.6.0"

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
}

# REAL_CLOUD provider — do NOT set LocalStack endpoints here by default.
# For emulator experiments, override via env (AWS_ENDPOINT_URL, etc.) outside this file.
provider "aws" {
  region = var.aws_region
}

# Phase 5 skeleton: no managed resources yet.
# Future: S3 bucket / SQS queue names aligned with lab.aws.* application config.
