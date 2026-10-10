# Stage 1: build the application with Maven and JDK 26
FROM eclipse-temurin:26-jdk-noble AS build
WORKDIR /app

# Download dependencies first so this layer is cached while only the sources change
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN ./mvnw -B -q dependency:go-offline

COPY src/ src/
RUN ./mvnw -B -q -DskipTests package

# Stage 2: run the executable JAR on a JRE 26 image
FROM eclipse-temurin:26-jre-noble
WORKDIR /app

RUN groupadd --system serenia && useradd --system --gid serenia --home-dir /app serenia \
    && chown serenia:serenia /app
COPY --from=build --chown=serenia:serenia /app/target/platform-*.jar app.jar
USER serenia

# The production profile reads every setting from environment variables
ENV SPRING_PROFILES_ACTIVE=prod
EXPOSE 8080

ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-jar", "app.jar"]
