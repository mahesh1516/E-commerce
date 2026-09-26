import { createContext, useCallback, useEffect, useMemo, useState } from 'react';
import authService from '../services/authService';
import { clearStoredAuth, getStoredAuth, storeAuth } from '../services/api';

export const AuthContext = createContext(null);

/**
 * Holds the logged-in user + JWT.
 * Saved in localStorage so a page refresh keeps you logged in.
 */
export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => getStoredAuth());

  const saveSession = (data) => {
    const auth = {
      token: data.token,
      userId: data.userId,
      username: data.username,
      email: data.email,
      role: data.role,
      expiresAt: Date.now() + data.expiresInMs,
    };
    storeAuth(auth);
    setUser(auth);
    return auth;
  };

  const login = async (credentials) => saveSession(await authService.login(credentials));

  const register = async (data) => saveSession(await authService.register(data));

  // JWT is stateless: logging out = forgetting the token
  const logout = useCallback(() => {
    clearStoredAuth();
    setUser(null);
  }, []);

  const updateUsername = (username) => {
    setUser((current) => {
      if (!current) return current;
      const updated = { ...current, username };
      storeAuth(updated);
      return updated;
    });
  };

  // The Axios interceptor fires this event on 401
  useEffect(() => {
    const handler = () => setUser(null);
    window.addEventListener('nexora:logout', handler);
    return () => window.removeEventListener('nexora:logout', handler);
  }, []);

  // Auto-logout exactly when the token expires
  useEffect(() => {
    if (!user?.expiresAt) return undefined;
    const timer = setTimeout(logout, Math.max(user.expiresAt - Date.now(), 0));
    return () => clearTimeout(timer);
  }, [user, logout]);

  const value = useMemo(() => ({
    user,
    isAuthenticated: Boolean(user),
    isAdmin: user?.role === 'ADMIN',
    login,
    register,
    logout,
    updateUsername,
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }), [user, logout]);

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}
