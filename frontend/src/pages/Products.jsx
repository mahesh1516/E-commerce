import { useCallback, useEffect, useMemo, useState } from 'react';
import { useSearchParams } from 'react-router-dom';
import ProductFilter from '../components/ProductFilter';
import ProductSort from '../components/ProductSort';
import ProductGrid from '../components/ProductGrid';
import Pagination from '../components/Pagination';
import LoadingSpinner from '../components/LoadingSpinner';
import ErrorMessage from '../components/ErrorMessage';
import productService from '../services/productService';
import { getErrorMessage } from '../services/api';
import { PAGE_SIZE } from '../utils/constants';
import useDocumentTitle from '../hooks/useDocumentTitle';

const FILTER_KEYS = ['search', 'category', 'brand', 'minPrice', 'maxPrice', 'discounted', 'sort', 'page'];

/**
 * All filters live in the URL (?category=mobiles&sort=price,asc&page=1)
 * so results can be bookmarked/shared and the Back button works.
 */
export default function Products() {
  useDocumentTitle('Products');
  const [params, setParams] = useSearchParams();
  const [result, setResult] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [showFilters, setShowFilters] = useState(false);

  const values = useMemo(() => {
    const v = {};
    FILTER_KEYS.forEach((k) => { v[k] = params.get(k) || ''; });
    return v;
  }, [params]);

  const page = Number(values.page || 0);
  const sort = values.sort || 'createdAt,desc';

  const load = useCallback(async () => {
    setLoading(true);
    setError('');
    try {
      const data = await productService.getProducts({ ...values, sort, page, size: PAGE_SIZE });
      setResult(data);
    } catch (err) {
      setError(getErrorMessage(err));
    } finally {
      setLoading(false);
    }
  }, [values, sort, page]);

  useEffect(() => {
    load();
  }, [load]);

  /** Merge changes into the URL. Any filter change resets to page 0. */
  const update = (changes, resetPage = true) => {
    const next = new URLSearchParams(params);
    Object.entries(changes).forEach(([k, v]) => {
      if (v === '' || v === null || v === undefined) next.delete(k);
      else next.set(k, v);
    });
    if (resetPage) next.delete('page');
    setParams(next);
  };

  const heading = values.search ? `Results for “${values.search}”` : 'All Products';

  return (
    <div className="container page">
      <div className="page-header">
        <div>
          <h1>{heading}</h1>
          {result && <p className="muted">{result.totalElements} product{result.totalElements !== 1 ? 's' : ''}</p>}
        </div>
        <div className="toolbar">
          <button type="button" className="btn btn-outline filter-toggle" onClick={() => setShowFilters((s) => !s)}>
            {showFilters ? 'Hide filters' : 'Filters'}
          </button>
          <ProductSort value={sort} onChange={(v) => update({ sort: v })} />
        </div>
      </div>

      <div className="products-layout">
        <div className={`filters-wrap ${showFilters ? 'open' : ''}`}>
          <ProductFilter
            values={values}
            onChange={(changes) => update(changes)}
            onClear={() => setParams(values.search ? { search: values.search } : {})}
          />
        </div>

        <div>
          {loading && <LoadingSpinner />}
          <ErrorMessage message={error} onRetry={load} />
          {!loading && !error && result && (
            <>
              <ProductGrid products={result.content} emptyText="No products match your filters." />
              <Pagination
                page={result.page}
                totalPages={result.totalPages}
                onChange={(p) => update({ page: p > 0 ? String(p) : '' }, false)}
              />
            </>
          )}
        </div>
      </div>
    </div>
  );
}
