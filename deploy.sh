#!/bin/bash
set -e

echo "Running gradlew bootJar"
./gradlew clean bootJar

echo "Building Docker image..."
docker buildx build --load -t kuuku123/auth-service:latest .

if docker info 2>/dev/null | grep -q "Username"; then
  echo "Pushing Docker image..."
  docker push kuuku123/auth-service:latest
else
  echo "Not logged in. Skipping push."
fi

echo "Stopping existing containers..."
docker compose -f deploy.yml down

echo "Starting services..."
docker compose -f deploy.yml up -d

echo "Deployment complete!"
