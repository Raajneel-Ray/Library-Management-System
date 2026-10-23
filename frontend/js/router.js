// Hash-based SPA Router with Role-based Guards

const router = {
    routes: {},

    register(hash, handler, options = { authRequired: true, adminOnly: false }) {
        this.routes[hash] = { handler, ...options };
    },

    parseHash() {
        const fullHash = window.location.hash.slice(1) || 'dashboard';
        const [path, queryString] = fullHash.split('?');
        const params = {};
        if (queryString) {
            new URLSearchParams(queryString).forEach((val, key) => {
                params[key] = val;
            });
        }
        return { path, params };
    },

    navigate(path, params = {}) {
        let hash = path;
        const query = new URLSearchParams(params).toString();
        if (query) hash += `?${query}`;
        window.location.hash = hash;
    },

    async handleRouteChange() {
        const { path, params } = this.parseHash();
        const route = this.routes[path];

        // Public/Auth routes
        const authRoutes = ['login', 'signup', 'forgot-password', 'reset-password'];
        const isAuthRoute = authRoutes.includes(path);
        const isAuthenticated = api.isAuthenticated();
        const isAdmin = api.isAdmin();

        // Containers
        const authContainer = document.getElementById('auth-container');
        const mainContainer = document.getElementById('main-container');

        // Check authentication guards
        if (!isAuthenticated && !isAuthRoute) {
            this.navigate('login');
            return;
        }

        if (isAuthenticated && isAuthRoute) {
            this.navigate('dashboard');
            return;
        }

        // Toggle layout visibility
        if (isAuthRoute) {
            authContainer.classList.remove('hidden');
            mainContainer.classList.add('hidden');
        } else {
            authContainer.classList.add('hidden');
            mainContainer.classList.remove('hidden');
            app.updateSidebar(path);
            app.updateUserInfo();
        }

        if (!route) {
            // Fallback
            this.navigate(isAuthenticated ? 'dashboard' : 'login');
            return;
        }

        // Admin guard
        if (route.adminOnly && !isAdmin) {
            utils.toast('Access denied. Administrator privileges required.', 'error');
            this.navigate('dashboard');
            return;
        }

        // Execute route handler
        try {
            await route.handler(params);
        } catch (err) {
            console.error('Route error:', err);
            utils.toast(err.message || 'Error loading page', 'error');
        }
    },

    init() {
        window.addEventListener('hashchange', () => this.handleRouteChange());
        // Initial route trigger
        this.handleRouteChange();
    }
};
