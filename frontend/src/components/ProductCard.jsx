import { useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import ProductImage from './ProductImage';
import StarRating from './StarRating';
import useAuth from '../hooks/useAuth';
import useCart from '../hooks/useCart';
import useWishlist from '../hooks/useWishlist';
import useToast from '../hooks/useToast';
import { getErrorMessage } from '../services/api';
import { formatPrice } from '../utils/format';

export default function ProductCard({ product }) {
  const { isAuthenticated } = useAuth();
  const { addToCart } = useCart();
  const { isInWishlist, toggle } = useWishlist();
  const toast = useToast();
  const navigate = useNavigate();
  const location = useLocation();
  const [adding, setAdding] = useState(false);

  const requireLogin = () => {
    toast.info('Please log in first');
    navigate('/login', { state: { from: location } });
  };

  const handleAdd = async () => {
    if (!isAuthenticated) return requireLogin();
    setAdding(true);
    try {
      await addToCart(product.id, 1);
      toast.success(`${product.name} added to cart`);
    } catch (err) {
      toast.error(getErrorMessage(err));
    } finally {
      setAdding(false);
    }
  };

  const handleWishlist = async () => {
    if (!isAuthenticated) return requireLogin();
    try {
      const added = await toggle(product.id);
      toast.success(added ? 'Added to wishlist' : 'Removed from wishlist');
    } catch (err) {
      toast.error(getErrorMessage(err));
    }
  };

  const wished = isInWishlist(product.id);

  return (
    <article className="product-card">
      <Link to={`/products/${product.id}`} className="product-card-image">
        <ProductImage src={product.imageUrl} name={product.name} />
        {product.discountPercent > 0 && <span className="discount-tag">{product.discountPercent}% OFF</span>}
      </Link>

      <button
        type="button"
        className={`wishlist-btn ${wished ? 'active' : ''}`}
        onClick={handleWishlist}
        aria-label={wished ? 'Remove from wishlist' : 'Add to wishlist'}
      >
        {wished ? '♥' : '♡'}
      </button>

      <div className="product-card-body">
        <span className="product-brand">{product.brand}</span>
        <Link to={`/products/${product.id}`} className="product-name">{product.name}</Link>
        <StarRating value={product.rating} count={product.reviewCount} />
        <div className="price-line">
          <span className="price">{formatPrice(product.effectivePrice)}</span>
          {product.discountPercent > 0 && <span className="price-old">{formatPrice(product.price)}</span>}
        </div>
        {!product.inStock && <span className="stock-label out">Out of stock</span>}
        {product.inStock && product.stock <= 5 && (
          <span className="stock-label low">Only {product.stock} left</span>
        )}
        <button
          type="button"
          className="btn btn-primary btn-block"
          disabled={!product.inStock || adding}
          onClick={handleAdd}
        >
          {adding ? 'Adding...' : 'Add to Cart'}
        </button>
      </div>
    </article>
  );
}
