#!/usr/bin/env bash
# Stawia klaster EKS na floci i wdraża na nim Webstauratora — patrz
# backend/md/todo-v2/08-floci-eks-s3.md, sekcja 3.
#
# Wymaga: floci uruchomione (docker-compose up -d floci), AWS CLI, kubectl, helm.
# Nie jest uruchamiane automatycznie — floci montuje /var/run/docker.sock i odpala
# prawdziwe kontenery k3s na Twojej maszynie.
#
# Użycie: ./k8s/scripts/floci-eks-bootstrap.sh

set -euo pipefail

export AWS_ENDPOINT_URL=http://localhost:4566
export AWS_DEFAULT_REGION=us-east-1
export AWS_ACCESS_KEY_ID=test
export AWS_SECRET_ACCESS_KEY=test

CLUSTER_NAME="webstaurator"

if ! curl -sf "${AWS_ENDPOINT_URL}/_localstack/health" > /dev/null 2>&1 && \
   ! curl -sf "${AWS_ENDPOINT_URL}" > /dev/null 2>&1; then
  echo "floci nie odpowiada na ${AWS_ENDPOINT_URL} — uruchom 'docker-compose up -d floci' i spróbuj ponownie." >&2
  exit 1
fi

echo "==> Tworzę klaster EKS (${CLUSTER_NAME}) na floci..."
aws eks create-cluster \
  --name "${CLUSTER_NAME}" \
  --role-arn arn:aws:iam::000000000000:role/eks-service-role \
  --resources-vpc-config subnetIds=subnet-fake1,subnet-fake2

echo "==> Aktualizuję kubeconfig..."
aws eks update-kubeconfig --name "${CLUSTER_NAME}"

echo "==> Sprawdzam klaster..."
kubectl get nodes
kubectl get namespaces

echo "==> Tworzę bucket S3 na media..."
aws s3 mb "s3://webstaurator-media" || true

echo "==> Wdrażam Webstauratora..."
kubectl apply -f k8s/namespaces/webstaurator.yaml
kubectl apply -f k8s/infrastructure/
kubectl apply -f k8s/services/
kubectl apply -f k8s/ingress/

echo "==> Gotowe. Sprawdź stan: kubectl get events -n webstaurator --sort-by=.lastTimestamp"
