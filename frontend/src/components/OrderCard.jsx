import { Link } from 'react-router-dom';
import StatusBadge from './StatusBadge';
import { formatDate, formatPrice } from '../utils/format';

export default function OrderCard({ order }) {
  return (
    <Link to={`/orders/${order.id}`} className="order-card card">
      <div>
        <strong>{order.orderNumber}</strong>
        <p className="muted">
          {formatDate(order.createdAt)} · {order.totalItems} item{order.totalItems !== 1 ? 's' : ''}
        </p>
        <p className="muted small">{order.items.map((i) => i.productName).join(', ')}</p>
      </div>
      <div className="order-card-right">
        <StatusBadge status={order.status} />
        <strong>{formatPrice(order.grandTotal)}</strong>
      </div>
    </Link>
  );
}
