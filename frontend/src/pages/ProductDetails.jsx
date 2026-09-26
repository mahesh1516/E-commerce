import { useCallback, useEffect, useState } from 'react';
import { Link, useLocation, useNavigate, useParams } from 'react-router-dom';
import ProductImage from '../components/ProductImage';
import StarRating from '../components/StarRating';
import LoadingSpinner from '../components/LoadingSpinner';
import ErrorMessage from '../components/ErrorMessage';
import productService from '../services/productService';
import reviewService from '../services/reviewService';
import { getErrorMessage } from '../services/api';
import useAuth from '../hooks/useAuth';
import useCart from '../hooks/useCart';
import useWishlist from '../hooks/useWishlist';
import useToast from '../hooks/useToast';
import useDocumentTitle from '../hooks/useDocumentTitle';
import { formatDate, formatPrice, parseSpecifications } from '../utils/format';
import { hasErrors, validateReview } from '../utils/validators';

export default function ProductDetails() {
  const { id } = useParams();
  const navigate = useNavigate();
  const location = useLocation();
  const { isAuthenticated } = useAuth();
  const { addToCart } = useCart();
  const { isInWishlist, toggle } = useWishlist();
  const toast = useToast();

  const [product, setProduct] = useState(null);
  const [reviews, setReviews] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [activeImage, setActiveImage] = useState(null);
  const [quantity, setQuantity] = useState(1);
  const [busy, setBusy] = useState(false);
  const [review, setReview] = useState({ rating: 0, comment: '' });
  const [reviewError, setReviewError] = useState('');

  useDocumentTitle(product?.name || 'Product');

  const load = useCallback(async () => {
    setLoading(true);
    setError('');
    try {
      const [p, r] = await Promise.all([productService.getProduct(id), reviewService.getReviews(id)]);
      setProduct(p);
      setReviews(r);
      setActiveImage(p.imageUrl);
      setQuantity(1);
    } catch (err) {
      setError(err.response?.status === 404 ? 'Product not found' : getErrorMessage(err));
    } finally {
      setLoading(false);
    }
  }, [id]);

  useEffect(() => {
    load();
  }, [load]);

  const requireLogin = () => {
    toast.info('Please log in first');
    navigate('/login', { state: { from: location } });
  };

  const handleAddToCart = async (goToCheckout) => {
    if (!isAuthenticated) return requireLogin();
    setBusy(true);
    try {
      await addToCart(product.id, quantity);
      if (goToCheckout) {
        navigate('/checkout');
      } else {
        toast.success('Added to cart');
      }
    } catch (err) {
      toast.error(getErrorMessage(err));
    } finally {
      setBusy(false);
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

  const submitReview = async (e) => {
    e.preventDefault();
    const errors = validateReview(review);
    if (hasErrors(errors)) {
      setReviewError(Object.values(errors)[0]);
      return;
    }
    setReviewError('');
    try {
      await reviewService.addReview(product.id, review);
      toast.success('Thanks for your review!');
      setReview({ rating: 0, comment: '' });
      load(); // refresh rating + list
    } catch (err) {
      setReviewError(getErrorMessage(err));
    }
  };

  if (loading) return <div className="container page"><LoadingSpinner /></div>;
  if (error) {
    return (
      <div className="container page">
        <ErrorMessage message={error} />
        <Link to="/products" className="btn btn-outline">Back to products</Link>
      </div>
    );
  }

  const gallery = [product.imageUrl, ...product.images].filter(Boolean);
  const specs = parseSpecifications(product.specifications);
  const maxQty = Math.min(10, product.stock);

  return (
    <div className="container page">
      <nav className="breadcrumb">
        <Link to="/">Home</Link> / <Link to={`/products?category=${product.categorySlug}`}>{product.categoryName}</Link> / <span>{product.name}</span>
      </nav>

      <div className="details-layout">
        <div className="gallery">
          <ProductImage src={activeImage} name={product.name} className="gallery-main" />
          {gallery.length > 1 && (
            <div className="gallery-thumbs">
              {gallery.map((url) => (
                <button key={url} type="button" onClick={() => setActiveImage(url)} className={url === activeImage ? 'active' : ''}>
                  <ProductImage src={url} name={product.name} />
                </button>
              ))}
            </div>
          )}
        </div>

        <div className="details-info">
          <span className="product-brand">{product.brand}</span>
          <h1>{product.name}</h1>
          <StarRating value={product.rating} count={product.reviewCount} />

          <div className="price-line big">
            <span className="price">{formatPrice(product.effectivePrice)}</span>
            {product.discountPercent > 0 && (
              <>
                <span className="price-old">{formatPrice(product.price)}</span>
                <span className="discount-text">{product.discountPercent}% off</span>
              </>
            )}
          </div>

          {product.inStock ? (
            <p className={product.stock <= 5 ? 'stock-label low' : 'stock-label in'}>
              {product.stock <= 5 ? `Hurry, only ${product.stock} left!` : 'In stock'}
            </p>
          ) : (
            <p className="stock-label out">Out of stock</p>
          )}

          <p>{product.description}</p>

          {product.inStock && (
            <div className="buy-box">
              <label className="field inline">
                <span>Qty</span>
                <select value={quantity} onChange={(e) => setQuantity(Number(e.target.value))}>
                  {Array.from({ length: maxQty }, (_, i) => i + 1).map((n) => (
                    <option key={n} value={n}>{n}</option>
                  ))}
                </select>
              </label>
              <button type="button" className="btn btn-primary" disabled={busy} onClick={() => handleAddToCart(false)}>
                Add to Cart
              </button>
              <button type="button" className="btn btn-accent" disabled={busy} onClick={() => handleAddToCart(true)}>
                Buy Now
              </button>
            </div>
          )}

          <button type="button" className="btn btn-outline" onClick={handleWishlist}>
            {isInWishlist(product.id) ? '♥ In wishlist' : '♡ Add to wishlist'}
          </button>

          {specs.length > 0 && (
            <div className="specs">
              <h3>Specifications</h3>
              <table>
                <tbody>
                  {specs.map(([k, v]) => (
                    <tr key={k}><th>{k}</th><td>{v}</td></tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      </div>

      <section className="section reviews">
        <h2>Customer Reviews ({reviews.length})</h2>

        {isAuthenticated ? (
          <form className="card review-form" onSubmit={submitReview}>
            <h3>Write a review</h3>
            <StarRating value={review.rating} onChange={(rating) => setReview((r) => ({ ...r, rating }))} size="lg" />
            <textarea
              rows="3"
              placeholder="Share your experience (optional)"
              value={review.comment}
              onChange={(e) => setReview((r) => ({ ...r, comment: e.target.value }))}
            />
            {reviewError && <small className="error-text">{reviewError}</small>}
            <button type="submit" className="btn btn-primary">Submit review</button>
          </form>
        ) : (
          <p className="muted"><Link to="/login" state={{ from: location }}>Log in</Link> to write a review.</p>
        )}

        {reviews.length === 0 && <p className="empty-state">No reviews yet. Be the first!</p>}
        {reviews.map((r) => (
          <div key={r.id} className="review card">
            <div className="review-head">
              <strong>{r.userName}</strong>
              <StarRating value={r.rating} />
              <span className="muted small">{formatDate(r.createdAt)}</span>
            </div>
            {r.comment && <p>{r.comment}</p>}
          </div>
        ))}
      </section>
    </div>
  );
}
