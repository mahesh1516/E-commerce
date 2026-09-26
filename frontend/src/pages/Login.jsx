import { useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import useAuth from '../hooks/useAuth';
import useToast from '../hooks/useToast';
import { getErrorMessage } from '../services/api';
import { hasErrors, validateLogin } from '../utils/validators';
import useDocumentTitle from '../hooks/useDocumentTitle';

export default function Login() {
  useDocumentTitle('Login');
  const { login } = useAuth();
  const toast = useToast();
  const navigate = useNavigate();
  const location = useLocation();

  const [form, setForm] = useState({ email: '', password: '' });
  const [errors, setErrors] = useState({});
  const [serverError, setServerError] = useState('');
  const [loading, setLoading] = useState(false);

  const set = (field) => (e) => setForm((f) => ({ ...f, [field]: e.target.value }));

  const handleSubmit = async (e) => {
    e.preventDefault();
    const found = validateLogin(form);
    setErrors(found);
    if (hasErrors(found)) return;

    setLoading(true);
    setServerError('');
    try {
      const user = await login(form);
      toast.success(`Welcome back, ${user.username}!`);
      // Go back to the page they came from, or a sensible default
      const from = location.state?.from?.pathname;
      navigate(from || (user.role === 'ADMIN' ? '/admin' : '/'), { replace: true });
    } catch (err) {
      setServerError(getErrorMessage(err));
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container page auth-page">
      <form className="card auth-card" onSubmit={handleSubmit} noValidate>
        <h1>Login</h1>
        {serverError && <div className="alert alert-error">{serverError}</div>}

        <label className="field">
          <span>Email</span>
          <input type="email" autoComplete="email" value={form.email} onChange={set('email')} className={errors.email ? 'invalid' : ''} />
          {errors.email && <small className="error-text">{errors.email}</small>}
        </label>

        <label className="field">
          <span>Password</span>
          <input type="password" autoComplete="current-password" value={form.password} onChange={set('password')} className={errors.password ? 'invalid' : ''} />
          {errors.password && <small className="error-text">{errors.password}</small>}
        </label>

        <button type="submit" className="btn btn-primary btn-block" disabled={loading}>
          {loading ? 'Logging in...' : 'Login'}
        </button>

        <p className="muted center">New to NEXORA? <Link to="/register">Create an account</Link></p>

        {import.meta.env.DEV && (
          <p className="dev-hint">
            Dev-only sample accounts: user@nexora.com / User@123 · admin@nexora.com / Admin@123
          </p>
        )}
      </form>
    </div>
  );
}
