import { Link } from 'react-router-dom';

export default function Footer() {
  return (
    <footer className="footer">
      <div className="container footer-grid">
        <div>
          <h4>NEXORA</h4>
          <p className="muted">Quality products, honest prices, fast delivery.</p>
        </div>
        <div>
          <h4>Shop</h4>
          <Link to="/products">All products</Link>
          <Link to="/products?discounted=true">Deals</Link>
          <Link to="/products?sort=createdAt,desc">New arrivals</Link>
        </div>
        <div>
          <h4>Account</h4>
          <Link to="/orders">My orders</Link>
          <Link to="/wishlist">Wishlist</Link>
          <Link to="/profile">Profile</Link>
        </div>
        <div>
          <h4>Help</h4>
          <p className="muted small">Free shipping on orders above ₹5,000.</p>
          <p className="muted small">Learning project - payments are simulated.</p>
        </div>
      </div>
      <p className="container copyright">© {new Date().getFullYear()} NEXORA E-Commerce</p>
    </footer>
  );
}
