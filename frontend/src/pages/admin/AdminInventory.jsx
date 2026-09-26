import { useCallback, useEffect, useState } from 'react';
import LoadingSpinner from '../../components/LoadingSpinner';
import ErrorMessage from '../../components/ErrorMessage';
import adminService from '../../services/adminService';
import { getErrorMessage } from '../../services/api';
import useToast from '../../hooks/useToast';
import useDocumentTitle from '../../hooks/useDocumentTitle';

export default function AdminInventory() {
  useDocumentTitle('Inventory');
  const toast = useToast();
  const [rows, setRows] = useState(null);
  const [edits, setEdits] = useState({}); // productId -> { quantity, lowStockThreshold }
  const [error, setError] = useState('');
  const [lowOnly, setLowOnly] = useState(false);

  const load = useCallback(async () => {
    setError('');
    try {
      setRows(await adminService.getInventory());
      setEdits({});
    } catch (err) {
      setError(getErrorMessage(err));
    }
  }, []);

  useEffect(() => {
    load();
  }, [load]);

  const edit = (productId, field, value) =>
    setEdits((e) => ({ ...e, [productId]: { ...e[productId], [field]: value } }));

  const save = async (row) => {
    const change = edits[row.productId] || {};
    const quantity = Number(change.quantity ?? row.quantity);
    const lowStockThreshold = Number(change.lowStockThreshold ?? row.lowStockThreshold);
    if (!(quantity >= 0) || !(lowStockThreshold >= 0)) {
      toast.error('Values must be 0 or more');
      return;
    }
    try {
      const updated = await adminService.updateInventory(row.productId, { quantity, lowStockThreshold });
      setRows((list) => list.map((r) => (r.productId === row.productId ? updated : r)));
      setEdits((e) => {
        const next = { ...e };
        delete next[row.productId];
        return next;
      });
      toast.success(`Stock updated for ${row.productName}`);
    } catch (err) {
      toast.error(getErrorMessage(err));
    }
  };

  if (error) return <ErrorMessage message={error} onRetry={load} />;
  if (!rows) return <LoadingSpinner />;

  const visible = lowOnly ? rows.filter((r) => r.lowStock) : rows;

  return (
    <div className="stack">
      <div className="page-header">
        <h1>Inventory</h1>
        <label className="checkbox">
          <input type="checkbox" checked={lowOnly} onChange={(e) => setLowOnly(e.target.checked)} />
          <span>Low stock only</span>
        </label>
      </div>

      <div className="card table-wrap">
        <table className="table">
          <thead>
            <tr><th>Product</th><th>Quantity</th><th>Low-stock alert at</th><th>Status</th><th /></tr>
          </thead>
          <tbody>
            {visible.map((r) => {
              const change = edits[r.productId] || {};
              return (
                <tr key={r.productId}>
                  <td>{r.productName}<br /><span className="muted small">{r.sku}</span></td>
                  <td>
                    <input
                      type="number"
                      min="0"
                      className="input-sm"
                      value={change.quantity ?? r.quantity}
                      onChange={(e) => edit(r.productId, 'quantity', e.target.value)}
                    />
                  </td>
                  <td>
                    <input
                      type="number"
                      min="0"
                      className="input-sm"
                      value={change.lowStockThreshold ?? r.lowStockThreshold}
                      onChange={(e) => edit(r.productId, 'lowStockThreshold', e.target.value)}
                    />
                  </td>
                  <td>
                    {r.quantity === 0 ? (
                      <span className="text-danger">Out of stock</span>
                    ) : r.lowStock ? (
                      <span className="text-warning">Low</span>
                    ) : (
                      <span className="text-success">OK</span>
                    )}
                  </td>
                  <td>
                    <button type="button" className="btn btn-sm btn-primary" disabled={!edits[r.productId]} onClick={() => save(r)}>
                      Save
                    </button>
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>
      </div>
    </div>
  );
}
