FROM maven:3.9-eclipse-temurin-25 AS builder
WORKDIR /workspace

COPY pom.xml ./
COPY .mvn .mvn
COPY mvnw ./
COPY src ./src

RUN chmod +x mvnw && ./mvnw -B -DskipTests clean package

FROM eclipse-temurin:25-jre
WORKDIR /app

COPY --from=builder /workspace/target/backend-0.0.1-SNAPSHOT.jar /app/app.jar

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java -Dserver.port=${PORT:-8080} -jar /app/app.jar"]