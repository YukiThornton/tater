#!/bin/bash

IMAGE_NAME="tater-db"
IMAGE_TAG=${1:-'latest'}

docker build --no-cache --rm -t $IMAGE_NAME:$IMAGE_TAG .
