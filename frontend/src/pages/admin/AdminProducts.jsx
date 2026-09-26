import { useCallback, useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import LoadingSpinner from '../../components/LoadingSpinner';
import ErrorMessage from '../../components/ErrorMessage';
import Pagination from '../../components/Pagination';
import ProductImage from '../../components/ProductImage';
import productService from '../../services/productService';
import adminService from '../../services/adminService';
import { getErrorMessage } from '../../services/api';
import useProducts from '../../hooks/useProducts';
import useToast from '../../hooks/useToast';
import useDocumentTitle from '../../hooks/useDocumentTitle';
import { formatPrice } from '../../utils/format';

export default function AdminProducts() {
  useDocumentTitle('Manage Products');
  const toast = useToast();
  const { refreshBrands } = useProducts();
  const [page, setPage] = useState(0);
  const [search, setSearch] = useState('');
  const [query, setQuery] = useState('');
  const [data, setData] = useState(null);
  const [error, setError] = useState('');

  const load = useCallback(async () => {
    setError('');
    try {
      setData(await productService.getProducts({ search: query, page, size: 10, sort: 'name,asc' }));
    } catch (err) {
      setError(getErrorMessage(err));
    }
  }, [page, query]);

  useEffect(() => {
    load();
  }, [load]);

  const handleDelete = async (product) => {
    if (!window.confirm(`Delete "${product.name}"? It will be hidden from the store.`)) return;
    try {
      await adminService.deleteProduct(product.id);
      toast.success('Product deleted');
      refreshBrands();
      load();
    } catch (err) {
      toast.error(getErrorMessage(err));
    }
  };

  return (
    <div className="stack">
      <div className="page-header">
        <h1>Products</h1>
        <Link to="/admin/products/add" className="btn btn-primary">+ Add product</Link>
      </div>

      <form
        className="search-bar"
        onSubmit={(e) => {
          e.preventDefault();
          setPage(0);
          setQuery(search.trim());
        }}
      >
        <input type="search" placeholder="Search by name or brand" value={search} onChange={(e) => setSearch(e.target.value)} />
        <button type="submit" className="btn btn-outline">Search</button>
      </form>

      <ErrorMessage message={error} onRetry={load} />
      {!data && !error && <LoadingSpinner />}

      {data && (
        <div className="card table-wrap">
          <table className="table">
            <thead>
              <tr><th /><th>Product</th><th>Category</th><th className="right">Price</th><th className="right">Stock</th><th /></tr>
            </thead>
            <tbody>
              {data.content.map((p) => (
                <tr key={p.id}>
                  <td><ProductImage src={p.imageUrl} name={p.name} className="thumb" /></td>
                  <td>{p.name}<br /><span className="muted small">{p.sku} · {p.brand}</span></td>
                  <td>{p.categoryName}</td>
                  <td className="right">
                    {formatPrice(p.effectivePrice)}
                    {p.discountPercent > 0 && <><br /><span className="price-old small">{formatPrice(p.price)}</span></>}
                  </td>
                  <td className={`right ${p.stock === 0 ? 'text-danger' : ''}`}>{p.stock}</td>
                  <td className="row-actions">
                    <Link to={`/admin/products/edit/${p.id}`} className="link-btn">Edit</Link>
                    <button type="button" className="link-btn danger" onClick={() => handleDelete(p)}>Delete</button>
                  </td>
                </tr>
              ))}
              {data.content.length === 0 && (
                <tr><td colSpan="6" className="center muted">No products found.</td></tr>
              )}
            </tbody>
          </table>
        </div>
      )}

      {data && <Pagination page={data.page} totalPages={data.totalPages} onChange={setPage} />}
    </div>
  );
}
