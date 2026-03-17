# Mini Marketplace - Full Stack (API + Multi-Page Web UI)

Mini Marketplace includes:
- Spring Boot backend REST APIs (auth, products, orders, admin)
- Multi-page frontend served by Spring Boot static resources

## Features
- Register and login with JWT
- Product catalog and search
- Cart and checkout using pricing strategy selection (Strategy pattern)
- Gift wrap and express shipping options per item (Decorator pattern)
- Order tracking for customers
- Admin product CRUD and order status updates (Observer notifications are triggered on status updates)

## Tech Stack
- Backend: Spring Boot, Spring Security, Spring Data JPA, Flyway
- Frontend: HTML, CSS, JavaScript (ES modules)
- Database: PostgreSQL

## Run Locally (PowerShell)
```powershell
Set-Location "D:\3.2\SWE\lab\Project\mini-marketplace"
.\mvnw.cmd -DskipTests package
.\mvnw.cmd spring-boot:run
```

## Frontend URLs
- Home / Catalog: `http://localhost:8080/`
- Login: `http://localhost:8080/login`
- Register: `http://localhost:8080/register`
- Cart: `http://localhost:8080/cart`
- Orders: `http://localhost:8080/orders`
- Admin: `http://localhost:8080/admin`

## API Docs
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI: `http://localhost:8080/v3/api-docs`

## Seed Accounts
(from `src/main/resources/db/migration/V2__seed_data.sql`)
- Admin: `admin@market.com` / `admin123`
- User: `alice@market.com` / `password123`
- User: `bob@market.com` / `password123`

## Frontend Source Files
- Pages: `src/main/resources/static/*.html`
- Styles: `src/main/resources/static/css/site.css`
- Scripts: `src/main/resources/static/js/*.js`

## Push to GitHub
```powershell
Set-Location "D:\3.2\SWE\lab\Project\mini-marketplace"
git add .
git commit -m "Convert storefront to proper multi-page mini marketplace UI"
git push origin <your-branch>
```
