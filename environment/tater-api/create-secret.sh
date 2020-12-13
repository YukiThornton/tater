#!/bin/bash

NAMESPACE=$1
PROPERTY_FILE=$2

kubectl -n $NAMESPACE create secret generic tater-secret --from-file=app.properties=$PROPERTY_FILE