provider "aws" {
  access_key = "test"
  secret_key = "test"
  region     = "us-east-1"

  skip_credentials_validation = true
  skip_metadata_api_check     = true
  skip_requesting_account_id  = true

  endpoints {
    ecr = "http://localhost:4566"
    sts = "http://localhost:4566"
  }
}

variables {
  project               = "mechanical-hub"
  image_retention_count = 5
  tags                  = { ManagedBy = "terraform", Env = "test" }
}

run "ecr_repository_is_created" {
  command = apply

  module {
    source = "../../modules/ecr"
  }

  assert {
    condition     = output.repository_url != ""
    error_message = "ECR repository was not created — repository_url is empty."
  }

  assert {
    condition     = endswith(output.repository_url, "mechanical-hub/api")
    error_message = "ECR repository URL does not end with 'mechanical-hub/api'."
  }
}
