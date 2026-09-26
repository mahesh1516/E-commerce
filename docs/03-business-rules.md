# NEXORA – Business Rules

## Checkout calculation
```
Subtotal   = Σ (unit price × quantity)
Discount   = Σ ((price − discountPrice) × quantity)
Tax        = 10% × (Subtotal − Discount)
Shipping   = ₹50, free when (Subtotal − Discount) > ₹5,000
GrandTotal = Subtotal − Discount + Tax + Shipping
```
Example: 2000 − 200 + 180 + 50 = **₹2,030**

Tax rate, shipping fee and free-shipping threshold are configurable
in `application.properties`.

## Money
- Java: `BigDecimal` · MySQL: `DECIMAL(12,2)` · never `double`.

## Stock
- Single source of truth: the `inventory` table.
  `ProductResponse.stock` is read from it.
- Checked when adding to cart **and** again at checkout.

## Orders
- Order placement is one `@Transactional` operation:
  validate user → validate cart → validate stock → calculate totals →
  create order → create order items → reduce stock → create payment →
  clear cart → return confirmation.
  Any failure rolls everything back.
- Order items copy the price at purchase time.
- Lifecycle:
  ```
  PENDING → CONFIRMED → PROCESSING → SHIPPED → DELIVERED
  CANCELLED: only before SHIPPED; restores stock
  ```

## Payment (simulated)
- Statuses: PENDING, SUCCESS, FAILED
  (proposed: REFUNDED for cancelled paid orders).
- Card number ending in `0000` → FAILED; any other → SUCCESS.

## Products
- `imageUrl` = main image; `product_images` = gallery.
- Delete = soft delete (`active = false`).
- `rating` = average of reviews, updated when a review is added.

## Reviews
- Authenticated users only; rating 1–5; one review per user per product.

## Addresses
- Setting a default address un-sets the previous default.

## Sample accounts (development only)
- Admin: `admin@nexora.com`
- Customer: `user@nexora.com`
- Dev-only passwords are documented in Step 4 and **must be changed**
  outside local development.
