# NEXORA – API Map

Base URL (development): `http://localhost:8080`
Interactive docs: `http://localhost:8080/swagger-ui.html`

Protected endpoints need the header `Authorization: Bearer <token>`.

## Auth (public)
| Method | URL | Notes |
|---|---|---|
| POST | /api/auth/register | Returns a JWT (auto-login) |
| POST | /api/auth/login | Returns a JWT |

## Profile (logged in)
| Method | URL |
|---|---|
| GET | /api/users/me |
| PUT | /api/users/me |

## Products
| Method | URL | Access |
|---|---|---|
| GET | /api/products | Public |
| GET | /api/products/brands | Public |
| GET | /api/products/{id} | Public |
| POST | /api/products | ADMIN |
| PUT | /api/products/{id} | ADMIN |
| DELETE | /api/products/{id} | ADMIN (soft delete) |

Query parameters for `GET /api/products`:
`search, category (slug), brand, minPrice, maxPrice, discounted, sort, page, size`
Sortable fields: `name, price, rating, reviewCount, createdAt` (e.g. `sort=price,asc`).

```
GET /api/products?page=0&size=10
GET /api/products?search=iphone
GET /api/products?category=electronics
GET /api/products?brand=samsung
GET /api/products?minPrice=100&maxPrice=1000&sort=price,asc
GET /api/products?discounted=true
```

## Reviews
| Method | URL | Access |
|---|---|---|
| GET | /api/products/{productId}/reviews | Public |
| POST | /api/products/{productId}/reviews | Logged in (one per product) |

## Categories
| Method | URL | Access |
|---|---|---|
| GET | /api/categories | Public |
| GET | /api/categories/{id} | Public |
| POST | /api/categories | ADMIN |
| PUT | /api/categories/{id} | ADMIN |
| DELETE | /api/categories/{id} | ADMIN (only if no products) |

## Cart (logged in)
| Method | URL | Body |
|---|---|---|
| GET | /api/cart | |
| POST | /api/cart/items | `{ "productId": 1, "quantity": 2 }` |
| PUT | /api/cart/items/{id} | `{ "quantity": 3 }` |
| DELETE | /api/cart/items/{id} | |
| DELETE | /api/cart | |

## Wishlist (logged in)
| Method | URL |
|---|---|
| GET | /api/wishlist |
| POST | /api/wishlist/{productId} |
| DELETE | /api/wishlist/{productId} |

## Addresses (logged in)
| Method | URL |
|---|---|
| GET | /api/addresses |
| POST | /api/addresses |
| PUT | /api/addresses/{id} |
| DELETE | /api/addresses/{id} |
| PUT | /api/addresses/{id}/default |

## Orders (logged in)
| Method | URL | Notes |
|---|---|---|
| POST | /api/orders | Checkout. Body: `{ "addressId": 1, "paymentMethod": "CARD", "cardNumber": "4111111111111111", "cardHolder": "Mahesh" }` |
| GET | /api/orders?page=0&size=10 | My orders |
| GET | /api/orders/{id} | My order |
| PUT | /api/orders/{id}/cancel | Before SHIPPED only |

Payment simulation: card number ending in `0000` → **402 Payment declined**, nothing is saved.
`paymentMethod: "COD"` → payment stays PENDING until the order is DELIVERED.

## Admin (ADMIN role)
| Method | URL |
|---|---|
| GET | /api/admin/dashboard |
| GET | /api/admin/users |
| GET | /api/admin/orders?status=CONFIRMED&page=0&size=20 |
| PUT | /api/admin/orders/{id}/status — body `{ "status": "SHIPPED" }` |
| GET | /api/admin/inventory |
| PUT | /api/admin/inventory/{productId} — body `{ "quantity": 50, "lowStockThreshold": 5 }` |

## Error format (all errors)
```json
{
  "success": false,
  "message": "Product not found with id 99",
  "status": 404,
  "timestamp": "2026-09-26T10:30:00"
}
```
Validation errors add `"errors": { "email": "Email must be valid" }`.

| Status | Meaning |
|---|---|
| 400 | Validation / bad request |
| 401 | Not logged in / bad credentials / expired token |
| 402 | Simulated payment declined |
| 403 | Logged in but not allowed (e.g. USER calling admin API) |
| 404 | Not found |
| 409 | Duplicate (email, SKU, review) or not enough stock |
| 500 | Unexpected server error |
