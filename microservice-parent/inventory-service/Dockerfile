# --------
# Dockerfile for product-service
# --------

# ----
# Build stage
# ----

# base image
FROM gradle:8-jdk17-alpine AS builder

#copy the application files from host machine to the image filesystem
COPY --chown=gradle:gradle . /home/gradle/src

# set the working directory, for executing future commands
WORKDIR /home/gradle/src

#run the gradle build within the container
RUN gradle build -x test

# ----
# Package Stage
# ----

FROM openjdk:17-jdk-alpine

RUN mkdir /app

COPY --from=builder /home/gradle/src/build/libs/*.jar /app/inventory-service.jar

ENV POSTGRES_USER=admin \
    POSTGRES_PASSWORD=password

EXPOSE 8085

ENTRYPOINT ["java", "-jar", "/app/inventory-service.jar"]

