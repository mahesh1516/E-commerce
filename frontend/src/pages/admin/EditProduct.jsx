import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import ProductForm from '../../components/ProductForm';
import LoadingSpinner from '../../components/LoadingSpinner';
import ErrorMessage from '../../components/ErrorMessage';
import productService from '../../services/productService';
import adminService from '../../services/adminService';
import { getErrorMessage, getFieldErrors } from '../../services/api';
import useProducts from '../../hooks/useProducts';
import useToast from '../../hooks/useToast';
import useDocumentTitle from '../../hooks/useDocumentTitle';

/** Converts the API product into the string values the form uses. */
function toFormValues(p) {
  return {
    name: p.name,
    brand: p.brand || '',
    sku: p.sku,
    categoryId: String(p.categoryId),
    price: String(p.price),
    discountPrice: p.discountPrice != null ? String(p.discountPrice) : '',
    stock: String(p.stock),
    imageUrl: p.imageUrl || '',
    imageUrls: p.images.join('\n'),
    description: p.description || '',
    specifications: p.specifications || '',
  };
}

export default function EditProduct() {
  useDocumentTitle('Edit Product');
  const { id } = useParams();
  const navigate = useNavigate();
  const toast = useToast();
  const { refreshBrands } = useProducts();
  const [initial, setInitial] = useState(null);
  const [error, setError] = useState('');
  const [serverErrors, setServerErrors] = useState({});

  useEffect(() => {
    productService.getProduct(id)
      .then((p) => setInitial(toFormValues(p)))
      .catch((err) => setError(getErrorMessage(err)));
  }, [id]);

  const handleSubmit = async (payload) => {
    try {
      await adminService.updateProduct(id, payload);
      toast.success('Product updated');
      refreshBrands();
      navigate('/admin/products');
    } catch (err) {
      setServerErrors(getFieldErrors(err));
      toast.error(getErrorMessage(err));
    }
  };

  if (error) return <ErrorMessage message={error} />;
  if (!initial) return <LoadingSpinner />;

  return (
    <div className="stack">
      <h1>Edit Product</h1>
      <ProductForm initial={initial} onSubmit={handleSubmit} submitLabel="Update product" serverErrors={serverErrors} />
    </div>
  );
}
