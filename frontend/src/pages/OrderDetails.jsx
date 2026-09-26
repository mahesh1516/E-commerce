import { useCallback, useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import ProductImage from '../components/ProductImage';
import PriceSummary from '../components/PriceSummary';
import StatusBadge from '../components/StatusBadge';
import LoadingSpinner from '../components/LoadingSpinner';
import ErrorMessage from '../components/ErrorMessage';
import orderService from '../services/orderService';
import { getErrorMessage } from '../services/api';
import useToast from '../hooks/useToast';
import useDocumentTitle from '../hooks/useDocumentTitle';
import { formatDateTime, formatPrice, formatStatus } from '../utils/format';

const STEPS = ['CONFIRMED', 'PROCESSING', 'SHIPPED', 'DELIVERED'];

export default function OrderDetails() {
  const { id } = useParams();
  const toast = useToast();
  const [order, setOrder] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [cancelling, setCancelling] = useState(false);

  useDocumentTitle(order ? `Order ${order.orderNumber}` : 'Order');

  const load = useCallback(async () => {
    setLoading(true);
    setError('');
    try {
      setOrder(await orderService.getOrder(id));
    } catch (err) {
      setError(getErrorMessage(err));
    } finally {
      setLoading(false);
    }
  }, [id]);

  useEffect(() => {
    load();
  }, [load]);

  const handleCancel = async () => {
    if (!window.confirm('Cancel this order? Items will be returned to stock.')) return;
    setCancelling(true);
    try {
      setOrder(await orderService.cancelOrder(order.id));
      toast.success('Order cancelled');
    } catch (err) {
      toast.error(getErrorMessage(err));
    } finally {
      setCancelling(false);
    }
  };

  if (loading) return <div className="container page"><LoadingSpinner /></div>;
  if (error) return <div className="container page"><ErrorMessage message={error} onRetry={load} /></div>;

  const a = order.shippingAddress;
  const currentStep = STEPS.indexOf(order.status);

  return (
    <div className="container page">
      <Link to="/orders" className="small">← All orders</Link>
      <div className="page-header">
        <div>
          <h1>Order {order.orderNumber}</h1>
          <p className="muted">Placed on {formatDateTime(order.createdAt)}</p>
        </div>
        <StatusBadge status={order.status} />
      </div>

      {order.status !== 'CANCELLED' && (
        <ol className="progress-steps">
          {STEPS.map((s, i) => (
            <li key={s} className={i <= currentStep ? 'done' : ''}>{formatStatus(s)}</li>
          ))}
        </ol>
      )}

      <div className="two-col">
        <div className="stack">
          <div className="card">
            <h3>Items</h3>
            {order.items.map((i) => (
              <div key={i.id} className="order-item">
                <ProductImage src={i.imageUrl} name={i.productName} className="thumb" />
                <div className="grow">
                  <Link to={`/products/${i.productId}`}>{i.productName}</Link>
                  <p className="muted small">{formatPrice(i.finalUnitPrice)} × {i.quantity}</p>
                </div>
                <strong>{formatPrice(i.lineTotal)}</strong>
              </div>
            ))}
          </div>

          <div className="card">
            <h3>Delivery address</h3>
            <p>
              <strong>{a.name}</strong> · {a.phone}<br />
              {a.addressLine1}{a.addressLine2 ? `, ${a.addressLine2}` : ''}<br />
              {a.city}, {a.state} {a.postalCode}, {a.country}
            </p>
          </div>

          {order.payment && (
            <div className="card">
              <h3>Payment</h3>
              <p>
                {order.payment.method === 'COD' ? 'Cash on delivery' : `Card •••• ${order.payment.cardLast4}`}
                {' · '}<StatusBadge status={order.payment.status} />
              </p>
              {order.payment.transactionId && <p className="muted small">Transaction: {order.payment.transactionId}</p>}
            </div>
          )}
        </div>

        <PriceSummary summary={order} title="Order total">
          {order.cancellable && (
            <button type="button" className="btn btn-danger btn-block" disabled={cancelling} onClick={handleCancel}>
              {cancelling ? 'Cancelling...' : 'Cancel order'}
            </button>
          )}
        </PriceSummary>
      </div>
    </div>
  );
}
