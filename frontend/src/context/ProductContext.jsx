import { createContext, useCallback, useEffect, useMemo, useState } from 'react';
import productService from '../services/productService';

export const ProductContext = createContext(null);

/** Categories and brands are needed on many pages, so load them once. */
export function ProductProvider({ children }) {
  const [categories, setCategories] = useState([]);
  const [brands, setBrands] = useState([]);

  const refreshCategories = useCallback(async () => {
    try {
      setCategories(await productService.getCategories());
    } catch {
      setCategories([]);
    }
  }, []);

  const refreshBrands = useCallback(async () => {
    try {
      setBrands(await productService.getBrands());
    } catch {
      setBrands([]);
    }
  }, []);

  useEffect(() => {
    refreshCategories();
    refreshBrands();
  }, [refreshCategories, refreshBrands]);

  const value = useMemo(
    () => ({ categories, brands, refreshCategories, refreshBrands }),
    [categories, brands, refreshCategories, refreshBrands]
  );

  return <ProductContext.Provider value={value}>{children}</ProductContext.Provider>;
}
