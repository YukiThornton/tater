FROM maven:3.6.3-jdk-11 AS build
ADD . /work
WORKDIR /work
RUN --mount=type=cache,target=/root/.m2 mvn clean package

FROM openjdk:11-jre-slim
COPY --from=build \
     /work/target/tater-api-jar-with-dependencies.jar \
     /usr/local/tater-api/
EXPOSE 80
CMD ["java", "-jar", "/usr/local/tater-api/tater-api-jar-with-dependencies.jar"]