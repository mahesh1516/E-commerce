import { useState } from 'react';
import { Link, NavLink, useNavigate } from 'react-router-dom';
import SearchBar from './SearchBar';
import useAuth from '../hooks/useAuth';
import useCart from '../hooks/useCart';
import useWishlist from '../hooks/useWishlist';

export default function Navbar() {
  const { user, isAuthenticated, isAdmin, logout } = useAuth();
  const { count } = useCart();
  const { count: wishCount } = useWishlist();
  const navigate = useNavigate();
  const [open, setOpen] = useState(false);

  const close = () => setOpen(false);

  const handleLogout = () => {
    logout();
    close();
    navigate('/');
  };

  return (
    <header className="navbar">
      <div className="container navbar-inner">
        <Link to="/" className="logo" onClick={close}>NEXORA</Link>

        <div className="navbar-search">
          <SearchBar onSearch={close} />
        </div>

        <button
          type="button"
          className="menu-toggle"
          onClick={() => setOpen((o) => !o)}
          aria-label="Toggle menu"
          aria-expanded={open}
        >
          ☰
        </button>

        <nav className={`nav-links ${open ? 'open' : ''}`} onClick={close}>
          <NavLink to="/products">Products</NavLink>
          {isAuthenticated ? (
            <>
              <NavLink to="/wishlist">Wishlist{wishCount > 0 && <span className="pill">{wishCount}</span>}</NavLink>
              <NavLink to="/cart">Cart{count > 0 && <span className="pill">{count}</span>}</NavLink>
              <NavLink to="/orders">Orders</NavLink>
              {isAdmin && <NavLink to="/admin">Admin</NavLink>}
              <NavLink to="/profile" className="nav-user">Hi, {user.username.split(' ')[0]}</NavLink>
              <button type="button" className="btn btn-sm btn-outline" onClick={handleLogout}>Logout</button>
            </>
          ) : (
            <>
              <NavLink to="/login">Login</NavLink>
              <Link to="/register" className="btn btn-sm btn-primary">Sign up</Link>
            </>
          )}
        </nav>
      </div>
    </header>
  );
}
