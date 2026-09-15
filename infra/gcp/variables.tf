variable "project_id" {
  type        = string
  description = "GCP project id for REAL_CLOUD experiments"
  default     = "cloud-event-lab-local"
}

variable "region" {
  type        = string
  description = "GCP region"
  default     = "us-central1"
}

variable "project_name" {
  type        = string
  description = "Name prefix for lab resources"
  default     = "cloud-event-lab"
}
