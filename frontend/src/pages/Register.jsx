import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import useAuth from '../hooks/useAuth';
import useToast from '../hooks/useToast';
import { getErrorMessage, getFieldErrors } from '../services/api';
import { hasErrors, validateRegister } from '../utils/validators';
import useDocumentTitle from '../hooks/useDocumentTitle';

const FIELDS = [
  ['name', 'Full name', 'text', 'name'],
  ['email', 'Email', 'email', 'email'],
  ['phone', 'Phone (optional)', 'tel', 'tel'],
  ['password', 'Password (min 8 characters)', 'password', 'new-password'],
  ['confirmPassword', 'Confirm password', 'password', 'new-password'],
];

export default function Register() {
  useDocumentTitle('Register');
  const { register } = useAuth();
  const toast = useToast();
  const navigate = useNavigate();

  const [form, setForm] = useState({ name: '', email: '', phone: '', password: '', confirmPassword: '' });
  const [errors, setErrors] = useState({});
  const [serverError, setServerError] = useState('');
  const [loading, setLoading] = useState(false);

  const set = (field) => (e) => setForm((f) => ({ ...f, [field]: e.target.value }));

  const handleSubmit = async (e) => {
    e.preventDefault();
    const found = validateRegister(form);
    setErrors(found);
    if (hasErrors(found)) return;

    setLoading(true);
    setServerError('');
    try {
      const { confirmPassword, ...payload } = form; // never send confirmPassword
      await register(payload);
      toast.success('Account created - welcome to NEXORA!');
      navigate('/', { replace: true });
    } catch (err) {
      setErrors(getFieldErrors(err));
      setServerError(getErrorMessage(err));
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container page auth-page">
      <form className="card auth-card" onSubmit={handleSubmit} noValidate>
        <h1>Create account</h1>
        {serverError && <div className="alert alert-error">{serverError}</div>}

        {FIELDS.map(([field, label, type, autoComplete]) => (
          <label key={field} className="field">
            <span>{label}</span>
            <input
              type={type}
              autoComplete={autoComplete}
              value={form[field]}
              onChange={set(field)}
              className={errors[field] ? 'invalid' : ''}
            />
            {errors[field] && <small className="error-text">{errors[field]}</small>}
          </label>
        ))}

        <button type="submit" className="btn btn-primary btn-block" disabled={loading}>
          {loading ? 'Creating account...' : 'Register'}
        </button>
        <p className="muted center">Already have an account? <Link to="/login">Login</Link></p>
      </form>
    </div>
  );
}
