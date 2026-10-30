// Small formatting helpers used across pages.

export function formatDate(value) {
  if (!value) return '-';
  return new Date(value).toLocaleDateString();
}

export function formatDateTime(value) {
  if (!value) return '-';
  return new Date(value).toLocaleString();
}

// Amounts in this backend are whole rupees.
export function formatMoney(amount, currency = 'INR') {
  if (amount === null || amount === undefined) return '-';
  return `${currency === 'INR' ? '₹' : currency + ' '}${amount}`;
}

export const LOAN_STATUSES = ['CHECKED_OUT', 'RETURNED', 'OVERDUE', 'LOST', 'DAMAGED'];
export const RESERVATION_STATUSES = ['PENDING', 'AVAILABLE', 'FULFILLED', 'CANCELLED', 'EXPIRED'];
export const FINE_STATUSES = ['PENDING', 'PARTIALLY_PAID', 'PAID', 'WAIVED'];
export const FINE_TYPES = ['OVERDUE', 'DAMAGE', 'LOSS', 'PROCESSING'];
