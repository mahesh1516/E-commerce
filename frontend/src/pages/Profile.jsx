import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import LoadingSpinner from '../components/LoadingSpinner';
import ErrorMessage from '../components/ErrorMessage';
import authService from '../services/authService';
import { getErrorMessage } from '../services/api';
import useAuth from '../hooks/useAuth';
import useToast from '../hooks/useToast';
import useDocumentTitle from '../hooks/useDocumentTitle';
import { formatDate } from '../utils/format';

export default function Profile() {
  useDocumentTitle('Profile');
  const { updateUsername } = useAuth();
  const toast = useToast();
  const [profile, setProfile] = useState(null);
  const [form, setForm] = useState({ name: '', phone: '' });
  const [error, setError] = useState('');
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    authService.getProfile()
      .then((p) => {
        setProfile(p);
        setForm({ name: p.name, phone: p.phone || '' });
      })
      .catch((err) => setError(getErrorMessage(err)));
  }, []);

  const handleSave = async (e) => {
    e.preventDefault();
    if (form.name.trim().length < 2) {
      toast.error('Name must be at least 2 characters');
      return;
    }
    setSaving(true);
    try {
      const updated = await authService.updateProfile(form);
      setProfile(updated);
      updateUsername(updated.name);
      toast.success('Profile updated');
    } catch (err) {
      toast.error(getErrorMessage(err));
    } finally {
      setSaving(false);
    }
  };

  if (error) return <div className="container page"><ErrorMessage message={error} /></div>;
  if (!profile) return <div className="container page"><LoadingSpinner /></div>;

  return (
    <div className="container page narrow">
      <h1>My Profile</h1>
      <form className="card stack" onSubmit={handleSave}>
        <p className="muted">
          {profile.email} · {profile.role} · member since {formatDate(profile.createdAt)}
        </p>
        <label className="field">
          <span>Name</span>
          <input value={form.name} onChange={(e) => setForm((f) => ({ ...f, name: e.target.value }))} />
        </label>
        <label className="field">
          <span>Phone</span>
          <input value={form.phone} onChange={(e) => setForm((f) => ({ ...f, phone: e.target.value }))} />
        </label>
        <button type="submit" className="btn btn-primary" disabled={saving}>
          {saving ? 'Saving...' : 'Save changes'}
        </button>
      </form>

      <div className="quick-links">
        <Link to="/orders" className="card">📦 My orders</Link>
        <Link to="/addresses" className="card">📍 Saved addresses</Link>
        <Link to="/wishlist" className="card">♡ Wishlist</Link>
      </div>
    </div>
  );
}
