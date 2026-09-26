import { useState } from 'react';
import { hasErrors, validateAddress } from '../utils/validators';

const EMPTY = {
  name: '', phone: '', addressLine1: '', addressLine2: '',
  city: '', state: '', postalCode: '', country: 'India', defaultAddress: false,
};

const FIELDS = [
  ['name', 'Full name'], ['phone', 'Phone'], ['addressLine1', 'Address line 1'],
  ['addressLine2', 'Address line 2 (optional)'], ['city', 'City'], ['state', 'State'],
  ['postalCode', 'Postal code'], ['country', 'Country'],
];

export default function AddressForm({ initial, onSubmit, onCancel, submitLabel = 'Save address' }) {
  const [form, setForm] = useState({ ...EMPTY, ...initial });
  const [errors, setErrors] = useState({});
  const [saving, setSaving] = useState(false);

  const set = (field) => (e) => {
    const value = e.target.type === 'checkbox' ? e.target.checked : e.target.value;
    setForm((f) => ({ ...f, [field]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const found = validateAddress(form);
    setErrors(found);
    if (hasErrors(found)) return;
    setSaving(true);
    try {
      await onSubmit(form);
    } finally {
      setSaving(false);
    }
  };

  return (
    <form className="card form-grid" onSubmit={handleSubmit} noValidate>
      {FIELDS.map(([field, label]) => (
        <label key={field} className="field">
          <span>{label}</span>
          <input value={form[field] || ''} onChange={set(field)} className={errors[field] ? 'invalid' : ''} />
          {errors[field] && <small className="error-text">{errors[field]}</small>}
        </label>
      ))}
      <label className="checkbox full">
        <input type="checkbox" checked={Boolean(form.defaultAddress)} onChange={set('defaultAddress')} />
        <span>Make this my default address</span>
      </label>
      <div className="form-actions full">
        {onCancel && (
          <button type="button" className="btn btn-outline" onClick={onCancel}>Cancel</button>
        )}
        <button type="submit" className="btn btn-primary" disabled={saving}>
          {saving ? 'Saving...' : submitLabel}
        </button>
      </div>
    </form>
  );
}
