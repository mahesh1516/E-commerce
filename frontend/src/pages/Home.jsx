import { useCallback, useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import CategoryMenu from '../components/CategoryMenu';
import ProductGrid from '../components/ProductGrid';
import LoadingSpinner from '../components/LoadingSpinner';
import ErrorMessage from '../components/ErrorMessage';
import StarRating from '../components/StarRating';
import productService from '../services/productService';
import { getErrorMessage } from '../services/api';
import useDocumentTitle from '../hooks/useDocumentTitle';

const SECTIONS = [
  { key: 'featured', title: 'Featured Products', params: { sort: 'reviewCount,desc' }, link: '/products' },
  { key: 'best', title: 'Best Sellers', params: { sort: 'rating,desc' }, link: '/products?sort=rating,desc' },
  { key: 'deals', title: 'Deals of the Day', params: { discounted: true, sort: 'price,asc' }, link: '/products?discounted=true' },
  { key: 'new', title: 'New Arrivals', params: { sort: 'createdAt,desc' }, link: '/products?sort=createdAt,desc' },
];

// Static sample testimonials for the homepage
const TESTIMONIALS = [
  { name: 'Priya S.', city: 'Hyderabad', rating: 5, text: 'Delivery was quick and the headphones are exactly as described.' },
  { name: 'Arjun M.', city: 'Pune', rating: 4, text: 'Great prices on laptops. Checkout was smooth and simple.' },
  { name: 'Neha R.', city: 'Chennai', rating: 5, text: 'Love the deals section - picked up two books at half price!' },
];

export default function Home() {
  useDocumentTitle('Home');
  const [data, setData] = useState({});
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const load = useCallback(async () => {
    setLoading(true);
    setError('');
    try {
      // Load all sections in parallel
      const results = await Promise.all(
        SECTIONS.map((s) => productService.getProducts({ ...s.params, size: 4 }))
      );
      const next = {};
      SECTIONS.forEach((s, i) => { next[s.key] = results[i].content; });
      setData(next);
    } catch (err) {
      setError(getErrorMessage(err));
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    load();
  }, [load]);

  return (
    <>
      <section className="hero">
        <div className="container hero-inner">
          <div>
            <h1>Everything you love, delivered.</h1>
            <p>Phones, laptops, fashion, books and more - with free shipping above ₹5,000.</p>
            <div className="hero-actions">
              <Link to="/products" className="btn btn-light">Shop now</Link>
              <Link to="/products?discounted=true" className="btn btn-ghost">View deals</Link>
            </div>
          </div>
        </div>
      </section>

      <div className="container page">
        <section className="section">
          <h2>Shop by Category</h2>
          <CategoryMenu />
        </section>

        {loading && <LoadingSpinner text="Loading products..." />}
        <ErrorMessage message={error} onRetry={load} />

        {!loading && !error && SECTIONS.map((s) => (
          <section key={s.key} className="section">
            <div className="section-header">
              <h2>{s.title}</h2>
              <Link to={s.link}>View all →</Link>
            </div>
            <ProductGrid products={data[s.key]} />
          </section>
        ))}

        <section className="section">
          <h2>What Our Customers Say</h2>
          <div className="testimonials">
            {TESTIMONIALS.map((t) => (
              <blockquote key={t.name} className="card testimonial">
                <StarRating value={t.rating} />
                <p>“{t.text}”</p>
                <footer className="muted">— {t.name}, {t.city}</footer>
              </blockquote>
            ))}
          </div>
        </section>
      </div>
    </>
  );
}
