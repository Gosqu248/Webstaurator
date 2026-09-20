# Infrastruktura Webstauratora opisana deklaratywnie przeciwko floci (lokalny emulator AWS).
# Wymaga uruchomionego floci (docker-compose up floci) na porcie 4566.
# Ten sam plik, ze zmienionym providerem (bez endpoints{}, z realnymi poświadczeniami/rolą IAM
# zamiast "test"/"test"), stosuje się wprost na prawdziwym koncie AWS — patrz
# backend/md/todo-v2/08-floci-eks-s3.md, sekcja 5.

terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
}

provider "aws" {
  region                      = "us-east-1"
  access_key                  = "test"
  secret_key                  = "test"
  skip_credentials_validation = true
  skip_requesting_account_id  = true
  skip_metadata_api_check     = true

  endpoints {
    s3  = "http://localhost:4566"
    eks = "http://localhost:4566"
  }
}

resource "aws_s3_bucket" "media" {
  bucket = "webstaurator-media"
}
