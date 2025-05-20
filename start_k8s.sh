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
minikube service client

echo "open manually: http://192.168.49.2:30783/Client/site"
