# Devices API — README

## 1. Instructions

1. Go to the application's main folder (where `pom.xml`, `Dockerfile`, and `docker-compose.yml` are located).

2. Build the API JAR:
   ```bash
   mvn clean install -DskipTests
   ```
   This avoids running tests before the environment is fully set up.

3. Build the API Docker image:
   ```bash
   docker build -t fmtareco/devices-api .
   ```
   This creates the image `fmtareco/devices-api`.

4. Launch the containers:
   ```bash
   docker compose up -d
   ```

5. Using an API testing platform (e.g., Postman), test the main endpoint:
   ```
   http://localhost:8080/api/devices
   ```
   Try different HTTP methods to validate all API operations.

---

## 2. Project Overview

This project provides a REST API for managing *Device* resources, supporting full CRUD operations and filtering by brand and state. It uses **Java 21**, **Spring Boot**, **PostgreSQL**, and supports containerized deployment via Docker.

---

## 3. Persistence Layer

### Database
- PostgreSQL is used as the persistence layer.
- Default configuration is found in `application.yaml`.

### Persistence
- The application relies on Hibernate’s auto-ddl (`spring.jpa.hibernate.ddl-auto=update`).

---

## 4. Business Logic & Validations

The API enforces domain rules:
- `creationTime` cannot be updated.
- `name` and `brand` **cannot** be updated if the device is in `in-use` state.
- Devices in `in-use` state **cannot** be deleted.

All business logic is isolated in the **Service Layer** and validated through **unit tests (Mockito)**.

---

## 5. Logging

The application uses **JSON logging** for production-ready observability.

Log fields include timestamp, level, message and logger.

---

## 6. API Documentation (Swagger / OpenAPI)

Swagger UI is auto-generated using **springdoc-openapi**.

### How to access it
Once the application is running, visit:
```
http://localhost:8080/swagger-ui.html
```


### How OpenAPI documentation is generated


To access the OpenAPI JSON:
```
http://localhost:8080/v3/api-docs 
```

---

## 7. Testing

### Integration Tests
- Use RestClient to issue API calls.
- Real PostgreSQL instance is used.
- Tests API endpoints end‑to‑end.

---

## 8. Running in Docker

Docker Compose includes:
- devices-api service
- PostgreSQL database
- pgAdmin to monitor/manage the database

To rebuild after changes:
```bash
docker build -t fmtareco/devices-api .
docker compose up -d 
```

---

## 9. Repository Structure
```
├── src
│   ├── main
│   │   ├── java
│   │   ├─── com.example.devicesapi
│   │   ├──── controllers
│   │   ├──── dtos
│   │   ├──── entities
│   │   ├──── exceptions
│   │   ├──── repository
│   │   ├──── services
│   │   ├── resources
│   │   └─── application.yaml
│   │   └─── logback-spring.xml
│   │   └─── banner.txt
│   ├── test
│   │   ├── java
│   │   ├─── com.example.devicesapi
│   │   ├──── controllers
├── Dockerfile
├── docker-compose.yml
├── README.md
├── pom.xml
├── actuator-health.jpg
├── first execution with mocked values.jpg
├── github-repo.jpg
├── open api docs.jpg
├── spring-initializer.jpg
├── swagger-ui.jpg

```

---

## 10. API Endpoints Summary

```
Method	Endpoint	               Description
- GET	   /api/devices	                Fetch all devices
- GET	   /api/devices/{id}	        Fetch a device by id
- GET	   /api/devices/brand/{brand}	Filter by brand
- GET	   /api/devices/state/{state}	Filter by state
- POST	   /api/devices	                Create a device
- PUT	   /api/devices/{id}	        Fully update a device
- PATCH	   /api/devices/{id}	        Partially update a device
- DELETE   /api/devices/{id}	        Delete a device

```


