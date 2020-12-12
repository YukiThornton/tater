#!/bin/bash

kubectl -n tater-e2e create secret generic tater-secret --from-file=app.properties=$1