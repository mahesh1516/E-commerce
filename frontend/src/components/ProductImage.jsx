import { useEffect, useState } from 'react';

const COLORS = [
  ['#6366f1', '#8b5cf6'], ['#0ea5e9', '#6366f1'], ['#f59e0b', '#ef4444'],
  ['#10b981', '#0ea5e9'], ['#ec4899', '#f97316'], ['#14b8a6', '#22c55e'],
];

function pickColors(text = '') {
  let hash = 0;
  for (let i = 0; i < text.length; i += 1) hash = (hash * 31 + text.charCodeAt(i)) >>> 0;
  return COLORS[hash % COLORS.length];
}

/**
 * Shows the product image, or a coloured placeholder when there is
 * no image URL or the image fails to load.
 */
export default function ProductImage({ src, name = '', className = '' }) {
  const [failed, setFailed] = useState(false);

  useEffect(() => setFailed(false), [src]);

  if (!src || failed) {
    const [from, to] = pickColors(name);
    const initials = name.split(' ').filter(Boolean).slice(0, 2).map((w) => w[0]).join('').toUpperCase();
    return (
      <div
        className={`img-placeholder ${className}`}
        style={{ background: `linear-gradient(135deg, ${from}, ${to})` }}
        role="img"
        aria-label={name}
      >
        <span>{initials || 'NX'}</span>
      </div>
    );
  }

  return <img src={src} alt={name} className={className} loading="lazy" onError={() => setFailed(true)} />;
}
