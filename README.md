<<<<<<< HEAD
# NEXORA E-Commerce

Full-stack e-commerce learning project.

**Stack:** React 18 (JavaScript, Vite, React Router, Axios, Context API, plain CSS) ·
Java 21 · Spring Boot 3.5 · Spring Data JPA/Hibernate · Spring Security + JWT ·
Maven · MySQL 8 · JUnit 5 · Mockito · Swagger/OpenAPI · Postman

## Project structure

```
nexora-ecommerce/
├── backend/                  Spring Boot REST API (port 8080)
│   ├── pom.xml
│   └── src/main/java/com/nexora/ecommerce/
│       ├── controller/       REST endpoints
│       ├── service/ + impl/  Business logic
│       ├── repository/       Spring Data JPA
│       ├── entity/           JPA entities (tables)
│       ├── dto/              Request/response records
│       ├── mapper/           Entity -> DTO
│       ├── security/         JWT + Spring Security
│       ├── exception/        Custom exceptions + global handler
│       ├── config/           Swagger, sample data seeder
│       └── util/             Price calculator, helpers
├── frontend/                 React app (port 5173)
│   └── src/ components, pages, pages/admin, context, hooks, services, routes, utils
└── docs/                     Architecture, API map, business rules, Postman collection
```

## Prerequisites

| Tool | Version |
|---|---|
| JDK | 21 |
| Maven | 3.9+ (or use the one bundled with IntelliJ IDEA) |
| Node.js | 20+ |
| MySQL Server | 8.x |

## 1. Start MySQL

Make sure MySQL is running. The database `nexora_db` is created automatically on first start.

## 2. Run the backend

```bash
cd backend
cp src/main/resources/application-local.properties.example src/main/resources/application-local.properties
# edit application-local.properties -> set your MySQL password
mvn spring-boot:run
```

Or open `backend/` in IntelliJ IDEA and run `NexoraEcommerceApplication`.

Expected log lines:
```
Sample data created: 2 users, 7 categories, 23 products
Tomcat started on port 8080 (http)
Started NexoraEcommerceApplication in ... seconds
```

Check it works:
- Health: http://localhost:8080/actuator/health → `{"status":"UP"}`
- Products: http://localhost:8080/api/products
- **Swagger UI:** http://localhost:8080/swagger-ui.html
  (use `POST /api/auth/login`, copy the `token`, click **Authorize**, paste it)

> No Maven wrapper is included. Use IntelliJ's bundled Maven, install Maven,
> or generate a wrapper once with `mvn wrapper:wrapper`.

## 3. Run the frontend

```bash
cd frontend
npm install
npm run dev
```

Open http://localhost:5173. The Vite dev server forwards `/api` to `http://localhost:8080`.

## Sample accounts (development only)

| Role | Email | Password |
|---|---|---|
| Customer | user@nexora.com | User@123 |
| Admin | admin@nexora.com | Admin@123 |

These are created by `DataSeeder` only when the users table is empty.
Change/remove them (or set `app.seed.enabled=false`) before any real use.

## Simulated payments

No real payment gateway is used.

| Card number | Result |
|---|---|
| 4111 1111 1111 1111 (any not ending in 0000) | Success |
| 4111 1111 1111 0000 | Declined (HTTP 402, order not created, cart kept) |

Any future expiry (MM/YY) and any 3-digit CVV. Only the last 4 digits are stored; CVV/expiry are never sent to the server.
Cash on delivery (COD) is also available.

## Business rules (summary)

- Tax 10%, shipping ₹50, free shipping when the discounted subtotal is above ₹5,000
  (see `app.pricing.*` in `application.properties`).
  Example: subtotal ₹2,000 − discount ₹200 + tax ₹180 + shipping ₹50 = **₹2,030**.
- Stock is checked when adding to cart **and** again at checkout; checkout is one transaction.
- Max 10 units of one product per cart.
- Orders: PENDING → CONFIRMED → PROCESSING → SHIPPED → DELIVERED; cancel allowed before SHIPPED (stock restored, card payment REFUNDED).
- One review per user per product; product rating = average of reviews.
- Deleting a product hides it (soft delete) so old orders stay intact.

Full details: `docs/03-business-rules.md`.

## Tests

```bash
cd backend
mvn test
```

Unit tests (JUnit 5 + Mockito, no database needed): price calculation, auth, products, cart, orders, exception handler.

## Postman

Import `docs/postman/NEXORA.postman_collection.json`.
Run **Auth → Login** first; its test script saves the JWT into the `{{token}}` variable used by all other requests.

## Common problems

| Problem | Fix |
|---|---|
| `Access denied for user 'root'@'localhost'` | Wrong MySQL password in `application-local.properties` |
| `Communications link failure` | MySQL is not running / wrong port |
| `Port 8080 was already in use` | Stop the other app or set `server.port=8081` (and update `vite.config.js`) |
| Frontend shows "Cannot reach the server" | Backend is not running on 8080 |
| 401 after some time | JWT expired (1 hour) – log in again |
| Lombok errors in IntelliJ | Enable *Settings → Build → Compiler → Annotation Processors* |
| Want a fresh database | `DROP DATABASE nexora_db;` then restart the backend |
=======
# E-commerce
>>>>>>> d4238f0d708729cdfd79f98052a1c22de39e1502
