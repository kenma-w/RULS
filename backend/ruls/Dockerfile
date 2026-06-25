# Use an official JDK 17 runtime as a parent image
FROM openjdk:17-jdk-slim

# Install Maven
RUN apt-get update && apt-get install -y maven

# Set the working directory in the container
WORKDIR /app

# Copy the current directory contents into the container at /app
COPY . /app

# Package the application as a JAR file
RUN mvn clean package -DskipTests

# Run the JAR file
CMD ["java", "-jar", "target/ruls-0.0.1-SNAPSHOT.jar"]

# Expose the application port
EXPOSE 8081
