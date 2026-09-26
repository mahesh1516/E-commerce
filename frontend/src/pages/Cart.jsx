import { useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import CartItem from '../components/CartItem';
import PriceSummary from '../components/PriceSummary';
import LoadingSpinner from '../components/LoadingSpinner';
import useCart from '../hooks/useCart';
import useToast from '../hooks/useToast';
import useDocumentTitle from '../hooks/useDocumentTitle';
import { getErrorMessage } from '../services/api';
import { formatPrice } from '../utils/format';

export default function Cart() {
  useDocumentTitle('Cart');
  const { cart, loading, refresh, clearCart } = useCart();
  const toast = useToast();
  const navigate = useNavigate();

  // Always show fresh prices/stock when opening the cart
  useEffect(() => {
    refresh();
  }, [refresh]);

  if (loading && !cart) return <div className="container page"><LoadingSpinner /></div>;

  if (!cart || cart.items.length === 0) {
    return (
      <div className="container page center">
        <h1>Your cart is empty</h1>
        <p className="muted">Looks like you haven't added anything yet.</p>
        <Link to="/products" className="btn btn-primary">Start shopping</Link>
      </div>
    );
  }

  const stockProblem = cart.items.some((i) => i.quantity > i.availableStock);
  const toFreeShipping = 5000 - (Number(cart.subtotal) - Number(cart.discount));

  const handleClear = async () => {
    if (!window.confirm('Remove all items from your cart?')) return;
    try {
      await clearCart();
    } catch (err) {
      toast.error(getErrorMessage(err));
    }
  };

  return (
    <div className="container page">
      <div className="page-header">
        <h1>Shopping Cart ({cart.totalItems})</h1>
        <button type="button" className="link-btn danger" onClick={handleClear}>Clear cart</button>
      </div>

      <div className="two-col">
        <div className="stack">
          {cart.items.map((item) => (
            <CartItem key={item.id} item={item} />
          ))}
        </div>

        <PriceSummary summary={cart}>
          {toFreeShipping > 0 && (
            <p className="muted small">Add {formatPrice(toFreeShipping)} more for free shipping.</p>
          )}
          {stockProblem && <p className="error-text">Some items exceed available stock.</p>}
          <button
            type="button"
            className="btn btn-primary btn-block"
            disabled={stockProblem}
            onClick={() => navigate('/checkout')}
          >
            Proceed to Checkout
          </button>
        </PriceSummary>
      </div>
    </div>
  );
}
