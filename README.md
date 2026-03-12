# Mini Marketplace — CSE 3220 Software Engineering Lab Project

## 🏗️ Tech Stack
- **Backend:** Spring Boot 3.2, Java 17
- **Database:** PostgreSQL 16 + Flyway migrations
- **Auth:** JWT (jjwt) + Spring Security
- **Docs:** SpringDoc OpenAPI (Swagger UI)
- **Testing:** JUnit 5, Mockito, H2, Testcontainers
- **DevOps:** Docker, GitHub Actions CI/CD, Render

## 📦 Prerequisites
- Java 17+
- Maven 3.9+
- Docker & Docker Compose
- PostgreSQL 16 (or use Docker)

## 🚀 Quick Start

### 1. Clone & Run with Docker Compose
```bash
git clone https://github.com/AsifJawad15/CSE3220-Software_Engineering.git
cd CSE3220-Software_Engineering
docker compose up --build
```
App runs at: `http://localhost:8080`  
Swagger UI: `http://localhost:8080/swagger-ui/index.html`

### 2. Run Locally (without Docker)
```bash
# Start PostgreSQL (ensure it's running on port 5432)
# Update application-dev.yml with your DB credentials

mvn clean install
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### 3. Run Tests
```bash
mvn clean test
```

## 📐 Design Patterns
- **Observer:** Order event notifications (inventory, email, analytics)
- **Strategy:** Pricing/discount strategies (no discount, percentage, bulk)
- **Decorator:** Order enhancements (gift wrap, express shipping)

## 🔐 API Authentication
1. Register: `POST /api/auth/register`
2. Login: `POST /api/auth/login` → returns JWT token
3. Use token: `Authorization: Bearer <token>` header

## 👥 Team
- **ASIF** — Auth, Security, CI/CD, Docker
- **SALEK** — Products, Orders, Deployment, DB Schema

## 📄 License
Academic project — CSE 3220, Spring 2026

