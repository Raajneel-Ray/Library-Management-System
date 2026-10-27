// Application Initialization, Navigation and Layout Control

const app = {
    init() {
        this.setupNavigation();
        this.setupEventListeners();
        this.registerRoutes();
        router.init();
        this.hideInitialLoading();
    },

    hideInitialLoading() {
        const loading = document.getElementById('loading-screen');
        if (loading) loading.classList.add('hidden');
    },

    showLoading(show = true) {
        const loading = document.getElementById('loading-screen');
        if (loading) {
            if (show) loading.classList.remove('hidden');
            else loading.classList.add('hidden');
        }
    },

    setupEventListeners() {
        // Logout button
        const logoutBtn = document.getElementById('logout-btn');
        if (logoutBtn) {
            logoutBtn.onclick = () => {
                api.clearAuth();
                utils.toast('You have been logged out', 'info');
                router.navigate('login');
            };
        }

        // Mobile sidebar toggle
        const toggleBtn = document.getElementById('sidebar-toggle');
        const sidebar = document.getElementById('sidebar');
        if (toggleBtn && sidebar) {
            toggleBtn.onclick = () => {
                sidebar.classList.toggle('open');
            };
        }
    },

    updateUserInfo() {
        const userInfo = document.getElementById('user-info');
        const user = api.getUser();
        if (userInfo && user) {
            const roleBadge = user.role === 'ROLE_ADMIN' 
                ? '<span class="user-badge" style="background: #fef2f2; color: #dc2626;">ADMIN</span>' 
                : '<span class="user-badge">MEMBER</span>';
            userInfo.innerHTML = `<span>👤 ${utils.escapeHtml(user.fullName || user.email)}</span> ${roleBadge}`;
        }
    },

    updateSidebar(activePath) {
        const nav = document.getElementById('sidebar-nav');
        if (!nav) return;

        const isAdmin = api.isAdmin();

        let navHtml = `
            <div class="nav-section-title">Navigation</div>
            <a href="#dashboard" class="nav-link ${activePath === 'dashboard' ? 'active' : ''}">
                <span class="nav-icon">📊</span> Dashboard
            </a>
            <a href="#books" class="nav-link ${activePath === 'books' || activePath === 'book-detail' ? 'active' : ''}">
                <span class="nav-icon">📖</span> Browse Books
            </a>
            <a href="#loans" class="nav-link ${activePath === 'loans' ? 'active' : ''}">
                <span class="nav-icon">📑</span> My Loans
            </a>
            <a href="#fines" class="nav-link ${activePath === 'fines' ? 'active' : ''}">
                <span class="nav-icon">💸</span> My Fines
            </a>
            <a href="#subscriptions" class="nav-link ${activePath === 'subscriptions' ? 'active' : ''}">
                <span class="nav-icon">💳</span> Subscriptions
            </a>
        `;

        if (isAdmin) {
            navHtml += `
                <div class="nav-section-title">Admin Management</div>
                <a href="#admin-books" class="nav-link ${activePath === 'admin-books' ? 'active' : ''}">
                    <span class="nav-icon">📚</span> Manage Books
                </a>
                <a href="#admin-loans" class="nav-link ${activePath === 'admin-loans' ? 'active' : ''}">
                    <span class="nav-icon">📋</span> Loan Records
                </a>
                <a href="#admin-fines" class="nav-link ${activePath === 'admin-fines' ? 'active' : ''}">
                    <span class="nav-icon">⚖️</span> Fine Records
                </a>
                <a href="#admin-genres" class="nav-link ${activePath === 'admin-genres' ? 'active' : ''}">
                    <span class="nav-icon">🏷️</span> Genres
                </a>
                <a href="#admin-plans" class="nav-link ${activePath === 'admin-plans' ? 'active' : ''}">
                    <span class="nav-icon">📦</span> Subscription Plans
                </a>
                <a href="#admin-subscriptions" class="nav-link ${activePath === 'admin-subscriptions' ? 'active' : ''}">
                    <span class="nav-icon">🎖️</span> Member Subscriptions
                </a>
                <a href="#admin-users" class="nav-link ${activePath === 'admin-users' ? 'active' : ''}">
                    <span class="nav-icon">👥</span> Users
                </a>
                <a href="#admin-payments" class="nav-link ${activePath === 'admin-payments' ? 'active' : ''}">
                    <span class="nav-icon">💰</span> Payment Logs
                </a>
            `;
        }

        nav.innerHTML = navHtml;

        // Auto close mobile sidebar on nav click
        const sidebar = document.getElementById('sidebar');
        if (sidebar && sidebar.classList.contains('open')) {
            sidebar.classList.remove('open');
        }
    },

    setupNavigation() {
        // Will run route registration
    },

    registerRoutes() {
        // Auth Routes
        router.register('login', () => auth.renderLogin(), { authRequired: false });
        router.register('signup', () => auth.renderSignup(), { authRequired: false });
        router.register('forgot-password', () => auth.renderForgotPassword(), { authRequired: false });
        router.register('reset-password', (p) => auth.renderResetPassword(p), { authRequired: false });

        // Member Routes
        router.register('dashboard', () => dashboard.render());
        router.register('books', (p) => books.renderList(p));
        router.register('book-detail', (p) => books.renderDetail(p));
        router.register('loans', (p) => loans.renderMyLoans(p));
        router.register('fines', (p) => fines.renderMyFines(p));
        router.register('subscriptions', () => subscriptions.render());

        // Admin Routes
        router.register('admin-books', () => admin.renderBooks(), { adminOnly: true });
        router.register('admin-loans', (p) => admin.renderLoans(p), { adminOnly: true });
        router.register('admin-fines', (p) => admin.renderFines(p), { adminOnly: true });
        router.register('admin-genres', () => admin.renderGenres(), { adminOnly: true });
        router.register('admin-plans', () => admin.renderPlans(), { adminOnly: true });
        router.register('admin-subscriptions', () => admin.renderSubscriptions(), { adminOnly: true });
        router.register('admin-users', () => admin.renderUsers(), { adminOnly: true });
        router.register('admin-payments', (p) => admin.renderPayments(p), { adminOnly: true });
    }
};

window.addEventListener('DOMContentLoaded', () => {
    app.init();
});
