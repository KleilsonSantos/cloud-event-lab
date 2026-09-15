variable "aws_region" {
  type        = string
  description = "AWS region for REAL_CLOUD experiments"
  default     = "us-east-1"
}

variable "project_name" {
  type        = string
  description = "Name prefix for lab resources"
  default     = "cloud-event-lab"
}
