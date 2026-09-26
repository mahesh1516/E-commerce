# NEXORA – Architecture

## High-level view

```
React (browser, :5173)
   │  HTTP + JSON, Authorization: Bearer <JWT>
   ▼
Spring Boot (:8080)
   Security filter (JWT)
   Controller
   Service
   Repository
   JPA / Hibernate
   │  JDBC
   ▼
MySQL 8 (:3306)
```

React and Spring Boot run on different ports, so CORS is configured
in the backend (Step 12).

## Layer responsibilities

| Layer | Responsibility |
|---|---|
| React | Displays UI, collects input |
| Axios service | Sends HTTP requests, attaches JWT |
| Security filter | Validates JWT, blocks unauthorized requests |
| Controller | Receives HTTP requests, validates DTOs, returns responses |
| Service | Business logic, calculations, transactions |
| Repository | Reads/writes the database |
| Entity | Java class representing a table |
| JPA/Hibernate | Translates objects ↔ SQL |
| MySQL | Stores data permanently |

Rule: each layer talks only to the layer directly below it.

## Backend packages (`com.nexora.ecommerce`)

| Package | Why it exists |
|---|---|
| controller | REST endpoints (the API's "doors") |
| service | Business-logic interfaces (easy to mock in tests) |
| service.impl | Implementations of the service interfaces |
| repository | Spring Data JPA interfaces for DB access |
| entity | Table-mapped classes; never returned from APIs |
| dto | Request/response objects; hide internals, avoid JSON loops |
| mapper | Entity ↔ DTO conversion |
| security | JWT utility, JWT filter, security configuration |
| exception | Custom exceptions + GlobalExceptionHandler |
| config | CORS, Swagger, password encoder, other beans |
| util | Constants and small helpers |

## Frontend structure (`frontend/src`)

| Folder | Purpose |
|---|---|
| components | Reusable UI (Navbar, ProductCard, Pagination…) |
| pages | Full screens; `pages/admin` for admin screens |
| services | Axios API calls (api.js, authService.js…) |
| context | AuthContext, CartContext, WishlistContext, ProductContext, OrderContext |
| hooks | useAuth(), useCart()… |
| utils | Formatters and validators |
| routes | AppRoutes, ProtectedRoute, AdminRoute |
| assets | Images, icons |

State management: **Context API** (shared state for auth, cart,
wishlist, products, orders without prop drilling).

Route protection happens in the frontend for user experience;
**real security is enforced by the backend**.

## Modules

Auth · Product · Category · Inventory · Cart · Wishlist · Address ·
Order · Payment (simulated) · Review · Admin

## Authentication flow

```
User enters email + password
 → React Login page
 → POST /api/auth/login
 → Spring Security AuthenticationManager loads user
 → BCrypt verifies password against stored hash
 → JwtUtil generates token (subject, role, expiry)
 → Response { token, userId, username, role }
 → AuthContext stores it (and localStorage for refresh)
 → Axios interceptor sends "Authorization: Bearer <token>"
 → JwtAuthFilter validates token on each request → Controller
```

Logout: the frontend deletes the token (JWT is stateless);
tokens also expire automatically (1 hour).

## Example request: "Add to cart"

```
Click "Add to Cart"
 → cartService.addItem(5, 2)
 → POST /api/cart/items {productId:5, quantity:2} + JWT
 → JwtAuthFilter: user = mahesh
 → CartController: validate DTO
 → CartService: product active? stock ≥ qty? merge if exists
 → CartRepository.save()
 → INSERT/UPDATE cart_items
 ← CartResponse (items + totals)
 → CartContext updates → Navbar badge refreshes
```
