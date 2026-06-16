variable "aws_region" {
  description = "AWS region. AWS Academy only allows us-east-1 and us-west-2."
  type        = string
  default     = "us-east-1"

  validation {
    condition     = contains(["us-east-1", "us-west-2"], var.aws_region)
    error_message = "AWS Academy only allows us-east-1 or us-west-2."
  }
}

variable "project" {
  description = "Project name used as a prefix for all resource names."
  type        = string
  default     = "mechanical-hub"
}

variable "environment" {
  description = "Deployment environment (production, staging)."
  type        = string
  default     = "production"
}

# ── VPC ──────────────────────────────────────────────────────────────────────

variable "vpc_cidr" {
  description = "CIDR block for the VPC."
  type        = string
  default     = "10.0.0.0/16"
}

variable "availability_zones" {
  description = "List of AZs to deploy into (must be within var.aws_region)."
  type        = list(string)
  default     = ["us-east-1a", "us-east-1b"]
}

variable "public_subnet_cidrs" {
  description = "CIDR blocks for public subnets (one per AZ). Used by the load balancer."
  type        = list(string)
  default     = ["10.0.1.0/24", "10.0.2.0/24"]
}

variable "private_subnet_cidrs" {
  description = "CIDR blocks for private subnets (one per AZ). Used by EKS nodes and RDS."
  type        = list(string)
  default     = ["10.0.11.0/24", "10.0.12.0/24"]
}

# ── EKS ──────────────────────────────────────────────────────────────────────

variable "eks_cluster_version" {
  description = "Kubernetes version for the EKS cluster."
  type        = string
  default     = "1.33"
}

variable "eks_node_instance_type" {
  description = "EC2 instance type for EKS worker nodes."
  type        = string
  default     = "t3.medium"
}

variable "eks_node_desired_size" {
  description = "Desired number of EKS worker nodes."
  type        = number
  default     = 2
}

variable "eks_node_min_size" {
  description = "Minimum number of EKS worker nodes."
  type        = number
  default     = 1
}

variable "eks_node_max_size" {
  description = "Maximum number of EKS worker nodes."
  type        = number
  default     = 4
}

# ── RDS ──────────────────────────────────────────────────────────────────────

variable "db_instance_class" {
  description = "RDS instance class."
  type        = string
  default     = "db.t3.micro"
}

variable "db_name" {
  description = "Name of the PostgreSQL database."
  type        = string
  default     = "mechanical_hub"
}

variable "db_username" {
  description = "Master username for the RDS instance."
  type        = string
  default     = "mechanical_hub"
}

variable "db_password" {
  description = "Master password for the RDS instance. Provided via TF_VAR_db_password env var in CI."
  type        = string
  sensitive   = true
}

variable "db_allocated_storage" {
  description = "Allocated storage in GB for the RDS instance."
  type        = number
  default     = 20
}

# ── ECR ──────────────────────────────────────────────────────────────────────

variable "ecr_image_retention_count" {
  description = "Number of tagged images to keep in ECR before older ones are pruned."
  type        = number
  default     = 10
}
