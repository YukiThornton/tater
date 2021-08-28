# syntax = docker/dockerfile:1.1-experimental

FROM node:lts-alpine AS build-env
WORKDIR /work
COPY package.json ./
COPY package-lock.json ./
RUN npm install
COPY . ./
RUN npm run build

FROM nginx:mainline
COPY --from=build-env /work/public /usr/share/nginx/html
COPY nginx-server.conf /etc/nginx/conf.d/default.conf
