import { useCallback, useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import OrderCard from '../components/OrderCard';
import Pagination from '../components/Pagination';
import LoadingSpinner from '../components/LoadingSpinner';
import ErrorMessage from '../components/ErrorMessage';
import orderService from '../services/orderService';
import { getErrorMessage } from '../services/api';
import useDocumentTitle from '../hooks/useDocumentTitle';

export default function Orders() {
  useDocumentTitle('My Orders');
  const [page, setPage] = useState(0);
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const load = useCallback(async () => {
    setLoading(true);
    setError('');
    try {
      setData(await orderService.getMyOrders(page, 10));
    } catch (err) {
      setError(getErrorMessage(err));
    } finally {
      setLoading(false);
    }
  }, [page]);

  useEffect(() => {
    load();
  }, [load]);

  return (
    <div className="container page">
      <h1>My Orders</h1>
      {loading && <LoadingSpinner />}
      <ErrorMessage message={error} onRetry={load} />

      {!loading && data && data.content.length === 0 && (
        <div className="center">
          <p className="empty-state">You haven't placed any orders yet.</p>
          <Link to="/products" className="btn btn-primary">Start shopping</Link>
        </div>
      )}

      {!loading && data && (
        <div className="stack">
          {data.content.map((o) => (
            <OrderCard key={o.id} order={o} />
          ))}
          <Pagination page={data.page} totalPages={data.totalPages} onChange={setPage} />
        </div>
      )}
    </div>
  );
}
