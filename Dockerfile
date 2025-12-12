#
# Multi-stage build for Spring Boot app (Java 21)
#

# ---- Build stage ----
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /app

# Leverage Docker layer cache: first copy pom.xml and download deps
COPY pom.xml ./
RUN mvn -q -B -e -DskipTests dependency:go-offline

# Copy sources and build
COPY src ./src
RUN mvn -q -B -e -DskipTests clean package

# ---- Runtime stage ----
FROM eclipse-temurin:21-jre
WORKDIR /app

# Add non-root user for security
RUN useradd -u 10001 -r -s /sbin/nologin spring

# Copy built jar
COPY --from=builder /app/target/*.jar /app/app.jar

# App listens on 8081 (see server.port in application.properties)
EXPOSE 8081

ENV JAVA_OPTS=""
ENV SPRING_PROFILES_ACTIVE=prod

USER spring
ENTRYPOINT ["sh","-c","exec java $JAVA_OPTS -jar /app/app.jar"]