import { useEffect, useState } from 'react';
import { Link, useLocation, useParams } from 'react-router-dom';
import LoadingSpinner from '../components/LoadingSpinner';
import orderService from '../services/orderService';
import useDocumentTitle from '../hooks/useDocumentTitle';
import { formatPrice } from '../utils/format';

export default function OrderSuccess() {
  useDocumentTitle('Order placed');
  const { id } = useParams();
  const { state } = useLocation();
  const [order, setOrder] = useState(state?.order || null);

  // If the page was refreshed, fetch the order again
  useEffect(() => {
    if (!order) {
      orderService.getOrder(id).then(setOrder).catch(() => setOrder(null));
    }
  }, [id, order]);

  if (!order) return <div className="container page"><LoadingSpinner /></div>;

  return (
    <div className="container page center">
      <div className="card success-card">
        <div className="success-icon" aria-hidden="true">✓</div>
        <h1>Thank you for your order!</h1>
        <p>Order number <strong>{order.orderNumber}</strong></p>
        <p>
          Total paid: <strong>{formatPrice(order.grandTotal)}</strong>
          {' '}({order.payment?.method === 'COD' ? 'Cash on delivery' : `Card •••• ${order.payment?.cardLast4}`})
        </p>
        <p className="muted">Delivering to {order.shippingAddress.name}, {order.shippingAddress.city}</p>
        <div className="hero-actions center">
          <Link to={`/orders/${order.id}`} className="btn btn-primary">View order</Link>
          <Link to="/products" className="btn btn-outline">Continue shopping</Link>
        </div>
      </div>
    </div>
  );
}
