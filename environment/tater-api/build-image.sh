#!/bin/bash

IMAGE_NAME="tater-api"
IMAGE_TAG=${1:-'latest'}

DOCKER_BUILDKIT=1 docker build --rm -t $IMAGE_NAME:$IMAGE_TAG -f Dockerfile ../../tater-api
