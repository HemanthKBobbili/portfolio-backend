# ==========================
# 1. Build stage
# ==========================
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy only the necessary files to cache dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Now copy the source and build the JAR
COPY src ./src
RUN mvn clean package -DskipTests

# ==========================
# 2. Runtime stage
# ==========================
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app

# Copy the JAR built in the previous stage
COPY --from=build /app/target/*.jar app.jar

# Expose port (same as application.yml)
EXPOSE 8080

# Environment variables (you can override them via .env)
ENV SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/portfolio_db \
    SPRING_DATASOURCE_USERNAME=postgres \
    SPRING_DATASOURCE_PASSWORD=postgres \
    JWT_SECRET=HemanthPortfolioSecretKeySpringBoot2025Secure

# Run the Spring Boot app
ENTRYPOINT ["java", "-jar", "app.jar"]
