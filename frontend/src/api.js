// All calls to the Spring Boot backend live in this file.

const BASE_URL = 'http://localhost:8080';

export function getToken() {
  return localStorage.getItem('jwt');
}

// Turns { a: 1, b: '', c: null } into "?a=1" (empty values are skipped).
function toQuery(params = {}) {
  const query = new URLSearchParams();
  Object.entries(params).forEach(([key, value]) => {
    if (value !== undefined && value !== null && value !== '') {
      query.append(key, value);
    }
  });
  const text = query.toString();
  return text ? `?${text}` : '';
}

async function request(method, path, body) {
  const headers = { 'Content-Type': 'application/json' };
  const token = getToken();
  if (token) headers.Authorization = `Bearer ${token}`;

  const response = await fetch(BASE_URL + path, {
    method,
    headers,
    body: body !== undefined ? JSON.stringify(body) : undefined,
  });

  const text = await response.text();
  let data = null;
  try {
    data = text ? JSON.parse(text) : null;
  } catch {
    data = text;
  }

  if (!response.ok) {
    if (response.status === 401) {
      localStorage.removeItem('jwt');
      localStorage.removeItem('user');
    }
    const message =
      (data && data.message) ||
      (data && data.error) ||
      (typeof data === 'string' && data) ||
      `Request failed (${response.status})`;
    throw new Error(message);
  }
  return data;
}

const get = (path, params) => request('GET', path + toQuery(params));
const post = (path, body, params) => request('POST', path + toQuery(params), body);
const put = (path, body) => request('PUT', path, body);
const del = (path) => request('DELETE', path);

// ---------- Auth ----------
export const authApi = {
  login: (email, password) => post('/auth/login', { email, password }),
  signup: (user) => post('/auth/signup', user),
  forgotPassword: (email) => post('/auth/forgot-password', { email }),
  resetPassword: (token, newPassword) => post('/auth/reset-password', { token, newPassword }),
};

// ---------- Users ----------
export const userApi = {
  profile: () => get('/api/users/profile'),
  list: () => get('/api/users/list'),
};

// ---------- Books ----------
export const bookApi = {
  // availableOnly / sortBy / sortDirection must always be sent: the backend fails when they are missing.
  search: (filters) =>
    post('/api/books/search', { availableOnly: false, sortBy: 'createdAt', sortDirection: 'DESC', ...filters }),
  getById: (id) => get(`/api/books/${id}`),
  stats: () => get('/api/books/stats'),
  create: (book) => post('/api/admin/books', book),
  update: (id, book) => put(`/api/admin/books/${id}`, book),
  softDelete: (id) => del(`/api/books/${id}`),
  hardDelete: (id) => del(`/api/admin/books/${id}/permanent`),
};

// ---------- Genres ----------
export const genreApi = {
  list: () => get('/api/genres'),
  create: (genre) => post('/api/genres/create', genre),
  update: (id, genre) => put(`/api/genres/${id}`, genre),
  softDelete: (id) => del(`/api/genres/${id}`),
  hardDelete: (id) => del(`/api/genres/${id}/hard`),
  count: () => get('/api/genres/count'),
};

// ---------- Book loans ----------
export const loanApi = {
  checkout: (bookId, checkoutDays, notes) => post('/api/book-loans/checkout', { bookId, checkoutDays, notes }),
  checkoutForUser: (userId, bookId, checkoutDays, notes) =>
    post(`/api/book-loans/checkout/user/${userId}`, { bookId, checkoutDays, notes }),
  checkin: (bookLoanId, condition, notes) => post('/api/book-loans/checkin', { bookLoanId, condition, notes }),
  renew: (bookLoanId, extensionDay) => post('/api/book-loans/renew', { bookLoanId, extensionDay }),
  my: (status, page = 0) => get('/api/book-loans/mybookloans', { status, page, size: 10 }),
  search: (filters) => post('/api/book-loans/search', { sortBy: 'createdAt', sortDirection: 'DESC', ...filters }),
  updateOverdue: () => post('/api/book-loans/admin/update-overdue'),
};

// ---------- Reservations ----------
export const reservationApi = {
  create: (bookId, notes) => post('/api/reservations', { bookId, notes }),
  createForUser: (userId, bookId, notes) => post(`/api/reservations/user/${userId}`, { bookId, notes }),
  cancel: (id) => del(`/api/reservations/${id}`),
  fulfill: (id) => post(`/api/reservations/${id}/fulfill`),
  my: (params) => get('/api/reservations/my', params),
  search: (params) => get('/api/reservations', params),
};

// ---------- Wishlist ----------
export const wishlistApi = {
  add: (bookId, notes) => post('/api/wishlist', { bookId, notes }),
  remove: (id) => del(`/api/wishlist/${id}`),
  removeBook: (bookId) => del(`/api/wishlist/book/${bookId}`),
  my: (page = 0) => get('/api/wishlist/my', { page, size: 12 }),
  count: () => get('/api/wishlist/count'),
};

// ---------- Reviews ----------
export const reviewApi = {
  create: (bookId, rating, reviewText) => post('/api/reviews', { bookId, rating, reviewText }),
  update: (id, bookId, rating, reviewText) => put(`/api/reviews/${id}`, { bookId, rating, reviewText }),
  remove: (id) => del(`/api/reviews/${id}`),
  forBook: (bookId, page = 0) => get(`/api/reviews/book/${bookId}`, { page, size: 10 }),
  summary: (bookId) => get(`/api/reviews/book/${bookId}/summary`),
  my: (page = 0) => get('/api/reviews/my', { page, size: 10 }),
};

// ---------- Fines ----------
export const fineApi = {
  my: (status) => get('/api/fines/my', { status }),
  all: (params) => get('/api/fines', params),
  create: (fine) => post('/api/fines', fine),
  // Without a transactionId the backend returns a Razorpay checkout link.
  pay: (id, transactionId) => post(`/api/fines/${id}/pay`, undefined, { transactionId }),
  waive: (fineId, reason) => post('/api/fines/waive', { fineId, reason }),
};

// ---------- Subscription plans ----------
export const planApi = {
  all: () => get('/api/subscription-plans'),
  featured: () => get('/api/subscription-plans/featured'),
  create: (plan) => post('/api/subscription-plans/admin/create', plan),
  update: (id, plan) => put(`/api/subscription-plans/admin/update/${id}`, plan),
  remove: (id) => del(`/api/subscription-plans/admin/delete/${id}`),
};

// ---------- Subscriptions ----------
export const subscriptionApi = {
  subscribe: (planId) => post('/api/subscriptions/subscribe', { planId }),
  myActive: () => get('/api/subscriptions/user/active-subscriptions'),
  cancel: (id, reason) => post(`/api/subscriptions/cancel/${id}`, undefined, { reason }),
  all: () => get('/api/subscriptions/admin'),
  deactivateExpired: () => get('/api/subscriptions/admin/deactivate-expired'),
};

// ---------- Payments ----------
export const paymentApi = {
  verify: (razorpayPaymentId) => post('/api/payments/verify', { razorpayPaymentId }),
  cancel: (id) => post(`/api/payments/${id}/cancel`),
  all: (page = 0) => get('/api/payments', { page, size: 10 }),
};
