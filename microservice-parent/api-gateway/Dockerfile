FROM gradle:8-jdk17-alpine AS builder

# COPY the application files from the host machine to the image filesystem
COPY --chown=gradle:gradle . /home/gradle/src

# Set the directory within the image, for executing future commands
WORKDIR /home/gradle/src

# Run gradle build within the container - NOT the host machine
# for RUN commands, the command only apply to images
# RUN gradle build --no-daemon -x test -- this did NOT WORK
RUN gradle build -x test

# ---------------
# Package Stage
# ---------------
# Base Image
FROM openjdk:17-jdk-alpine

RUN mkdir /app

COPY --from=builder /home/gradle/src/build/libs/*.jar /app/api-gateway.jar

EXPOSE 8181

# Execute the following 1 command after the container starts
CMD ["java", "-jar", "app/api-gateway.jar"]
ENTRYPOINT ["java", "-jar", "/app/api-gateway.jar"]