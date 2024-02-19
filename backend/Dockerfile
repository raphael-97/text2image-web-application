FROM maven:3.9.6-eclipse-temurin-21-alpine as build

WORKDIR /app

COPY pom.xml .
COPY src src
RUN mvn clean verify -DskipTests

FROM eclipse-temurin:21.0.2_13-jre-alpine

WORKDIR /app

COPY --from=build /app/target/BackendStableDiffusionWebApp-0.0.1-SNAPSHOT.jar .
COPY --from=build /app/src/main/resources/static/* src/main/resources/static/

ENTRYPOINT ["java","-jar","BackendStableDiffusionWebApp-0.0.1-SNAPSHOT.jar"]