import ProductCard from './ProductCard';

export default function ProductGrid({ products, emptyText = 'No products found.' }) {
  if (!products?.length) {
    return <p className="empty-state">{emptyText}</p>;
  }
  return (
    <div className="product-grid">
      {products.map((p) => (
        <ProductCard key={p.id} product={p} />
      ))}
    </div>
  );
}
