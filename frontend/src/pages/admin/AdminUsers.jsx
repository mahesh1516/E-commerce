import { useEffect, useState } from 'react';
import LoadingSpinner from '../../components/LoadingSpinner';
import ErrorMessage from '../../components/ErrorMessage';
import adminService from '../../services/adminService';
import { getErrorMessage } from '../../services/api';
import useDocumentTitle from '../../hooks/useDocumentTitle';
import { formatDate } from '../../utils/format';

export default function AdminUsers() {
  useDocumentTitle('Users');
  const [users, setUsers] = useState(null);
  const [error, setError] = useState('');

  useEffect(() => {
    adminService.getUsers()
      .then(setUsers)
      .catch((err) => setError(getErrorMessage(err)));
  }, []);

  if (error) return <ErrorMessage message={error} />;
  if (!users) return <LoadingSpinner />;

  return (
    <div className="stack">
      <h1>Users ({users.length})</h1>
      <div className="card table-wrap">
        <table className="table">
          <thead>
            <tr><th>Name</th><th>Email</th><th>Phone</th><th>Role</th><th>Joined</th></tr>
          </thead>
          <tbody>
            {users.map((u) => (
              <tr key={u.id}>
                <td>{u.name}</td>
                <td>{u.email}</td>
                <td>{u.phone || '-'}</td>
                <td><span className={`badge ${u.role === 'ADMIN' ? 'badge-admin' : ''}`}>{u.role}</span></td>
                <td>{formatDate(u.createdAt)}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
