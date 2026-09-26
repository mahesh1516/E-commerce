import { useCallback, useEffect, useState } from 'react';
import AddressForm from '../components/AddressForm';
import LoadingSpinner from '../components/LoadingSpinner';
import ErrorMessage from '../components/ErrorMessage';
import addressService from '../services/addressService';
import { getErrorMessage } from '../services/api';
import useToast from '../hooks/useToast';
import useDocumentTitle from '../hooks/useDocumentTitle';

export default function Addresses() {
  useDocumentTitle('Addresses');
  const toast = useToast();
  const [addresses, setAddresses] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [editing, setEditing] = useState(null); // null | 'new' | address object

  const load = useCallback(async () => {
    setLoading(true);
    try {
      setAddresses(await addressService.getAll());
    } catch (err) {
      setError(getErrorMessage(err));
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    load();
  }, [load]);

  const run = async (action, message) => {
    try {
      await action();
      toast.success(message);
      setEditing(null);
      await load();
    } catch (err) {
      toast.error(getErrorMessage(err));
    }
  };

  const handleSave = (form) =>
    editing === 'new'
      ? run(() => addressService.create(form), 'Address added')
      : run(() => addressService.update(editing.id, form), 'Address updated');

  if (loading) return <div className="container page"><LoadingSpinner /></div>;

  return (
    <div className="container page narrow">
      <div className="page-header">
        <h1>Saved Addresses</h1>
        {!editing && (
          <button type="button" className="btn btn-primary" onClick={() => setEditing('new')}>+ Add address</button>
        )}
      </div>
      <ErrorMessage message={error} onRetry={load} />

      {editing && (
        <AddressForm
          key={editing === 'new' ? 'new' : editing.id}
          initial={editing === 'new' ? undefined : editing}
          onSubmit={handleSave}
          onCancel={() => setEditing(null)}
        />
      )}

      {addresses.length === 0 && !editing && <p className="empty-state">No saved addresses yet.</p>}

      <div className="stack">
        {addresses.map((a) => (
          <div key={a.id} className="card address-card">
            <div>
              <strong>{a.name}</strong> {a.defaultAddress && <span className="badge">Default</span>}
              <p className="muted small">
                {a.addressLine1}{a.addressLine2 ? `, ${a.addressLine2}` : ''}, {a.city}, {a.state} {a.postalCode}, {a.country}
              </p>
              <p className="muted small">📞 {a.phone}</p>
            </div>
            <div className="row-actions">
              <button type="button" className="link-btn" onClick={() => setEditing(a)}>Edit</button>
              {!a.defaultAddress && (
                <button type="button" className="link-btn" onClick={() => run(() => addressService.setDefault(a.id), 'Default address updated')}>
                  Set default
                </button>
              )}
              <button
                type="button"
                className="link-btn danger"
                onClick={() => window.confirm('Delete this address?') && run(() => addressService.remove(a.id), 'Address deleted')}
              >
                Delete
              </button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
