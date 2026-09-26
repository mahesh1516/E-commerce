import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import ProductForm from '../../components/ProductForm';
import adminService from '../../services/adminService';
import { getErrorMessage, getFieldErrors } from '../../services/api';
import useProducts from '../../hooks/useProducts';
import useToast from '../../hooks/useToast';
import useDocumentTitle from '../../hooks/useDocumentTitle';

export default function AddProduct() {
  useDocumentTitle('Add Product');
  const navigate = useNavigate();
  const toast = useToast();
  const { refreshBrands } = useProducts();
  const [serverErrors, setServerErrors] = useState({});

  const handleSubmit = async (payload) => {
    try {
      await adminService.createProduct(payload);
      toast.success('Product created');
      refreshBrands();
      navigate('/admin/products');
    } catch (err) {
      setServerErrors(getFieldErrors(err));
      toast.error(getErrorMessage(err));
    }
  };

  return (
    <div className="stack">
      <h1>Add Product</h1>
      <ProductForm onSubmit={handleSubmit} submitLabel="Create product" serverErrors={serverErrors} />
    </div>
  );
}
