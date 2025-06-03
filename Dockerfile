# === Build stage ===
FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY . .
RUN mvn clean package spring-boot:repackage -DskipTests

# === Run stage ===
FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=builder /app/target/DictionaryBot-0.0.1-SNAPSHOT.jar /app/app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar", "--db.init.json-file=en_ru_dict.json"]
