# ── EKS ──────────────────────────────────────────────────────────────────────

output "eks_cluster_name" {
  description = "EKS cluster name. Used in GitHub Actions: aws eks update-kubeconfig --name <value>"
  value       = module.eks.cluster_name
}

output "eks_cluster_endpoint" {
  description = "EKS API server endpoint."
  value       = module.eks.cluster_endpoint
}

# ── ECR ──────────────────────────────────────────────────────────────────────

output "ecr_repository_url" {
  description = "ECR repository URL. Used in GitHub Actions as ECR_REGISTRY env var."
  value       = module.ecr.repository_url
}

# ── RDS ──────────────────────────────────────────────────────────────────────

output "rds_endpoint" {
  description = "RDS instance endpoint hostname. Used as DB_HOST in the GitHub Actions pipeline."
  value       = module.rds.endpoint
}

output "rds_port" {
  description = "RDS instance port."
  value       = module.rds.port
}

output "rds_db_name" {
  description = "PostgreSQL database name."
  value       = module.rds.db_name
}

# ── VPC ──────────────────────────────────────────────────────────────────────

output "vpc_id" {
  description = "VPC ID."
  value       = module.vpc.vpc_id
}
