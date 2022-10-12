FROM openjdk:17-jdk-slim-buster
WORKDIR /app

COPY build/libs/Worker-0.0.1-SNAPSHOT.jar build/

WORKDIR /app/build
ENTRYPOINT java -jar Worker-0.0.1-SNAPSHOT.jar