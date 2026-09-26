import { useEffect, useState } from 'react';
import useProducts from '../hooks/useProducts';

/**
 * Sidebar filters. "values" come from the URL; onChange updates the URL.
 */
export default function ProductFilter({ values, onChange, onClear }) {
  const { categories, brands } = useProducts();
  const [minPrice, setMinPrice] = useState(values.minPrice || '');
  const [maxPrice, setMaxPrice] = useState(values.maxPrice || '');

  useEffect(() => {
    setMinPrice(values.minPrice || '');
    setMaxPrice(values.maxPrice || '');
  }, [values.minPrice, values.maxPrice]);

  const applyPrice = (e) => {
    e.preventDefault();
    onChange({ minPrice, maxPrice });
  };

  return (
    <aside className="filters card">
      <div className="filters-header">
        <h3>Filters</h3>
        <button type="button" className="link-btn" onClick={onClear}>Clear all</button>
      </div>

      <label className="field">
        <span>Category</span>
        <select value={values.category || ''} onChange={(e) => onChange({ category: e.target.value })}>
          <option value="">All categories</option>
          {categories.map((c) => (
            <option key={c.id} value={c.slug}>{c.name}</option>
          ))}
        </select>
      </label>

      <label className="field">
        <span>Brand</span>
        <select value={values.brand || ''} onChange={(e) => onChange({ brand: e.target.value })}>
          <option value="">All brands</option>
          {brands.map((b) => (
            <option key={b} value={b}>{b}</option>
          ))}
        </select>
      </label>

      <form onSubmit={applyPrice} className="field">
        <span>Price (₹)</span>
        <div className="price-inputs">
          <input type="number" min="0" placeholder="Min" value={minPrice} onChange={(e) => setMinPrice(e.target.value)} />
          <input type="number" min="0" placeholder="Max" value={maxPrice} onChange={(e) => setMaxPrice(e.target.value)} />
        </div>
        <button type="submit" className="btn btn-sm btn-outline">Apply price</button>
      </form>

      <label className="checkbox">
        <input
          type="checkbox"
          checked={values.discounted === 'true'}
          onChange={(e) => onChange({ discounted: e.target.checked ? 'true' : '' })}
        />
        <span>On sale only</span>
      </label>
    </aside>
  );
}
