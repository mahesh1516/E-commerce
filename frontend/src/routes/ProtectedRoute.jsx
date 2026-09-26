import { Navigate, Outlet, useLocation } from 'react-router-dom';
import useAuth from '../hooks/useAuth';

/** Only logged-in users may see the nested routes. */
export default function ProtectedRoute() {
  const { isAuthenticated } = useAuth();
  const location = useLocation();

  if (!isAuthenticated) {
    // Remember where they wanted to go, so Login can send them back
    return <Navigate to="/login" replace state={{ from: location }} />;
  }
  return <Outlet />;
}
