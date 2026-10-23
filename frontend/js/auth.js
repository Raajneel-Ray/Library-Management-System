// Authentication Module (Login, Signup, Forgot & Reset Password)

const auth = {
    renderLogin() {
        const container = document.getElementById('auth-content');
        if (!container) return;

        container.innerHTML = `
            <div class="auth-card">
                <div class="auth-header">
                    <span class="auth-logo">📖</span>
                    <h1 class="auth-title">Welcome Back</h1>
                    <p class="auth-subtitle">Sign in to your LibraryMS account</p>
                </div>
                <form id="login-form">
                    <div class="form-group">
                        <label class="form-label" for="login-email">Email Address</label>
                        <input type="email" id="login-email" class="form-control" placeholder="name@example.com" required>
                    </div>
                    <div class="form-group">
                        <div class="flex justify-between items-center mb-2">
                            <label class="form-label mb-0" for="login-password" style="margin-bottom: 0;">Password</label>
                            <a href="#forgot-password" class="text-sm" style="color: var(--primary); text-decoration: none;">Forgot password?</a>
                        </div>
                        <input type="password" id="login-password" class="form-control" placeholder="••••••••" required>
                    </div>
                    <button type="submit" id="login-submit-btn" class="btn btn-primary w-full mt-2">Sign In</button>
                </form>
                <div class="text-center mt-4 text-sm" style="color: var(--text-secondary);">
                    Don't have an account? 
                    <a href="#signup" style="color: var(--primary); font-weight: 500; text-decoration: none;">Create one here</a>
                </div>
            </div>
        `;

        const form = document.getElementById('login-form');
        form.onsubmit = async (e) => {
            e.preventDefault();
            const email = document.getElementById('login-email').value.trim();
            const password = document.getElementById('login-password').value;
            const btn = document.getElementById('login-submit-btn');

            try {
                btn.disabled = true;
                btn.textContent = 'Signing in...';
                const response = await api.auth.login({ email, password });
                
                api.setToken(response.jwt);
                api.setUser(response.user);
                
                utils.toast(response.message || 'Login successful!', 'success');
                router.navigate('dashboard');
            } catch (err) {
                utils.toast(err.message || 'Invalid credentials', 'error');
            } finally {
                btn.disabled = false;
                btn.textContent = 'Sign In';
            }
        };
    },

    renderSignup() {
        const container = document.getElementById('auth-content');
        if (!container) return;

        container.innerHTML = `
            <div class="auth-card">
                <div class="auth-header">
                    <span class="auth-logo">📚</span>
                    <h1 class="auth-title">Create Account</h1>
                    <p class="auth-subtitle">Join LibraryMS to borrow books and access collections</p>
                </div>
                <form id="signup-form">
                    <div class="form-group">
                        <label class="form-label" for="signup-name">Full Name</label>
                        <input type="text" id="signup-name" class="form-control" placeholder="John Doe" required>
                    </div>
                    <div class="form-group">
                        <label class="form-label" for="signup-email">Email Address</label>
                        <input type="email" id="signup-email" class="form-control" placeholder="name@example.com" required>
                    </div>
                    <div class="form-group">
                        <label class="form-label" for="signup-password">Password</label>
                        <input type="password" id="signup-password" class="form-control" placeholder="At least 6 characters" minlength="6" required>
                    </div>
                    <div class="form-row">
                        <div class="form-group">
                            <label class="form-label" for="signup-phone">Phone Number</label>
                            <input type="tel" id="signup-phone" class="form-control" placeholder="+91 9876543210">
                        </div>
                        <div class="form-group">
                            <label class="form-label" for="signup-role">Account Role</label>
                            <select id="signup-role" class="form-control">
                                <option value="ROLE_USER">Member (User)</option>
                                <option value="ROLE_ADMIN">Administrator</option>
                            </select>
                        </div>
                    </div>
                    <button type="submit" id="signup-submit-btn" class="btn btn-primary w-full mt-2">Sign Up</button>
                </form>
                <div class="text-center mt-4 text-sm" style="color: var(--text-secondary);">
                    Already have an account? 
                    <a href="#login" style="color: var(--primary); font-weight: 500; text-decoration: none;">Sign in</a>
                </div>
            </div>
        `;

        const form = document.getElementById('signup-form');
        form.onsubmit = async (e) => {
            e.preventDefault();
            const fullName = document.getElementById('signup-name').value.trim();
            const email = document.getElementById('signup-email').value.trim();
            const password = document.getElementById('signup-password').value;
            const phone = document.getElementById('signup-phone').value.trim();
            const role = document.getElementById('signup-role').value;
            const btn = document.getElementById('signup-submit-btn');

            try {
                btn.disabled = true;
                btn.textContent = 'Creating account...';
                const response = await api.auth.signup({
                    fullName,
                    email,
                    password,
                    phone,
                    role
                });

                api.setToken(response.jwt);
                api.setUser(response.user);

                utils.toast(response.message || 'Account registered successfully!', 'success');
                router.navigate('dashboard');
            } catch (err) {
                utils.toast(err.message || 'Registration failed', 'error');
            } finally {
                btn.disabled = false;
                btn.textContent = 'Sign Up';
            }
        };
    },

    renderForgotPassword() {
        const container = document.getElementById('auth-content');
        if (!container) return;

        container.innerHTML = `
            <div class="auth-card">
                <div class="auth-header">
                    <span class="auth-logo">🔑</span>
                    <h1 class="auth-title">Reset Password</h1>
                    <p class="auth-subtitle">Enter your email and we'll send a reset token</p>
                </div>
                <form id="forgot-form">
                    <div class="form-group">
                        <label class="form-label" for="forgot-email">Registered Email</label>
                        <input type="email" id="forgot-email" class="form-control" placeholder="name@example.com" required>
                    </div>
                    <button type="submit" id="forgot-submit-btn" class="btn btn-primary w-full mt-2">Send Reset Token</button>
                </form>
                <div class="text-center mt-4 text-sm" style="display: flex; justify-content: space-between;">
                    <a href="#login" style="color: var(--secondary); text-decoration: none;">&larr; Back to Login</a>
                    <a href="#reset-password" style="color: var(--primary); text-decoration: none;">Already have a token?</a>
                </div>
            </div>
        `;

        const form = document.getElementById('forgot-form');
        form.onsubmit = async (e) => {
            e.preventDefault();
            const email = document.getElementById('forgot-email').value.trim();
            const btn = document.getElementById('forgot-submit-btn');

            try {
                btn.disabled = true;
                btn.textContent = 'Sending...';
                const res = await api.auth.forgotPassword(email);
                utils.toast(res.message || 'Reset token generated! Please check your email.', 'success');
                router.navigate('reset-password');
            } catch (err) {
                utils.toast(err.message || 'Failed to send reset link', 'error');
            } finally {
                btn.disabled = false;
                btn.textContent = 'Send Reset Token';
            }
        };
    },

    renderResetPassword(params = {}) {
        const container = document.getElementById('auth-content');
        if (!container) return;

        container.innerHTML = `
            <div class="auth-card">
                <div class="auth-header">
                    <span class="auth-logo">🔒</span>
                    <h1 class="auth-title">Set New Password</h1>
                    <p class="auth-subtitle">Provide your reset token and new password</p>
                </div>
                <form id="reset-form">
                    <div class="form-group">
                        <label class="form-label" for="reset-token">Reset Token</label>
                        <input type="text" id="reset-token" class="form-control" placeholder="Enter token from email" value="${params.token || ''}" required>
                    </div>
                    <div class="form-group">
                        <label class="form-label" for="reset-password">New Password</label>
                        <input type="password" id="reset-password" class="form-control" placeholder="Minimum 6 characters" minlength="6" required>
                    </div>
                    <button type="submit" id="reset-submit-btn" class="btn btn-primary w-full mt-2">Update Password</button>
                </form>
                <div class="text-center mt-4 text-sm">
                    <a href="#login" style="color: var(--secondary); text-decoration: none;">&larr; Back to Login</a>
                </div>
            </div>
        `;

        const form = document.getElementById('reset-form');
        form.onsubmit = async (e) => {
            e.preventDefault();
            const token = document.getElementById('reset-token').value.trim();
            const newPassword = document.getElementById('reset-password').value;
            const btn = document.getElementById('reset-submit-btn');

            try {
                btn.disabled = true;
                btn.textContent = 'Updating...';
                const res = await api.auth.resetPassword(token, newPassword);
                utils.toast(res.message || 'Password reset successful! Please log in.', 'success');
                router.navigate('login');
            } catch (err) {
                utils.toast(err.message || 'Reset failed. Token may be invalid or expired.', 'error');
            } finally {
                btn.disabled = false;
                btn.textContent = 'Update Password';
            }
        };
    }
};
