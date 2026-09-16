FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /workspace
COPY pom.xml .
COPY discovery-server/pom.xml discovery-server/pom.xml
COPY src src
COPY discovery-server/src discovery-server/src
RUN mvn -B -f discovery-server/pom.xml -DskipTests package

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /workspace/discovery-server/target/discovery-server-1.0.0-SNAPSHOT.jar app.jar
EXPOSE 8761
ENTRYPOINT ["java", "-jar", "app.jar"]
