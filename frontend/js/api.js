// API Client covering all 45 backend endpoints

const API_BASE_URL = 'http://localhost:8080';

const api = {
    // Auth Token & User Storage Helpers
    getToken() {
        return localStorage.getItem('jwt');
    },
    setToken(token) {
        if (token) localStorage.setItem('jwt', token);
        else localStorage.removeItem('jwt');
    },
    getUser() {
        const u = localStorage.getItem('user');
        try {
            return u ? JSON.parse(u) : null;
        } catch (e) {
            return null;
        }
    },
    setUser(user) {
        if (user) localStorage.setItem('user', JSON.stringify(user));
        else localStorage.removeItem('user');
    },
    clearAuth() {
        localStorage.removeItem('jwt');
        localStorage.removeItem('user');
    },
    isAuthenticated() {
        return !!this.getToken();
    },
    isAdmin() {
        const user = this.getUser();
        return user && user.role === 'ROLE_ADMIN';
    },

    // Core Fetch Wrapper
    async request(endpoint, options = {}) {
        const url = `${API_BASE_URL}${endpoint}`;
        const headers = {
            'Content-Type': 'application/json',
            ...(options.headers || {})
        };

        const token = this.getToken();
        if (token && !headers['Authorization']) {
            headers['Authorization'] = `Bearer ${token}`;
        }

        const config = {
            ...options,
            headers
        };

        try {
            const response = await fetch(url, config);

            // Handle 401 Unauthorized
            if (response.status === 401 && !endpoint.startsWith('/auth/')) {
                this.clearAuth();
                window.location.hash = '#login';
                throw new Error('Session expired. Please log in again.');
            }

            // Parse response body
            const contentType = response.headers.get('content-type');
            let data = null;
            if (contentType && contentType.includes('application/json')) {
                data = await response.json();
            } else {
                const text = await response.text();
                data = text ? { message: text } : {};
            }

            if (!response.ok) {
                const errorMessage = data?.message || data?.title || `Request failed with status ${response.status}`;
                const error = new Error(errorMessage);
                error.status = response.status;
                error.data = data;
                throw error;
            }

            return data;
        } catch (err) {
            console.error(`API Error [${options.method || 'GET'} ${endpoint}]:`, err);
            throw err;
        }
    },

    // 1. AuthController (4 endpoints)
    auth: {
        signup(userData) {
            return api.request('/auth/signup', {
                method: 'POST',
                body: JSON.stringify(userData)
            });
        },
        login(credentials) {
            return api.request('/auth/login', {
                method: 'POST',
                body: JSON.stringify(credentials)
            });
        },
        forgotPassword(email) {
            return api.request('/auth/forgot-password', {
                method: 'POST',
                body: JSON.stringify({ email })
            });
        },
        resetPassword(token, newPassword) {
            return api.request('/auth/reset-password', {
                method: 'POST',
                body: JSON.stringify({ token, newPassword })
            });
        }
    },

    // 2. BookController (6 endpoints)
    books: {
        getById(id) {
            return api.request(`/api/book/${id}`);
        },
        getByIsbn(isbn) {
            return api.request(`/api/book/isbn/${encodeURIComponent(isbn)}`);
        },
        deleteSoft(id) {
            return api.request(`/api/book/${id}`, { method: 'DELETE' });
        },
        search(params = {}) {
            const query = new URLSearchParams();
            if (params.genreId) query.append('genreId', params.genreId);
            if (params.availableOnly !== undefined) query.append('availableOnly', params.availableOnly);
            if (params.activeOnly !== undefined) query.append('activeOnly', params.activeOnly);
            if (params.page !== undefined) query.append('page', params.page);
            if (params.size !== undefined) query.append('size', params.size);
            if (params.sortBy) query.append('sortBy', params.sortBy);
            if (params.sortDirection) query.append('sortDirection', params.sortDirection);
            return api.request(`/api/book/search?${query.toString()}`);
        },
        advancedSearch(searchRequest = {}) {
            return api.request('/api/book/search', {
                method: 'POST',
                body: JSON.stringify(searchRequest)
            });
        },
        getStats() {
            return api.request('/api/book/stats');
        }
    },

    // 3. AdminBookController (4 endpoints - ADMIN ONLY)
    adminBooks: {
        create(bookData) {
            return api.request('/api/admin/book', {
                method: 'POST',
                body: JSON.stringify(bookData)
            });
        },
        createBulk(booksList) {
            return api.request('/api/admin/book/bulk', {
                method: 'POST',
                body: JSON.stringify(booksList)
            });
        },
        update(id, bookData) {
            return api.request(`/api/admin/book/${id}`, {
                method: 'PUT',
                body: JSON.stringify(bookData)
            });
        },
        deletePermanent(id) {
            return api.request(`/api/admin/book/${id}/permanent`, {
                method: 'DELETE'
            });
        }
    },

    // 4. BookLoanController (7 endpoints)
    loans: {
        checkout(checkoutRequest) {
            return api.request('/api/book-loans/checkout', {
                method: 'POST',
                body: JSON.stringify(checkoutRequest)
            });
        },
        checkoutForUser(userId, checkoutRequest) {
            return api.request(`/api/book-loans/checkout/user/${userId}`, {
                method: 'POST',
                body: JSON.stringify(checkoutRequest)
            });
        },
        checkin(checkinRequest) {
            return api.request('/api/book-loans/checkin', {
                method: 'POST',
                body: JSON.stringify(checkinRequest)
            });
        },
        renew(renewRequest) {
            return api.request('/api/book-loans/renew', {
                method: 'POST',
                body: JSON.stringify(renewRequest)
            });
        },
        getMyLoans(params = {}) {
            const query = new URLSearchParams();
            if (params.status) query.append('status', params.status);
            if (params.page !== undefined) query.append('page', params.page);
            if (params.size !== undefined) query.append('size', params.size);
            return api.request(`/api/book-loans/mybookloans?${query.toString()}`);
        },
        search(searchRequest = {}) {
            return api.request('/api/book-loans/search', {
                method: 'POST',
                body: JSON.stringify(searchRequest)
            });
        },
        updateOverdueAdmin() {
            return api.request('/api/book-loans/admin/update-overdue', {
                method: 'POST'
            });
        }
    },

    // 5. GenreController (9 endpoints)
    genres: {
        create(genreData) {
            return api.request('/api/genres/create', {
                method: 'POST',
                body: JSON.stringify(genreData)
            });
        },
        getAll() {
            return api.request('/api/genres');
        },
        getById(id) {
            return api.request(`/api/genres/${id}`);
        },
        update(id, genreData) {
            return api.request(`/api/genres/${id}`, {
                method: 'PUT',
                body: JSON.stringify(genreData)
            });
        },
        delete(id) {
            return api.request(`/api/genres/${id}`, { method: 'DELETE' });
        },
        deleteHard(id) {
            return api.request(`/api/genres/${id}/hard`, { method: 'DELETE' });
        },
        getTopLevel() {
            return api.request('/api/genres/top-level');
        },
        getCount() {
            return api.request('/api/genres/count');
        },
        getBookCount(id) {
            return api.request(`/api/genres/${id}/books/count`);
        }
    },

    // 6. SubscriptionController (6 endpoints)
    subscriptions: {
        subscribe(subscriptionData) {
            return api.request('/api/subscriptions/subscribe', {
                method: 'POST',
                body: JSON.stringify(subscriptionData)
            });
        },
        getAllAdmin() {
            return api.request('/api/subscriptions/admin');
        },
        getActive(userId = null) {
            const endpoint = userId 
                ? `/api/subscriptions/user/active-subscriptions?userId=${userId}` 
                : '/api/subscriptions/user/active-subscriptions';
            return api.request(endpoint);
        },
        deactivateExpiredAdmin() {
            return api.request('/api/subscriptions/admin/deactivate-expired');
        },
        cancel(subscriptionId, reason = '') {
            const query = reason ? `?reason=${encodeURIComponent(reason)}` : '';
            return api.request(`/api/subscriptions/cancel/${subscriptionId}${query}`, {
                method: 'POST'
            });
        },
        activate(subscriptionId, paymentId) {
            return api.request(`/api/subscriptions/activate?subscriptionId=${subscriptionId}&paymentId=${paymentId}`, {
                method: 'POST'
            });
        }
    },

    // 7. SubscriptionPlanController (5 endpoints)
    plans: {
        getAll() {
            return api.request('/api/subscription-plans');
        },
        getFeatured() {
            return api.request('/api/subscription-plans/featured');
        },
        createAdmin(planData) {
            return api.request('/api/subscription-plans/admin/create', {
                method: 'POST',
                body: JSON.stringify(planData)
            });
        },
        updateAdmin(planId, planData) {
            return api.request(`/api/subscription-plans/admin/update/${planId}`, {
                method: 'PUT',
                body: JSON.stringify(planData)
            });
        },
        deleteAdmin(planId) {
            return api.request(`/api/subscription-plans/admin/delete/${planId}`, {
                method: 'DELETE'
            });
        }
    },

    // 8. UserController (2 endpoints)
    users: {
        getAll() {
            return api.request('/api/users/list');
        },
        getProfile() {
            return api.request('/api/users/profile');
        }
    },

    // 9. PaymentController (2 endpoints)
    payments: {
        verify(verifyRequest) {
            return api.request('/api/payments/verify', {
                method: 'POST',
                body: JSON.stringify(verifyRequest)
            });
        },
        getAll(params = {}) {
            const query = new URLSearchParams();
            if (params.page !== undefined) query.append('page', params.page);
            if (params.size !== undefined) query.append('size', params.size);
            if (params.sortBy) query.append('sortBy', params.sortBy);
            if (params.sortDir) query.append('sortDir', params.sortDir);
            return api.request(`/api/payments?${query.toString()}`);
        }
    }
};
