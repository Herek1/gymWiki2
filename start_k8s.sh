#!/bin/bash
eval $(minikube docker-env)

echo "=== Starting Minikube ==="
minikube start

echo "=== Enabling Docker inside Minikube ==="
eval $(minikube docker-env)

echo "=== Building Docker images ==="
docker build -t gymwiki2_server:latest ./Server
docker build -t gymwiki2_client:latest ./Client

echo "=== Deploying Kubernetes manifests ==="
kubectl apply -f ./k8s

echo "=== Waiting for pods to be ready ==="
kubectl wait --for=condition=ready pod -l app=client --timeout=60s
kubectl wait --for=condition=ready pod -l app=server --timeout=60s

echo "=== Launching client in browser ==="
URL=$(minikube service client --url)
echo "🔗 Opening: $URL/Client/site"
xdg-open "$URL/Client/site"

