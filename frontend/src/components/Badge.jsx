// Coloured label for statuses like PENDING, PAID, OVERDUE...
const COLORS = {
  green: ['RETURNED', 'PAID', 'SUCCESS', 'FULFILLED', 'AVAILABLE', 'ACTIVE', 'YES'],
  red: ['OVERDUE', 'LOST', 'DAMAGED', 'FAILED', 'EXPIRED', 'CANCELLED', 'INACTIVE', 'NO'],
  yellow: ['PENDING', 'PARTIALLY_PAID', 'PROCESSING', 'CHECKED_OUT'],
};

export default function Badge({ value }) {
  if (value === null || value === undefined) return '-';
  const text = String(value).toUpperCase();
  const color = Object.keys(COLORS).find((c) => COLORS[c].includes(text)) || 'gray';
  return <span className={`badge badge-${color}`}>{text.replace(/_/g, ' ')}</span>;
}
