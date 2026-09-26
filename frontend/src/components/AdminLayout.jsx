import { NavLink, Outlet } from 'react-router-dom';

const LINKS = [
  ['/admin', 'Dashboard', true],
  ['/admin/products', 'Products'],
  ['/admin/categories', 'Categories'],
  ['/admin/orders', 'Orders'],
  ['/admin/users', 'Users'],
  ['/admin/inventory', 'Inventory'],
];

/** Sidebar + the selected admin page (rendered by <Outlet />). */
export default function AdminLayout() {
  return (
    <div className="container admin-layout">
      <aside className="admin-sidebar card">
        <h3>Admin</h3>
        {LINKS.map(([to, label, end]) => (
          <NavLink key={to} to={to} end={end}>{label}</NavLink>
        ))}
      </aside>
      <section className="admin-content">
        <Outlet />
      </section>
    </div>
  );
}
