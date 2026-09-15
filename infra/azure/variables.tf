variable "location" {
  type        = string
  description = "Azure location for REAL_CLOUD experiments"
  default     = "eastus"
}

variable "project_name" {
  type        = string
  description = "Name prefix for lab resources"
  default     = "cloud-event-lab"
}
