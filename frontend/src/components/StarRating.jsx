/** Read-only stars, or clickable stars when onChange is given. */
export default function StarRating({ value = 0, count, onChange, size = 'sm' }) {
  const rounded = Math.round(value);
  return (
    <span className={`stars stars-${size}`} aria-label={`Rated ${value} out of 5`}>
      {[1, 2, 3, 4, 5].map((n) =>
        onChange ? (
          <button
            key={n}
            type="button"
            className={n <= value ? 'star filled' : 'star'}
            onClick={() => onChange(n)}
            aria-label={`${n} star${n > 1 ? 's' : ''}`}
          >
            ★
          </button>
        ) : (
          <span key={n} className={n <= rounded ? 'star filled' : 'star'}>★</span>
        )
      )}
      {count !== undefined && <span className="star-count">({count})</span>}
    </span>
  );
}
