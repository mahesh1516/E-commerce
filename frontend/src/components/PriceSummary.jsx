import { formatPrice } from '../utils/format';

/** Works for both the cart (shipping) and orders (shippingFee). */
export default function PriceSummary({ summary, title = 'Price Details', children }) {
  if (!summary) return null;
  const shipping = summary.shipping ?? summary.shippingFee;

  return (
    <div className="card summary">
      <h3>{title}</h3>
      <div className="summary-row"><span>Subtotal</span><span>{formatPrice(summary.subtotal)}</span></div>
      <div className="summary-row text-success">
        <span>Discount</span><span>- {formatPrice(summary.discount)}</span>
      </div>
      <div className="summary-row"><span>Tax (10%)</span><span>{formatPrice(summary.tax)}</span></div>
      <div className="summary-row">
        <span>Shipping</span>
        <span>{Number(shipping) === 0 ? 'FREE' : formatPrice(shipping)}</span>
      </div>
      <div className="summary-row summary-total">
        <span>Grand Total</span><span>{formatPrice(summary.grandTotal)}</span>
      </div>
      {children}
    </div>
  );
}
