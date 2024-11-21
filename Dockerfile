FROM openjdk:11-jdk-slim

# Set environment variables for OpenCV
ENV OPENCV_VERSION=4.9.0-0

# Install dependencies
RUN apt-get update && \
    apt-get install -y \
    maven 

# Copy Java application files
WORKDIR /app
COPY pom.xml /app/pom.xml
COPY src /app/src

# Compile Java application
RUN mvn clean install

# Copy the final application jar
COPY target/Aura-1.0-SNAPSHOT.jar /app/Aura-1.0-SNAPSHOT.jar

# Run the Java application
CMD ["java", "-Djava.library.path=/usr/local/lib", "-jar", "/app/Aura-1.0-SNAPSHOT.jar"]
