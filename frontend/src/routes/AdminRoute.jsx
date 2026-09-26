import { Link, Navigate, Outlet, useLocation } from 'react-router-dom';
import useAuth from '../hooks/useAuth';

/**
 * Only ADMIN users may see admin pages.
 * NOTE: this only hides the UI - the backend enforces the real security.
 */
export default function AdminRoute() {
  const { isAuthenticated, isAdmin } = useAuth();
  const location = useLocation();

  if (!isAuthenticated) {
    return <Navigate to="/login" replace state={{ from: location }} />;
  }
  if (!isAdmin) {
    return (
      <div className="container page center">
        <h1>403 - Access denied</h1>
        <p className="muted">You need an admin account to view this page.</p>
        <Link to="/" className="btn btn-primary">Go home</Link>
      </div>
    );
  }
  return <Outlet />;
}
