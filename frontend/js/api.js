// API Client covering all backend endpoints

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
        const isAuthEndpoint = endpoint.startsWith('/auth') || endpoint.startsWith('/api/auth');
        if (token && !isAuthEndpoint && !headers['Authorization']) {
            headers['Authorization'] = `Bearer ${token}`;
        }

        try {
            const response = await fetch(url, {
                ...options,
                headers
            });

            // Handle unauthorized / token expired
            if (response.status === 401 || response.status === 403) {
                // If it's a secured API call and returned 401/403, clear stale auth and redirect
                if (!isAuthEndpoint) {
                    api.clearAuth();
                    if (window.location.hash !== '#login' && window.location.hash !== '#signup') {
                        window.location.hash = 'login';
                    }
                }
            }

            // Extract content-type
            const contentType = response.headers.get('content-type') || '';
            let data = null;

            if (contentType.includes('application/json')) {
                data = await response.json();
            } else {
                const text = await response.text();
                data = text ? { message: text } : {};
            }

            if (!response.ok) {
                const errorMessage = (data && (data.message || data.error)) || `HTTP error ${response.status}`;
                throw new Error(errorMessage);
            }

            return data;
        } catch (error) {
            console.error(`API Error [${endpoint}]:`, error);
            throw error;
        }
    },

    // 1. AuthController (5 endpoints)
    auth: {
        login(credentials) {
            return api.request('/auth/login', {
                method: 'POST',
                body: JSON.stringify(credentials)
            });
        },
        signup(userData) {
            return api.request('/auth/signup', {
                method: 'POST',
                body: JSON.stringify(userData)
            });
        },
        forgotPassword(email) {
            return api.request(`/auth/forgot-password?email=${encodeURIComponent(email)}`, {
                method: 'POST'
            });
        },
        verifyResetOtp(email, otp) {
            return api.request(`/auth/verify-reset-otp?email=${encodeURIComponent(email)}&otp=${encodeURIComponent(otp)}`, {
                method: 'POST'
            });
        },
        resetPassword(resetData) {
            return api.request('/auth/reset-password', {
                method: 'POST',
                body: JSON.stringify(resetData)
            });
        }
    },

    // 2. UserController (3 endpoints)
    users: {
        getProfile() {
            return api.request('/api/users/profile');
        },
        getAllAdmin() {
            return api.request('/api/users/admin');
        },
        toggleBlockAdmin(userId) {
            return api.request(`/api/users/admin/${userId}/block`, {
                method: 'PUT'
            });
        }
    },

    // 3. BookController & AdminBookController
    books: {
        getAll(params = {}) {
            return this.search(params);
        },
        getById(id) {
            return api.request(`/api/book/${id}`);
        },
        getByIsbn(isbn) {
            return api.request(`/api/book/isbn/${isbn}`);
        },
        getStats() {
            return api.request('/api/book/stats');
        },
        search(arg1 = {}, arg2 = {}) {
            let params = {};
            if (typeof arg1 === 'string') {
                params = { ...arg2, searchTerm: arg1 };
            } else if (typeof arg1 === 'object') {
                params = { ...arg1 };
            }

            const query = new URLSearchParams();
            if (params.genreId !== undefined && params.genreId !== null && params.genreId !== '') query.append('genreId', params.genreId);
            if (params.availableOnly !== undefined) query.append('availableOnly', params.availableOnly);
            if (params.activeOnly !== undefined) query.append('activeOnly', params.activeOnly);
            if (params.page !== undefined) query.append('page', params.page);
            if (params.size !== undefined) query.append('size', params.size);
            if (params.sortBy) query.append('sortBy', params.sortBy);
            if (params.sortDirection) query.append('sortDirection', params.sortDirection);
            else if (params.sortDir) query.append('sortDirection', params.sortDir);

            return api.request(`/api/book/search?${query.toString()}`);
        },
        advancedSearch(searchRequest = {}) {
            return api.request('/api/book/search', {
                method: 'POST',
                body: JSON.stringify(searchRequest)
            });
        },
        filter(filterParams, params = {}) {
            return this.search({ ...filterParams, ...params });
        },
        createAdmin(bookData) {
            return api.request('/api/admin/book', {
                method: 'POST',
                body: JSON.stringify(bookData)
            });
        },
        createBulkAdmin(booksList) {
            return api.request('/api/admin/book/bulk', {
                method: 'POST',
                body: JSON.stringify(booksList)
            });
        },
        updateAdmin(id, bookData) {
            return api.request(`/api/admin/book/${id}`, {
                method: 'PUT',
                body: JSON.stringify(bookData)
            });
        },
        delete(id) {
            return api.request(`/api/book/${id}`, {
                method: 'DELETE'
            });
        },
        deleteSoft(id) {
            return api.request(`/api/book/${id}`, {
                method: 'DELETE'
            });
        },
        deletePermanent(id) {
            return api.request(`/api/admin/book/${id}/permanent`, {
                method: 'DELETE'
            });
        },
        deleteAdmin(id) {
            return api.request(`/api/book/${id}`, {
                method: 'DELETE'
            });
        }
    },

    // 4. BookLoanController (7 endpoints)
    loans: {
        checkout(bookId) {
            return api.request(`/api/book-loans`, {
                method: 'POST',
                body: JSON.stringify({ bookId })
            });
        },
        returnBook(loanId) {
            return api.request(`/api/book-loans/return?loanId=${loanId}`, {
                method: 'POST'
            });
        },
        renew(loanId) {
            return api.request(`/api/book-loans/renew?loanId=${loanId}`, {
                method: 'POST'
            });
        },
        getMyLoans(params = {}) {
            const query = new URLSearchParams();
            if (params.page !== undefined) query.append('page', params.page);
            if (params.size !== undefined) query.append('size', params.size);
            if (params.status) query.append('status', params.status);
            return api.request(`/api/book-loans/mybookloans?${query.toString()}`);
        },
        getAllAdmin(params = {}) {
            const query = new URLSearchParams();
            if (params.page !== undefined) query.append('page', params.page);
            if (params.size !== undefined) query.append('size', params.size);
            if (params.status) query.append('status', params.status);
            return api.request(`/api/book-loans/all?${query.toString()}`);
        },
        getOverdueAdmin() {
            return api.request('/api/book-loans/overdue');
        },
        markLostAdmin(loanId, reason = '') {
            const query = reason ? `?reason=${encodeURIComponent(reason)}` : '';
            return api.request(`/api/book-loans/lost/${loanId}${query}`, {
                method: 'PUT'
            });
        }
    },

    // 5. GenreController (8 endpoints)
    genres: {
        getAll() {
            return api.request('/api/genres');
        },
        getById(id) {
            return api.request(`/api/genres/${id}`);
        },
        create(genreData) {
            return api.request('/api/genres', {
                method: 'POST',
                body: JSON.stringify(genreData)
            });
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

    // 9. PaymentController (3 endpoints)
    payments: {
        verify(verifyRequest) {
            return api.request('/api/payments/verify', {
                method: 'POST',
                body: JSON.stringify(verifyRequest)
            });
        },
        cancel(paymentId) {
            return api.request(`/api/payments/${paymentId}/cancel`, {
                method: 'POST'
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
