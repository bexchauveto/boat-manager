FROM gradle:8.2.1-jdk17-alpine AS cache
WORKDIR /app
ENV GRADLE_USER_HOME /cache
COPY build.gradle settings.gradle ./
RUN gradle --no-daemon build --stacktrace

FROM gradle:8.2.1-jdk17-alpine AS builder
WORKDIR /app
COPY --from=cache /cache /home/gradle/.gradle
COPY build.gradle .
COPY settings.gradle .
COPY src/ src/
RUN gradle --no-daemon build --stacktrace --scan

FROM openjdk:17-alpine
WORKDIR /app
COPY --from=builder /app/build/libs/boat-manager-1.0.0.jar app.jar
COPY /src/main/resources/application.yml .
EXPOSE 8080
CMD ["java", "-jar", "-Dspring.profiles.active=prod", "app.jar"]