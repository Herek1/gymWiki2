#!/bin/bash
eval $(minikube docker-env -u)

set -e  # Exit immediately if a command fails
echo "=== Cleaning and building Maven projects ==="

echo "-> Building Server"
cd Server
mvn clean package

echo "-> Building Client"
cd ../Client
mvn clean package

echo "=== Building Docker images ==="
cd ..
docker-compose build

echo "=== Starting containers ==="
docker-compose up
