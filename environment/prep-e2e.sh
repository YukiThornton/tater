#!/bin/bash

NAMESPACE=tater-e2e

kubectl apply -f namespace-e2e.yml

kubectl apply -n $NAMESPACE -f tater-api/service-template.yml
kubectl apply -n $NAMESPACE -f wiremock/service-template.yml
kubectl apply -n $NAMESPACE -f tater-db/service-template.yml

kubectl apply -n $NAMESPACE -f tater-api/deployment-template.yml
kubectl apply -n $NAMESPACE -f wiremock/deployment-template.yml
kubectl apply -n $NAMESPACE -f tater-db/deployment-template.yml

while [ $(kubectl get pods -n tater-e2e --field-selector=status.phase=Running --output json | jq '.items | length') -ne 3 ]
do
    sleep 1
done

echo 'All pods are running'

kubectl -n $NAMESPACE port-forward service/tater-api-svc 18000:18000 &
kubectl -n $NAMESPACE port-forward service/wiremock-svc 18001:8080 &
kubectl -n $NAMESPACE port-forward service/tater-db-svc 18002:5432