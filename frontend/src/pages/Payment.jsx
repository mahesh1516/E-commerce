import { useRef, useState } from 'react';
import { Navigate, useLocation, useNavigate } from 'react-router-dom';
import PriceSummary from '../components/PriceSummary';
import orderService from '../services/orderService';
import { getErrorMessage } from '../services/api';
import useCart from '../hooks/useCart';
import useToast from '../hooks/useToast';
import useDocumentTitle from '../hooks/useDocumentTitle';
import { hasErrors, validateCard } from '../utils/validators';
import { TEST_CARD_DECLINE, TEST_CARD_SUCCESS } from '../utils/constants';

/** Formats "4111111111111111" as "4111 1111 1111 1111" while typing. */
function formatCardNumber(value) {
  return value.replace(/\D/g, '').slice(0, 19).replace(/(\d{4})(?=\d)/g, '$1 ');
}

export default function Payment() {
  useDocumentTitle('Payment');
  const { state } = useLocation();
  const navigate = useNavigate();
  const { cart, refresh } = useCart();
  const toast = useToast();

  const [method, setMethod] = useState('CARD');
  const [card, setCard] = useState({ cardNumber: '', cardHolder: '', expiry: '', cvv: '' });
  const [errors, setErrors] = useState({});
  const [serverError, setServerError] = useState('');
  const [placing, setPlacing] = useState(false);
  const placedRef = useRef(false);

  // Must come from the Checkout page with an address selected
  if (!state?.addressId) return <Navigate to="/checkout" replace />;
  if (!placedRef.current && (!cart || cart.items.length === 0)) return <Navigate to="/cart" replace />;

  const set = (field) => (e) => {
    const value = field === 'cardNumber' ? formatCardNumber(e.target.value) : e.target.value;
    setCard((c) => ({ ...c, [field]: value }));
  };

  const handlePay = async (e) => {
    e.preventDefault();
    if (method === 'CARD') {
      const found = validateCard(card);
      setErrors(found);
      if (hasErrors(found)) return;
    }

    setPlacing(true);
    setServerError('');
    try {
      const order = await orderService.placeOrder({
        addressId: state.addressId,
        paymentMethod: method,
        // Only card number + name go to the server (CVV/expiry are never sent)
        cardNumber: method === 'CARD' ? card.cardNumber.replace(/\s/g, '') : null,
        cardHolder: method === 'CARD' ? card.cardHolder : null,
      });
      placedRef.current = true;
      toast.success('Order placed successfully!');
      navigate(`/order-success/${order.id}`, { replace: true, state: { order } });
      refresh(); // the server emptied the cart; update the badge
    } catch (err) {
      setServerError(getErrorMessage(err));
    } finally {
      setPlacing(false);
    }
  };

  return (
    <div className="container page">
      <h1>Payment</h1>
      <div className="two-col">
        <form className="card stack" onSubmit={handlePay} noValidate>
          <div className="payment-methods">
            <label className={`address-option ${method === 'CARD' ? 'selected' : ''}`}>
              <input type="radio" name="method" checked={method === 'CARD'} onChange={() => setMethod('CARD')} />
              <span>Credit / Debit card</span>
            </label>
            <label className={`address-option ${method === 'COD' ? 'selected' : ''}`}>
              <input type="radio" name="method" checked={method === 'COD'} onChange={() => setMethod('COD')} />
              <span>Cash on delivery</span>
            </label>
          </div>

          {method === 'CARD' && (
            <>
              <div className="alert alert-info small">
                Simulated payment - no real money moves. Try <code>{TEST_CARD_SUCCESS}</code> (success)
                or <code>{TEST_CARD_DECLINE}</code> (declined). Any future expiry and any 3-digit CVV.
              </div>
              <div className="form-grid">
                <label className="field full">
                  <span>Card number</span>
                  <input inputMode="numeric" autoComplete="cc-number" value={card.cardNumber} onChange={set('cardNumber')} className={errors.cardNumber ? 'invalid' : ''} />
                  {errors.cardNumber && <small className="error-text">{errors.cardNumber}</small>}
                </label>
                <label className="field full">
                  <span>Name on card</span>
                  <input autoComplete="cc-name" value={card.cardHolder} onChange={set('cardHolder')} className={errors.cardHolder ? 'invalid' : ''} />
                  {errors.cardHolder && <small className="error-text">{errors.cardHolder}</small>}
                </label>
                <label className="field">
                  <span>Expiry (MM/YY)</span>
                  <input placeholder="12/29" autoComplete="cc-exp" maxLength={5} value={card.expiry} onChange={set('expiry')} className={errors.expiry ? 'invalid' : ''} />
                  {errors.expiry && <small className="error-text">{errors.expiry}</small>}
                </label>
                <label className="field">
                  <span>CVV</span>
                  <input type="password" inputMode="numeric" autoComplete="cc-csc" maxLength={4} value={card.cvv} onChange={set('cvv')} className={errors.cvv ? 'invalid' : ''} />
                  {errors.cvv && <small className="error-text">{errors.cvv}</small>}
                </label>
              </div>
            </>
          )}

          {method === 'COD' && <p className="muted">Pay in cash when your order is delivered.</p>}

          {serverError && <div className="alert alert-error">{serverError}</div>}

          <button type="submit" className="btn btn-primary btn-block" disabled={placing}>
            {placing ? 'Placing order...' : 'Place Order'}
          </button>
        </form>

        <PriceSummary summary={cart} title="You pay" />
      </div>
    </div>
  );
}
