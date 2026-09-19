# Webstaurator Backend

Backend service for the Webstaurator platform.

Built with Spring Boot 3.3.2, Java 21, and Maven.

Includes OpenAPI UI support via SpringDoc.

---

## ⚙️ Requirements
- Java 21 (JDK)
- Maven 3.8+
- Docker & Docker Compose (for local development)

---

## 🚀 Getting Started

### Clone the Repository
```
git clone https://github.com/Gosqu248/Webstaurator.git
cd Webstaurator/backend
```

### Run the Application
Using Maven Wrapper:
```
./mvnw spring-boot:run
```
Or with system Maven:
```
mvn spring-boot:run
```

### Build and Run as a JAR
```
mvn clean package
java -jar target/backend-0.0.1-SNAPSHOT.jar
```

---

## ✅ Running Tests
```
mvn test
```
Output:
```
BUILD SUCCESS
```
If not — fix it. No excuses.

---

## 📖 API Documentation
Access Swagger UI at:
- http://localhost:8080/swagger-ui.html

---

## 📦 Maven Coordinates
- **Group ID:** pl.urban
- **Artifact ID:** backend
- **Version:** 0.0.1-SNAPSHOT

---

## 🧱 Tech Stack
- Spring Boot Starter
- Spring Boot Starter Web
- Spring Boot Starter Data JPA
- Spring Boot Starter Security
- Spring Boot Starter Mail
- Spring Boot Starter Actuator
- Spring Boot Starter OAuth2 Client
- SpringDoc OpenAPI
- PostgreSQL Driver
- Flyway Core & Flyway PostgreSQL
- Lombok
- OkHttp
- Jackson Databind
- JWT (io.jsonwebtoken)
- Jakarta Mail
- Groovy
- JUnit Jupiter
- Mockito

---

## 🐳 Docker Support
Build and run with Docker:
```
docker build -t backend .
docker run -p 8080:8080 backend
```

Or use docker-compose (from project root):
```
docker-compose up
```

---

## 🔒 Security
- JWT-based authentication
- Two-factor authentication (2FA)
- Password encryption
- Role-based access control

---

## 📧 Email Service
- Registration confirmations
- Password reset links
- Order notifications

---

## 🗄️ Database
- PostgreSQL 17 (via Docker Compose)
- Flyway for migrations (see `src/main/resources/db/migration`)

---

## 🔍 Monitoring
- Logging via `application.yml`
- Spring Boot Actuator endpoints

