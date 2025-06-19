# Use the latest Ubuntu LTS as the base image
FROM ubuntu:24.04

# Set environment variables to avoid interactive prompts
ENV DEBIAN_FRONTEND=noninteractive

# Update and install basic packages
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
        ca-certificates \
        curl \
        wget \
        vim \
        nano \
        git \
        openjdk-21-jdk

EXPOSE 8080

COPY target/meng-1.0.0.jar /home/ubuntu/meng-1.0.0.jar

# Set the script as the entrypoint
ENTRYPOINT ["java", "-jar", "/home/ubuntu/meng-1.0.0.jar"]