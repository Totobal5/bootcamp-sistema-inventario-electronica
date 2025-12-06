# Multi-stage build optimized for Railway

# Stage 1: Build with Maven dependency caching
FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Copy only pom.xml first to cache dependencies
COPY pom.xml .

# Download all dependencies (this layer will be cached unless pom.xml changes)
RUN mvn dependency:go-offline -B

# Now copy source code
COPY src ./src

# Build the application - use batch mode and quiet for faster output
RUN mvn clean package -DskipTests -B -q

# Stage 2: Runtime - smaller image
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Create non-root user for security
RUN addgroup -S spring && adduser -S spring -G spring

# Copy JAR from build stage
COPY --from=build /app/target/*.jar app.jar

# Create uploads directory with correct permissions
RUN mkdir -p /app/uploads && chown -R spring:spring /app

# Switch to non-root user
USER spring:spring

# Expose port (Railway uses 8080 by default)
EXPOSE 8080

# Optimized JVM settings for Railway (512MB limit on free tier)
ENV JAVA_TOOL_OPTIONS="-Xmx512m -Xms256m -XX:MaxMetaspaceSize=128m -XX:+UseContainerSupport"

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Start application
ENTRYPOINT ["java", "-jar", "app.jar"]
