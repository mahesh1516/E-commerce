const inr = new Intl.NumberFormat('en-IN', {
  style: 'currency',
  currency: 'INR',
  minimumFractionDigits: 0,
  maximumFractionDigits: 2,
});

/** 2030 -> "₹2,030" */
export function formatPrice(value) {
  return inr.format(Number(value || 0));
}

export function formatDate(iso) {
  if (!iso) return '';
  return new Date(iso).toLocaleDateString('en-IN', { day: 'numeric', month: 'short', year: 'numeric' });
}

export function formatDateTime(iso) {
  if (!iso) return '';
  return new Date(iso).toLocaleString('en-IN', {
    day: 'numeric', month: 'short', year: 'numeric', hour: '2-digit', minute: '2-digit',
  });
}

/** "Display: 6.1-inch\nChip: A16" -> [["Display","6.1-inch"],["Chip","A16"]] */
export function parseSpecifications(text) {
  if (!text) return [];
  return text
    .split('\n')
    .map((line) => line.trim())
    .filter(Boolean)
    .map((line) => {
      const i = line.indexOf(':');
      return i === -1 ? [line, ''] : [line.slice(0, i).trim(), line.slice(i + 1).trim()];
    });
}

export function formatStatus(status) {
  if (!status) return '';
  return status.charAt(0) + status.slice(1).toLowerCase();
}
