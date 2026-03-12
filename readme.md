# Spring Boot Microservices Course 2025

## Prerequisites
**IMPORTANT:** Before running the project, make sure to have Docker installed on your machine.

## Environment Setup
Create a `.env` file in the root directory and add your database credentials following this format:

```
POSTGRES_USER=******
POSTGRES_PASSWORD=*****
POSTGRES_DB=******
```

## Docker Commands

### Starting and Stopping the Project
- `docker-compose down` - Stop and remove containers, networks, volumes, and images created by `up`
- `docker-compose up` - Build, create, start, and attach to containers for a service

### Building the Project
- `docker-compose up --build` - Rebuild the project after making changes to the code

### Rebuilding a Specific Service
- `docker-compose up --build item-service` - Example to rebuild only the item-service

**Note:** Up Gateway after building the project!!

## Debugging
You can debug the code by creating remote debug servers. Follow these steps:

1. Go to **Run > Edit Configurations**.
2. Click **+** and select **Remote JVM Debug**.
3. Set the **Host** to `localhost` and the **Port** to the debug port of the service you want to debug (e.g., 5005 for product-es, 5006 for item-service, etc.).
