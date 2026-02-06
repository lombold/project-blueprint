# Stage 1: Build Angular frontend
FROM node:22-alpine AS frontend-builder

WORKDIR /app/frontend

# Copy frontend files
COPY frontend/package.json frontend/bun.lock* ./

# Install dependencies using bun
RUN npm install -g bun && bun install

# Copy frontend source code
COPY frontend/ .

# Build Angular app
RUN bun run build

# Stage 2: Build Spring Boot backend with Maven
FROM maven:3.9-eclipse-temurin-25 AS backend-builder

WORKDIR /app/backend

# Copy backend files
COPY backend/pom.xml .

# Download dependencies
RUN mvn dependency:resolve

# Copy backend source code
COPY backend/ .

# Build without tests
RUN mvn clean package -DskipTests

# Stage 3: Combine Angular build with backend resources
FROM maven:3.9-eclipse-temurin-25 AS app-builder

WORKDIR /app

# Copy the built backend JAR
COPY --from=backend-builder /app/backend/target/gym-buddy.jar ./backend.jar

# Copy the built Angular app
COPY --from=frontend-builder /app/frontend/dist/frontend /app/frontend-dist

# Create a new build to package everything together
WORKDIR /app/packaging
RUN jar xf ../backend.jar

# Copy Angular build to static resources
RUN mkdir -p BOOT-INF/classes/static/ui && \
    cp -r /app/frontend-dist/* BOOT-INF/classes/static/ui/

# Recreate the JAR with Angular assets
RUN jar cfm gym-buddy.jar META-INF/MANIFEST.MF -C . .

# Stage 4: Runtime image (minimal JRE)
FROM eclipse-temurin:25-jre-alpine

WORKDIR /app

# Copy the final JAR from builder
COPY --from=app-builder /app/packaging/gym-buddy.jar .

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/api/v1/health || exit 1

# Expose port
EXPOSE 8080

# Start the application
ENTRYPOINT ["java", "-jar", "gym-buddy.jar"]
