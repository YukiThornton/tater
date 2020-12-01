#!/bin/bash

IMAGE_NAME="tater-api"
IMAGE_TAG=${1:-'latest'}

docker build -t $IMAGE_NAME:$IMAGE_TAG -f Dockerfile ../../tater-api
