import { useState } from 'react';
import useProducts from '../hooks/useProducts';
import { hasErrors, validateProduct } from '../utils/validators';

const EMPTY = {
  name: '', brand: '', sku: '', categoryId: '', price: '', discountPrice: '', stock: '',
  imageUrl: '', imageUrls: '', description: '', specifications: '',
};

/** Shared by Add Product and Edit Product. */
export default function ProductForm({ initial, onSubmit, submitLabel = 'Save product', serverErrors = {} }) {
  const { categories } = useProducts();
  const [form, setForm] = useState({ ...EMPTY, ...initial });
  const [errors, setErrors] = useState({});
  const [saving, setSaving] = useState(false);

  const set = (field) => (e) => setForm((f) => ({ ...f, [field]: e.target.value }));
  const allErrors = { ...serverErrors, ...errors };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const found = validateProduct(form);
    setErrors(found);
    if (hasErrors(found)) return;

    // Convert form strings into the JSON the API expects
    const payload = {
      name: form.name.trim(),
      brand: form.brand.trim(),
      sku: form.sku.trim(),
      categoryId: Number(form.categoryId),
      price: Number(form.price),
      discountPrice: form.discountPrice === '' ? null : Number(form.discountPrice),
      stock: Number(form.stock),
      imageUrl: form.imageUrl.trim() || null,
      imageUrls: form.imageUrls.split('\n').map((u) => u.trim()).filter(Boolean),
      description: form.description,
      specifications: form.specifications,
    };

    setSaving(true);
    try {
      await onSubmit(payload);
    } finally {
      setSaving(false);
    }
  };

  const input = (field, label, props = {}) => (
    <label className="field">
      <span>{label}</span>
      <input value={form[field]} onChange={set(field)} className={allErrors[field] ? 'invalid' : ''} {...props} />
      {allErrors[field] && <small className="error-text">{allErrors[field]}</small>}
    </label>
  );

  return (
    <form className="card form-grid" onSubmit={handleSubmit} noValidate>
      {input('name', 'Product name')}
      {input('brand', 'Brand')}
      {input('sku', 'SKU (unique code)')}

      <label className="field">
        <span>Category</span>
        <select value={form.categoryId} onChange={set('categoryId')} className={allErrors.categoryId ? 'invalid' : ''}>
          <option value="">Choose...</option>
          {categories.map((c) => (
            <option key={c.id} value={c.id}>{c.name}</option>
          ))}
        </select>
        {allErrors.categoryId && <small className="error-text">{allErrors.categoryId}</small>}
      </label>

      {input('price', 'Price (₹)', { type: 'number', min: '0', step: '0.01' })}
      {input('discountPrice', 'Discount price (₹, optional)', { type: 'number', min: '0', step: '0.01' })}
      {input('stock', 'Stock quantity', { type: 'number', min: '0' })}
      {input('imageUrl', 'Main image URL (optional)')}

      <label className="field full">
        <span>Extra image URLs (one per line)</span>
        <textarea rows="2" value={form.imageUrls} onChange={set('imageUrls')} />
      </label>

      <label className="field full">
        <span>Description</span>
        <textarea rows="3" value={form.description} onChange={set('description')} />
      </label>

      <label className="field full">
        <span>Specifications (one "Key: Value" per line)</span>
        <textarea rows="4" value={form.specifications} onChange={set('specifications')} placeholder={'RAM: 8 GB\nStorage: 256 GB'} />
      </label>

      <div className="form-actions full">
        <button type="submit" className="btn btn-primary" disabled={saving}>
          {saving ? 'Saving...' : submitLabel}
        </button>
      </div>
    </form>
  );
}
