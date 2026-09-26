import { createContext, useCallback, useEffect, useMemo, useState } from 'react';
import wishlistService from '../services/wishlistService';
import useAuth from '../hooks/useAuth';

export const WishlistContext = createContext(null);

export function WishlistProvider({ children }) {
  const { isAuthenticated } = useAuth();
  const [items, setItems] = useState([]);

  const refresh = useCallback(async () => {
    if (!isAuthenticated) {
      setItems([]);
      return;
    }
    try {
      setItems(await wishlistService.getWishlist());
    } catch {
      setItems([]);
    }
  }, [isAuthenticated]);

  useEffect(() => {
    refresh();
  }, [refresh]);

  const ids = useMemo(() => new Set(items.map((p) => p.id)), [items]);

  /** Adds if missing, removes if present. Returns true when now in wishlist. */
  const toggle = useCallback(async (productId) => {
    if (ids.has(productId)) {
      setItems(await wishlistService.remove(productId));
      return false;
    }
    setItems(await wishlistService.add(productId));
    return true;
  }, [ids]);

  const remove = useCallback(async (productId) => {
    setItems(await wishlistService.remove(productId));
  }, []);

  const value = useMemo(() => ({
    items,
    count: items.length,
    isInWishlist: (id) => ids.has(id),
    toggle,
    remove,
    refresh,
  }), [items, ids, toggle, remove, refresh]);

  return <WishlistContext.Provider value={value}>{children}</WishlistContext.Provider>;
}
