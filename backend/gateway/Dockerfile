# ============================================================================
# gateway :: build multi-stage (contexto = backend/, reator multi-módulo)
# ============================================================================

# --- Stage 1: build ---
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml .
COPY gateway/pom.xml gateway/pom.xml
RUN mvn -B -ntp -pl gateway -am dependency:go-offline

COPY gateway/src gateway/src
RUN mvn -B -ntp -pl gateway -am clean package -DskipTests

# --- Stage 2: runtime ---
FROM eclipse-temurin:21-jre AS runtime
WORKDIR /app

RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/* \
    && useradd --system --uid 1001 spp
USER spp

COPY --from=build /app/gateway/target/*.jar app.jar

EXPOSE 8090

HEALTHCHECK --interval=30s --timeout=5s --start-period=40s --retries=3 \
    CMD curl -f http://localhost:8090/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
