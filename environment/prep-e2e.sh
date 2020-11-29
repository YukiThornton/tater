#!/bin/bash

NAMESPACE=tater-e2e

kubectl apply -f namespace-e2e.yml

kubectl apply -n $NAMESPACE -f wiremock/service-template.yml
kubectl apply -n $NAMESPACE -f tater-db/service-template.yml

kubectl apply -n $NAMESPACE -f wiremock/deployment-template.yml
kubectl apply -n $NAMESPACE -f tater-db/deployment-template.yml

kubectl -n $NAMESPACE port-forward service/wiremock-svc 18001:8080 &
kubectl -n $NAMESPACE port-forward service/tater-db-svc 18002:5432