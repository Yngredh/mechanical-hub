provider "aws" {
  access_key = "test"
  secret_key = "test"
  region     = "us-east-1"

  skip_credentials_validation = true
  skip_metadata_api_check     = true
  skip_requesting_account_id  = true

  endpoints {
    ec2  = "http://localhost:4566"
    rds  = "http://localhost:4566"
    sts  = "http://localhost:4566"
  }
}

# Step 1: Create the VPC the RDS module depends on.
run "setup_vpc" {
  command = apply

  module {
    source = "../../modules/vpc"
  }

  variables {
    project              = "mhub-test"
    environment          = "test"
    vpc_cidr             = "10.88.0.0/16"
    availability_zones   = ["us-east-1a", "us-east-1b"]
    public_subnet_cidrs  = ["10.88.1.0/24", "10.88.2.0/24"]
    private_subnet_cidrs = ["10.88.11.0/24", "10.88.12.0/24"]
    tags                 = { ManagedBy = "terraform" }
  }
}

# Step 2: Create a placeholder security group (the EKS node SG the RDS module expects).
run "setup_eks_sg_placeholder" {
  command = apply

  variables {
    project              = "mhub-test"
    environment          = "test"
    vpc_cidr             = "10.88.0.0/16"
    availability_zones   = ["us-east-1a", "us-east-1b"]
    public_subnet_cidrs  = ["10.88.1.0/24", "10.88.2.0/24"]
    private_subnet_cidrs = ["10.88.11.0/24", "10.88.12.0/24"]
    tags                 = { ManagedBy = "terraform" }
  }

  module {
    source = "../../modules/vpc"
  }
}

# Step 3: Deploy RDS using the VPC outputs from step 1.
run "rds_instance_is_created" {
  command = apply

  module {
    source = "../../modules/rds"
  }

  variables {
    project            = "mhub-test"
    environment        = "test"
    vpc_id             = run.setup_vpc.vpc_id
    private_subnet_ids = run.setup_vpc.private_subnet_ids
    eks_sg_id          = "sg-00000000"
    db_instance_class  = "db.t3.micro"
    db_name            = "mechanical_hub"
    db_username        = "mechanical_hub"
    db_password        = "test-password-123"
    allocated_storage  = 20
    tags               = { ManagedBy = "terraform" }
  }

  assert {
    condition     = output.endpoint != ""
    error_message = "RDS endpoint is empty — instance was not created."
  }

  assert {
    condition     = output.port == 5432
    error_message = "RDS port should be 5432."
  }

  assert {
    condition     = output.db_name == "mechanical_hub"
    error_message = "RDS database name mismatch."
  }
}
