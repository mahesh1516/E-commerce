import { useEffect } from 'react';
import { Link } from 'react-router-dom';
import ProductGrid from '../components/ProductGrid';
import useWishlist from '../hooks/useWishlist';
import useDocumentTitle from '../hooks/useDocumentTitle';

export default function Wishlist() {
  useDocumentTitle('Wishlist');
  const { items, refresh } = useWishlist();

  useEffect(() => {
    refresh();
  }, [refresh]);

  return (
    <div className="container page">
      <h1>My Wishlist ({items.length})</h1>
      {items.length === 0 ? (
        <div className="center">
          <p className="empty-state">Your wishlist is empty. Tap ♡ on any product to save it here.</p>
          <Link to="/products" className="btn btn-primary">Browse products</Link>
        </div>
      ) : (
        <ProductGrid products={items} />
      )}
    </div>
  );
}
