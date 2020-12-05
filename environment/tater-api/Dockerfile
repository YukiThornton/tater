# syntax = docker/dockerfile:1.1-experimental

FROM maven:3.6.3-jdk-11-slim AS build-env
WORKDIR /work
ADD ./pom.xml /work/
RUN --mount=type=cache,target=/root/.m2 mvn dependency:go-offline
ADD . /work/
RUN --mount=type=cache,target=/root/.m2 mvn package -Dmaven.clean.skip=true

FROM openjdk:11-jre-slim
COPY --from=build-env \
     /work/target/tater-api-standalone.jar \
     /usr/local/tater-api/
EXPOSE 80
CMD ["java", "-jar", "/usr/local/tater-api/tater-api-standalone.jar"]