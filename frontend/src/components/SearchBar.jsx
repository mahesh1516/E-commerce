import { useEffect, useState } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';

export default function SearchBar({ onSearch }) {
  const navigate = useNavigate();
  const [params] = useSearchParams();
  const [term, setTerm] = useState(params.get('search') || '');

  // Keep the box in sync when the URL changes
  useEffect(() => {
    setTerm(params.get('search') || '');
  }, [params]);

  const handleSubmit = (e) => {
    e.preventDefault();
    const q = term.trim();
    navigate(q ? `/products?search=${encodeURIComponent(q)}` : '/products');
    onSearch?.();
  };

  return (
    <form className="search-bar" onSubmit={handleSubmit} role="search">
      <input
        type="search"
        placeholder="Search products, brands..."
        value={term}
        onChange={(e) => setTerm(e.target.value)}
        aria-label="Search products"
      />
      <button type="submit" className="btn btn-primary">Search</button>
    </form>
  );
}
