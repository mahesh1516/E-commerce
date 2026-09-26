export default function Pagination({ page, totalPages, onChange }) {
  if (!totalPages || totalPages <= 1) return null;

  // Show at most 5 page numbers around the current page
  const start = Math.max(0, Math.min(page - 2, totalPages - 5));
  const pages = [];
  for (let i = start; i < Math.min(totalPages, start + 5); i += 1) pages.push(i);

  return (
    <nav className="pagination" aria-label="Pagination">
      <button type="button" className="btn btn-sm btn-outline" disabled={page === 0} onClick={() => onChange(page - 1)}>
        ‹ Prev
      </button>
      {pages.map((p) => (
        <button
          key={p}
          type="button"
          className={`btn btn-sm ${p === page ? 'btn-primary' : 'btn-outline'}`}
          onClick={() => onChange(p)}
          aria-current={p === page ? 'page' : undefined}
        >
          {p + 1}
        </button>
      ))}
      <button
        type="button"
        className="btn btn-sm btn-outline"
        disabled={page >= totalPages - 1}
        onClick={() => onChange(page + 1)}
      >
        Next ›
      </button>
    </nav>
  );
}
