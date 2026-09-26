import { useState } from 'react';
import adminService from '../../services/adminService';
import { getErrorMessage } from '../../services/api';
import useProducts from '../../hooks/useProducts';
import useToast from '../../hooks/useToast';
import useDocumentTitle from '../../hooks/useDocumentTitle';

const EMPTY = { name: '', description: '' };

export default function AdminCategories() {
  useDocumentTitle('Manage Categories');
  const { categories, refreshCategories } = useProducts();
  const toast = useToast();
  const [form, setForm] = useState(EMPTY);
  const [editingId, setEditingId] = useState(null);
  const [saving, setSaving] = useState(false);

  const reset = () => {
    setForm(EMPTY);
    setEditingId(null);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (form.name.trim().length < 2) {
      toast.error('Name must be at least 2 characters');
      return;
    }
    setSaving(true);
    try {
      if (editingId) {
        await adminService.updateCategory(editingId, form);
        toast.success('Category updated');
      } else {
        await adminService.createCategory(form);
        toast.success('Category created');
      }
      reset();
      refreshCategories();
    } catch (err) {
      toast.error(getErrorMessage(err));
    } finally {
      setSaving(false);
    }
  };

  const handleDelete = async (c) => {
    if (!window.confirm(`Delete category "${c.name}"?`)) return;
    try {
      await adminService.deleteCategory(c.id);
      toast.success('Category deleted');
      refreshCategories();
    } catch (err) {
      toast.error(getErrorMessage(err)); // e.g. "still has products"
    }
  };

  return (
    <div className="stack">
      <h1>Categories</h1>

      <form className="card form-grid" onSubmit={handleSubmit}>
        <label className="field">
          <span>{editingId ? 'Edit name' : 'New category name'}</span>
          <input value={form.name} onChange={(e) => setForm((f) => ({ ...f, name: e.target.value }))} />
        </label>
        <label className="field">
          <span>Description</span>
          <input value={form.description} onChange={(e) => setForm((f) => ({ ...f, description: e.target.value }))} />
        </label>
        <div className="form-actions full">
          {editingId && <button type="button" className="btn btn-outline" onClick={reset}>Cancel</button>}
          <button type="submit" className="btn btn-primary" disabled={saving}>
            {editingId ? 'Update category' : 'Add category'}
          </button>
        </div>
      </form>

      <div className="card table-wrap">
        <table className="table">
          <thead>
            <tr><th>Name</th><th>Slug</th><th>Description</th><th /></tr>
          </thead>
          <tbody>
            {categories.map((c) => (
              <tr key={c.id}>
                <td>{c.name}</td>
                <td className="muted">{c.slug}</td>
                <td className="muted">{c.description}</td>
                <td className="row-actions">
                  <button
                    type="button"
                    className="link-btn"
                    onClick={() => {
                      setEditingId(c.id);
                      setForm({ name: c.name, description: c.description || '' });
                    }}
                  >
                    Edit
                  </button>
                  <button type="button" className="link-btn danger" onClick={() => handleDelete(c)}>Delete</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
