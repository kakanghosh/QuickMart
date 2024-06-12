# QuickMart Application Docker Setup

This guide provides instructions on how to build and run your QuickMart application using Docker and Docker Compose.

## Prerequisites

- Maven 3.6+ and Java 17
- Docker installed on your machine
- Docker Compose installed on your machine

## Steps to Build and Run the Project

### 1. Build and Package the project

First, you need to package the application jar. Then we will use this jar to build the docker image.
Make sure you are in the root directory.

```
mvn clean package -DskipTests=true
```

### 2. Build the Docker Image

Next, you need to build the Docker image for your application.
Make sure you are in the root directory of
your project (where the Dockerfile is located) and run the following command:

```
docker build -f Dockerfile-slim -t quickmart:PUT_YOUR_TAG .
Example: 
docker build -f Dockerfile-slim -t quickmart:2024-06-12-23-35 .
```

### 3. Update image tag docker compose file

Next, in docker compose file update the newly build image tag for quickmart service.

```
Example:
image: quickmart:2024-06-12-23-35
```

### 4. Run docker compose file

Finally, run the docker compose command to start the application

```
docker compose up
```
