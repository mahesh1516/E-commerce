import { createContext, useCallback, useEffect, useMemo, useState } from 'react';
import cartService from '../services/cartService';
import useAuth from '../hooks/useAuth';

export const CartContext = createContext(null);

/** The cart lives on the server; this keeps a copy for the UI. */
export function CartProvider({ children }) {
  const { isAuthenticated } = useAuth();
  const [cart, setCart] = useState(null);
  const [loading, setLoading] = useState(false);

  const refresh = useCallback(async () => {
    if (!isAuthenticated) {
      setCart(null);
      return;
    }
    setLoading(true);
    try {
      setCart(await cartService.getCart());
    } catch {
      setCart(null);
    } finally {
      setLoading(false);
    }
  }, [isAuthenticated]);

  // Reload whenever the user logs in or out
  useEffect(() => {
    refresh();
  }, [refresh]);

  const addToCart = async (productId, quantity = 1) => {
    const updated = await cartService.addItem(productId, quantity);
    setCart(updated);
    return updated;
  };

  const updateQuantity = async (itemId, quantity) => {
    setCart(await cartService.updateItem(itemId, quantity));
  };

  const removeItem = async (itemId) => {
    setCart(await cartService.removeItem(itemId));
  };

  const clearCart = async () => {
    setCart(await cartService.clearCart());
  };

  const value = useMemo(() => ({
    cart,
    loading,
    count: cart?.totalItems ?? 0,
    refresh,
    addToCart,
    updateQuantity,
    removeItem,
    clearCart,
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }), [cart, loading, refresh]);

  return <CartContext.Provider value={value}>{children}</CartContext.Provider>;
}
