import { useCallback, useEffect, useState } from 'react';
import LoadingSpinner from '../../components/LoadingSpinner';
import ErrorMessage from '../../components/ErrorMessage';
import Pagination from '../../components/Pagination';
import StatusBadge from '../../components/StatusBadge';
import adminService from '../../services/adminService';
import { getErrorMessage } from '../../services/api';
import useToast from '../../hooks/useToast';
import useDocumentTitle from '../../hooks/useDocumentTitle';
import { NEXT_STATUSES, ORDER_STATUSES } from '../../utils/constants';
import { formatDateTime, formatPrice, formatStatus } from '../../utils/format';

export default function AdminOrders() {
  useDocumentTitle('Manage Orders');
  const toast = useToast();
  const [status, setStatus] = useState('');
  const [page, setPage] = useState(0);
  const [data, setData] = useState(null);
  const [error, setError] = useState('');
  const [updatingId, setUpdatingId] = useState(null);

  const load = useCallback(async () => {
    setError('');
    try {
      setData(await adminService.getOrders({ status: status || undefined, page, size: 15 }));
    } catch (err) {
      setError(getErrorMessage(err));
    }
  }, [status, page]);

  useEffect(() => {
    load();
  }, [load]);

  const changeStatus = async (order, newStatus) => {
    if (!newStatus) return;
    if (newStatus === 'CANCELLED' && !window.confirm(`Cancel order ${order.orderNumber}? Stock will be restored.`)) return;
    setUpdatingId(order.id);
    try {
      await adminService.updateOrderStatus(order.id, newStatus);
      toast.success(`Order ${order.orderNumber} → ${formatStatus(newStatus)}`);
      load();
    } catch (err) {
      toast.error(getErrorMessage(err));
    } finally {
      setUpdatingId(null);
    }
  };

  return (
    <div className="stack">
      <div className="page-header">
        <h1>Orders</h1>
        <label className="sort-select">
          <span>Status</span>
          <select
            value={status}
            onChange={(e) => {
              setStatus(e.target.value);
              setPage(0);
            }}
          >
            <option value="">All</option>
            {ORDER_STATUSES.map((s) => (
              <option key={s} value={s}>{formatStatus(s)}</option>
            ))}
          </select>
        </label>
      </div>

      <ErrorMessage message={error} onRetry={load} />
      {!data && !error && <LoadingSpinner />}

      {data && (
        <div className="card table-wrap">
          <table className="table">
            <thead>
              <tr><th>Order</th><th>Customer</th><th>Items</th><th className="right">Total</th><th>Payment</th><th>Status</th><th>Update</th></tr>
            </thead>
            <tbody>
              {data.content.map((o) => (
                <tr key={o.id}>
                  <td>{o.orderNumber}<br /><span className="muted small">{formatDateTime(o.createdAt)}</span></td>
                  <td>{o.customerName}<br /><span className="muted small">{o.customerEmail}</span></td>
                  <td>{o.totalItems}</td>
                  <td className="right">{formatPrice(o.grandTotal)}</td>
                  <td>{o.payment?.method} <StatusBadge status={o.payment?.status} /></td>
                  <td><StatusBadge status={o.status} /></td>
                  <td>
                    {NEXT_STATUSES[o.status].length > 0 ? (
                      <select
                        value=""
                        disabled={updatingId === o.id}
                        onChange={(e) => changeStatus(o, e.target.value)}
                      >
                        <option value="">Move to...</option>
                        {NEXT_STATUSES[o.status].map((s) => (
                          <option key={s} value={s}>{formatStatus(s)}</option>
                        ))}
                      </select>
                    ) : (
                      <span className="muted small">Final</span>
                    )}
                  </td>
                </tr>
              ))}
              {data.content.length === 0 && (
                <tr><td colSpan="7" className="center muted">No orders found.</td></tr>
              )}
            </tbody>
          </table>
        </div>
      )}

      {data && <Pagination page={data.page} totalPages={data.totalPages} onChange={setPage} />}
    </div>
  );
}
