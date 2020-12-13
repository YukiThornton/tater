#!/bin/bash

NAMESPACE=tater-prd

kubectl apply -n $NAMESPACE -f tater-api/service-template.yml
kubectl apply -n $NAMESPACE -f tater-db/service-template.yml

kubectl apply -n $NAMESPACE -f tater-api/deployment-template.yml
kubectl apply -n $NAMESPACE -f tater-db/deployment-template.yml

while [ $(kubectl get pods -n tater-prd --field-selector=status.phase=Running --output json | jq '.items | length') -ne 2 ]
do
    sleep 1
done

echo 'All pods are running'

kubectl -n $NAMESPACE port-forward service/tater-api-svc 19000:18000 &
kubectl -n $NAMESPACE port-forward service/tater-db-svc 19002:5432