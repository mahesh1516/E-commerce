import { useState } from 'react';
import { Link } from 'react-router-dom';
import ProductImage from './ProductImage';
import useCart from '../hooks/useCart';
import useToast from '../hooks/useToast';
import { getErrorMessage } from '../services/api';
import { formatPrice } from '../utils/format';

const MAX_QTY = 10;

export default function CartItem({ item }) {
  const { updateQuantity, removeItem } = useCart();
  const toast = useToast();
  const [busy, setBusy] = useState(false);

  const run = async (action) => {
    setBusy(true);
    try {
      await action();
    } catch (err) {
      toast.error(getErrorMessage(err));
    } finally {
      setBusy(false);
    }
  };

  const maxAllowed = Math.min(MAX_QTY, item.availableStock);

  return (
    <div className="cart-item card">
      <Link to={`/products/${item.productId}`} className="cart-item-image">
        <ProductImage src={item.imageUrl} name={item.productName} />
      </Link>

      <div className="cart-item-info">
        <Link to={`/products/${item.productId}`} className="product-name">{item.productName}</Link>
        <span className="product-brand">{item.brand}</span>
        <div className="price-line">
          <span className="price">{formatPrice(item.unitPrice)}</span>
          {Number(item.price) > Number(item.unitPrice) && (
            <span className="price-old">{formatPrice(item.price)}</span>
          )}
        </div>
        {item.availableStock < item.quantity && (
          <span className="stock-label out">Only {item.availableStock} in stock - please reduce quantity</span>
        )}
      </div>

      <div className="cart-item-actions">
        <div className="qty-control">
          <button
            type="button"
            disabled={busy || item.quantity <= 1}
            onClick={() => run(() => updateQuantity(item.id, item.quantity - 1))}
            aria-label="Decrease quantity"
          >
            −
          </button>
          <span>{item.quantity}</span>
          <button
            type="button"
            disabled={busy || item.quantity >= maxAllowed}
            onClick={() => run(() => updateQuantity(item.id, item.quantity + 1))}
            aria-label="Increase quantity"
          >
            +
          </button>
        </div>
        <strong>{formatPrice(item.lineTotal)}</strong>
        <button
          type="button"
          className="link-btn danger"
          disabled={busy}
          onClick={() => run(async () => {
            await removeItem(item.id);
            toast.success('Item removed');
          })}
        >
          Remove
        </button>
      </div>
    </div>
  );
}
