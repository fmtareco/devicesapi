FROM eclipse-temurin:25-jdk-jammy
ARG JAR_FILE=target/devicesapi-0.0.3-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]