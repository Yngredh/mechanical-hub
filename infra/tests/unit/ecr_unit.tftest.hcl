mock_provider "aws" {
  mock_resource "aws_ecr_repository" {
    defaults = {
      id             = "mechanical-hub/api"
      repository_url = "123456789012.dkr.ecr.us-east-1.amazonaws.com/mechanical-hub/api"
      registry_id    = "123456789012"
    }
  }
  mock_resource "aws_ecr_lifecycle_policy" {
    defaults = { id = "mechanical-hub/api" }
  }
}

variables {
  project               = "mechanical-hub"
  image_retention_count = 10
  tags                  = { ManagedBy = "terraform" }
}

run "ecr_plan_is_valid" {
  command = plan

  module {
    source = "../../modules/ecr"
  }

  assert {
    condition     = var.image_retention_count > 0
    error_message = "image_retention_count must be greater than 0."
  }
}

run "ecr_repository_name_includes_project" {
  command = plan

  module {
    source = "../../modules/ecr"
  }

  assert {
    condition     = startswith(var.project, "mechanical")
    error_message = "Project name should start with 'mechanical' for this repo."
  }
}
