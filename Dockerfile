# Stage 1: Build Angular frontend
FROM node:22-alpine AS frontend-builder

WORKDIR /app/frontend

# Copy frontend files
COPY frontend/package.json frontend/bun.lock* ./

# Install dependencies using bun
RUN npm install -g bun && bun install

# Copy frontend source code
COPY frontend/ .

# Build Angular app (production config includes baseHref: /ui/)
RUN bun run build -- --configuration=production

# Stage 2: Build Spring Boot backend with Maven
FROM maven:3.9-eclipse-temurin-25 AS backend-builder

WORKDIR /app/backend

# Copy backend files
COPY backend/pom.xml .

# Download dependencies
RUN mvn dependency:resolve

# Copy backend source code
COPY backend/ .

# Inject Angular build into Spring Boot static resources before packaging
RUN mkdir -p src/main/resources/static/ui
COPY --from=frontend-builder /app/frontend/dist/frontend/browser/ ./src/main/resources/static/ui/

# Build without tests
RUN mvn clean package -DskipTests

# Stage 3: Runtime image (minimal JRE)
FROM eclipse-temurin:25-jre-alpine

WORKDIR /app

# Copy the final JAR from builder
COPY --from=backend-builder /app/backend/target/projectName.jar ./project-name.jar

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Expose port
EXPOSE 8080

# Start the application
ENTRYPOINT ["java", "-jar", "project-name.jar"]
