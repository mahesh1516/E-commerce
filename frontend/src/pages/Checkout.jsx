import { useCallback, useEffect, useState } from 'react';
import { Link, Navigate, useNavigate } from 'react-router-dom';
import AddressForm from '../components/AddressForm';
import PriceSummary from '../components/PriceSummary';
import LoadingSpinner from '../components/LoadingSpinner';
import ErrorMessage from '../components/ErrorMessage';
import addressService from '../services/addressService';
import { getErrorMessage } from '../services/api';
import useCart from '../hooks/useCart';
import useToast from '../hooks/useToast';
import useDocumentTitle from '../hooks/useDocumentTitle';
import { formatPrice } from '../utils/format';

export default function Checkout() {
  useDocumentTitle('Checkout');
  const { cart, loading: cartLoading } = useCart();
  const toast = useToast();
  const navigate = useNavigate();

  const [addresses, setAddresses] = useState([]);
  const [selectedId, setSelectedId] = useState(null);
  const [showForm, setShowForm] = useState(false);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const loadAddresses = useCallback(async () => {
    setLoading(true);
    try {
      const list = await addressService.getAll();
      setAddresses(list);
      setSelectedId((current) => current ?? list.find((a) => a.defaultAddress)?.id ?? list[0]?.id ?? null);
      setShowForm(list.length === 0);
    } catch (err) {
      setError(getErrorMessage(err));
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    loadAddresses();
  }, [loadAddresses]);

  const handleAddAddress = async (form) => {
    try {
      const created = await addressService.create(form);
      toast.success('Address saved');
      setSelectedId(created.id);
      await loadAddresses();
      setShowForm(false);
    } catch (err) {
      toast.error(getErrorMessage(err));
    }
  };

  if (cartLoading || loading) return <div className="container page"><LoadingSpinner /></div>;
  if (!cart || cart.items.length === 0) return <Navigate to="/cart" replace />;

  return (
    <div className="container page">
      <h1>Checkout</h1>
      <ErrorMessage message={error} onRetry={loadAddresses} />

      <div className="two-col">
        <div className="stack">
          <div className="card">
            <div className="section-header">
              <h3>1. Delivery address</h3>
              {!showForm && (
                <button type="button" className="link-btn" onClick={() => setShowForm(true)}>+ Add new</button>
              )}
            </div>

            {addresses.map((a) => (
              <label key={a.id} className={`address-option ${selectedId === a.id ? 'selected' : ''}`}>
                <input
                  type="radio"
                  name="address"
                  checked={selectedId === a.id}
                  onChange={() => setSelectedId(a.id)}
                />
                <div>
                  <strong>{a.name}</strong> {a.defaultAddress && <span className="badge">Default</span>}
                  <p className="muted small">
                    {a.addressLine1}{a.addressLine2 ? `, ${a.addressLine2}` : ''}, {a.city}, {a.state} {a.postalCode}, {a.country} · {a.phone}
                  </p>
                </div>
              </label>
            ))}
          </div>

          {showForm && (
            <AddressForm
              onSubmit={handleAddAddress}
              onCancel={addresses.length ? () => setShowForm(false) : undefined}
              submitLabel="Save and use this address"
            />
          )}

          <div className="card">
            <h3>2. Review items</h3>
            {cart.items.map((i) => (
              <div key={i.id} className="summary-row">
                <span>{i.productName} × {i.quantity}</span>
                <span>{formatPrice(i.lineTotal)}</span>
              </div>
            ))}
            <Link to="/cart" className="small">Edit cart</Link>
          </div>
        </div>

        <PriceSummary summary={cart} title="Order Summary">
          <button
            type="button"
            className="btn btn-primary btn-block"
            disabled={!selectedId}
            onClick={() => navigate('/payment', { state: { addressId: selectedId } })}
          >
            Continue to Payment
          </button>
          {!selectedId && <p className="muted small">Add or select an address to continue.</p>}
        </PriceSummary>
      </div>
    </div>
  );
}
