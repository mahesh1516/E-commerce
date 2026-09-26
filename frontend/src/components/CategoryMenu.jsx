import { Link } from 'react-router-dom';
import useProducts from '../hooks/useProducts';

const ICONS = {
  mobiles: '📱', laptops: '💻', electronics: '🎧', clothing: '👕',
  shoes: '👟', 'home-appliances': '🏠', books: '📚',
};

export default function CategoryMenu() {
  const { categories } = useProducts();
  if (!categories.length) return null;

  return (
    <div className="category-menu">
      {categories.map((c) => (
        <Link key={c.id} to={`/products?category=${c.slug}`} className="category-tile">
          <span className="category-icon" aria-hidden="true">{ICONS[c.slug] || '🛍️'}</span>
          <span>{c.name}</span>
        </Link>
      ))}
    </div>
  );
}
