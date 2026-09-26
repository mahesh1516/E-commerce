import { useCallback, useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import LoadingSpinner from '../../components/LoadingSpinner';
import ErrorMessage from '../../components/ErrorMessage';
import StatusBadge from '../../components/StatusBadge';
import adminService from '../../services/adminService';
import { getErrorMessage } from '../../services/api';
import useDocumentTitle from '../../hooks/useDocumentTitle';
import { formatDate, formatPrice, formatStatus } from '../../utils/format';

export default function AdminDashboard() {
  useDocumentTitle('Admin Dashboard');
  const [data, setData] = useState(null);
  const [error, setError] = useState('');

  const load = useCallback(async () => {
    setError('');
    try {
      setData(await adminService.getDashboard());
    } catch (err) {
      setError(getErrorMessage(err));
    }
  }, []);

  useEffect(() => {
    load();
  }, [load]);

  if (error) return <ErrorMessage message={error} onRetry={load} />;
  if (!data) return <LoadingSpinner />;

  const stats = [
    ['Total users', data.totalUsers],
    ['Active products', data.totalProducts],
    ['Total orders', data.totalOrders],
    ['Revenue', formatPrice(data.totalRevenue)],
  ];

  return (
    <div className="stack">
      <h1>Dashboard</h1>

      <div className="stat-grid">
        {stats.map(([label, value]) => (
          <div key={label} className="card stat">
            <span className="muted">{label}</span>
            <strong>{value}</strong>
          </div>
        ))}
      </div>

      <div className="card">
        <h3>Orders by status</h3>
        <div className="status-counts">
          {Object.entries(data.orderStatusCounts).map(([status, count]) => (
            <span key={status} className="status-count">
              {formatStatus(status)}: <strong>{count}</strong>
            </span>
          ))}
        </div>
      </div>

      <div className="two-col equal">
        <div className="card">
          <div className="section-header">
            <h3>Recent orders</h3>
            <Link to="/admin/orders">View all</Link>
          </div>
          {data.recentOrders.length === 0 && <p className="muted">No orders yet.</p>}
          <div className="table-wrap">
            <table className="table">
              <tbody>
                {data.recentOrders.map((o) => (
                  <tr key={o.id}>
                    <td>{o.orderNumber}<br /><span className="muted small">{o.customerName} · {formatDate(o.createdAt)}</span></td>
                    <td><StatusBadge status={o.status} /></td>
                    <td className="right">{formatPrice(o.grandTotal)}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>

        <div className="card">
          <div className="section-header">
            <h3>Low stock</h3>
            <Link to="/admin/inventory">Manage</Link>
          </div>
          {data.lowStockProducts.length === 0 && <p className="muted">All products are well stocked.</p>}
          <div className="table-wrap">
            <table className="table">
              <tbody>
                {data.lowStockProducts.map((i) => (
                  <tr key={i.productId}>
                    <td>{i.productName}<br /><span className="muted small">{i.sku}</span></td>
                    <td className={`right ${i.quantity === 0 ? 'text-danger' : 'text-warning'}`}>
                      {i.quantity === 0 ? 'Out of stock' : `${i.quantity} left`}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  );
}
