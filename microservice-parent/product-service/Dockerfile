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

COPY --from=builder /home/gradle/src/build/libs/*.jar /app/product-service.jar

ENV MONGO_DB_USERNAME =rootadmin MONGO_DB_PWD =password

EXPOSE 8084

ENTRYPOINT ["java", "-jar", "/app/product-service.jar"]


