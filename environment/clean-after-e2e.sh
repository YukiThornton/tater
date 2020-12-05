#!/bin/bash

NAMESPACE=tater-e2e

kill $(ps aux | grep kubectl | grep port-forward | awk '{print $2}')

kubectl -n $NAMESPACE delete service tater-api-svc
kubectl -n $NAMESPACE delete service wiremock-svc
kubectl -n $NAMESPACE delete service tater-db-svc

kubectl -n $NAMESPACE delete deploy tater-api
kubectl -n $NAMESPACE delete deploy wiremock
kubectl -n $NAMESPACE delete deploy tater-db

while [ $(kubectl get pods -n tater-e2e --field-selector=status.phase=Running --output json | jq '.items | length') -ne 0 ]
do
    sleep 2
done

echo 'Cleaned up!'

