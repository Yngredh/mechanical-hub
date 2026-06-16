locals {
  common_tags = {
    Project     = var.project
    Environment = var.environment
    ManagedBy   = "terraform"
  }
}

data "aws_iam_role" "lab_role" {
  name = "LabRole"
}

module "vpc" {
  source = "./modules/vpc"

  project              = var.project
  environment          = var.environment
  vpc_cidr             = var.vpc_cidr
  availability_zones   = var.availability_zones
  public_subnet_cidrs  = var.public_subnet_cidrs
  private_subnet_cidrs = var.private_subnet_cidrs
  tags                 = local.common_tags
}

module "ecr" {
  source = "./modules/ecr"

  project                   = var.project
  image_retention_count     = var.ecr_image_retention_count
  tags                      = local.common_tags
}

module "eks" {
  source = "./modules/eks"

  project              = var.project
  environment          = var.environment
  cluster_version      = var.eks_cluster_version
  vpc_id               = module.vpc.vpc_id
  private_subnet_ids   = module.vpc.private_subnet_ids
  node_instance_type   = var.eks_node_instance_type
  node_desired_size    = var.eks_node_desired_size
  node_min_size        = var.eks_node_min_size
  node_max_size        = var.eks_node_max_size
  lab_role_arn         = data.aws_iam_role.lab_role.arn
  tags                 = local.common_tags
}

module "rds" {
  source = "./modules/rds"

  project            = var.project
  environment        = var.environment
  vpc_id             = module.vpc.vpc_id
  private_subnet_ids = module.vpc.private_subnet_ids
  eks_sg_id          = module.eks.cluster_security_group_id
  db_instance_class  = var.db_instance_class
  db_name            = var.db_name
  db_username        = var.db_username
  db_password        = var.db_password
  allocated_storage  = var.db_allocated_storage
  tags               = local.common_tags
}
