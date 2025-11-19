# Build stage
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
COPY mvnw pom.xml .
COPY .mvn .mvn
COPY src src
RUN ./mvnw -DskipTests package

# Run stage
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/catalog-api-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENV SPRING_PROFILES_ACTIVE=postgres
ENTRYPOINT ["java","-jar","/app/app.jar"]
