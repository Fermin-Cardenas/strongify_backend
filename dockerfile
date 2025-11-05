FROM eclipse-temurin:17-jdk-alpine
COPY target/gymapp-backend-v2-0.0.1-SNAPSHOT.jar java-app.jar
ENTRYPOINT ["java", "-jar", "java-app.jar"]
