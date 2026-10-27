// Member & Admin Dashboard

const dashboard = {
    async render() {
        const container = document.getElementById('app-content');
        if (!container) return;

        container.innerHTML = `
            <div class="page-header">
                <div>
                    <h1 class="page-title">Dashboard</h1>
                    <p class="page-subtitle">Welcome to your library management portal</p>
                </div>
            </div>
            <div class="stats-grid" id="dashboard-stats">
                <div class="stat-card">
                    <div class="stat-icon">📚</div>
                    <div>
                        <div class="stat-value" id="stat-active-books">-</div>
                        <div class="stat-label">Total Active Books</div>
                    </div>
                </div>
                <div class="stat-card">
                    <div class="stat-icon" style="background: var(--success-light); color: var(--success);">📖</div>
                    <div>
                        <div class="stat-value" id="stat-available-books">-</div>
                        <div class="stat-label">Available Copies</div>
                    </div>
                </div>
                <div class="stat-card">
                    <div class="stat-icon" style="background: var(--info-light); color: var(--info);">🏷️</div>
                    <div>
                        <div class="stat-value" id="stat-genres">-</div>
                        <div class="stat-label">Active Genres</div>
                    </div>
                </div>
                <div class="stat-card">
                    <div class="stat-icon" style="background: var(--warning-light); color: var(--warning);">🎖️</div>
                    <div>
                        <div class="stat-value" id="stat-plan">-</div>
                        <div class="stat-label">My Membership</div>
                    </div>
                </div>
                <div class="stat-card" style="cursor: pointer;" onclick="router.navigate('fines')">
                    <div class="stat-icon" style="background: var(--danger-light); color: var(--danger);">💸</div>
                    <div>
                        <div class="stat-value" id="stat-pending-fines">-</div>
                        <div class="stat-label">Unpaid Fines</div>
                    </div>
                </div>
            </div>

            <!-- Profile & Active Subscription Overview -->
            <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(320px, 1fr)); gap: 1.5rem; margin-bottom: 1.5rem;">
                <div class="card" style="margin-bottom: 0;">
                    <div class="card-header">
                        <h2 class="card-title">My Account</h2>
                        <span id="profile-role-badge"></span>
                    </div>
                    <div id="profile-details">
                        <div class="spinner" style="margin: 2rem auto;"></div>
                    </div>
                </div>
                <div class="card" style="margin-bottom: 0;">
                    <div class="card-header">
                        <h2 class="card-title">Current Subscription</h2>
                        <a href="#subscriptions" class="btn btn-outline btn-sm">Manage</a>
                    </div>
                    <div id="active-subscription-details">
                        <div class="spinner" style="margin: 2rem auto;"></div>
                    </div>
                </div>
            </div>

            <!-- Active Loans Preview -->
            <div class="card">
                <div class="card-header">
                    <h2 class="card-title">My Active Loans</h2>
                    <a href="#loans" class="btn btn-primary btn-sm">View All Loans</a>
                </div>
                <div id="active-loans-table">
                    <div class="spinner" style="margin: 2rem auto;"></div>
                </div>
            </div>

            <!-- Future Roadmap Notice -->
            ${utils.renderFutureFeatureNote('Book Reviews, Reservations & Wishlist')}
        `;

        this.loadDashboardData();
    },

    async loadDashboardData() {
        // Load Book Stats, Genres & User Fines
        try {
            const [bookStats, genreCount, userFines] = await Promise.all([
                api.books.getStats().catch(() => ({ totalActiveBooks: 0, totalAvailableBooks: 0 })),
                api.genres.getCount().catch(() => 0),
                api.fines.getMyFines({ status: 'PENDING' }).catch(() => [])
            ]);

            document.getElementById('stat-active-books').textContent = bookStats.totalActiveBooks || 0;
            document.getElementById('stat-available-books').textContent = bookStats.totalAvailableBooks || 0;
            document.getElementById('stat-genres').textContent = genreCount || 0;

            const pendingFinesEl = document.getElementById('stat-pending-fines');
            if (pendingFinesEl) {
                let pendingTotal = 0;
                (userFines || []).forEach(f => {
                    pendingTotal += (f.amountOutstanding !== undefined && f.amountOutstanding !== null ? f.amountOutstanding : f.amount);
                });
                pendingFinesEl.textContent = utils.formatCurrency(pendingTotal);
                if (pendingTotal > 0) {
                    pendingFinesEl.style.color = 'var(--danger)';
                } else {
                    pendingFinesEl.style.color = 'var(--success)';
                }
            }
        } catch (e) {
            console.error('Failed to load stats:', e);
        }

        // Load Profile
        try {
            const profile = await api.users.getProfile();
            const profileContainer = document.getElementById('profile-details');
            const roleBadgeContainer = document.getElementById('profile-role-badge');

            if (roleBadgeContainer) {
                roleBadgeContainer.innerHTML = profile.role === 'ROLE_ADMIN' 
                    ? '<span class="badge badge-danger">Administrator</span>' 
                    : '<span class="badge badge-info">Member</span>';
            }

            if (profileContainer) {
                profileContainer.innerHTML = `
                    <div style="display: flex; flex-direction: column; gap: 0.65rem; font-size: 0.9rem;">
                        <div><strong>Full Name:</strong> ${utils.escapeHtml(profile.fullName)}</div>
                        <div><strong>Email:</strong> ${utils.escapeHtml(profile.email)}</div>
                        <div><strong>Phone:</strong> ${utils.escapeHtml(profile.phone || 'Not provided')}</div>
                        <div><strong>User ID:</strong> #${profile.id}</div>
                        <div><strong>Last Login:</strong> ${utils.formatDateTime(profile.lastLogin)}</div>
                    </div>
                `;
            }
        } catch (e) {
            const profileContainer = document.getElementById('profile-details');
            if (profileContainer) profileContainer.innerHTML = '<p class="text-muted">Unable to load profile.</p>';
        }

        // Load Active Subscription
        try {
            const sub = await api.subscriptions.getActive();
            const subContainer = document.getElementById('active-subscription-details');
            const statPlan = document.getElementById('stat-plan');

            if (statPlan) {
                statPlan.textContent = sub ? (sub.planName || 'Active') : 'No Plan';
            }

            if (subContainer) {
                if (sub) {
                    subContainer.innerHTML = `
                        <div style="display: flex; flex-direction: column; gap: 0.65rem; font-size: 0.9rem;">
                            <div style="display: flex; justify-content: space-between; align-items: center;">
                                <strong>Plan:</strong> 
                                <span class="badge badge-success">${utils.escapeHtml(sub.planName || sub.planCode)}</span>
                            </div>
                            <div><strong>Max Books Allowed:</strong> ${sub.maxBooksAllowed}</div>
                            <div><strong>Max Days Per Book:</strong> ${sub.maxDaysPerBook} days</div>
                            <div><strong>Valid Until:</strong> ${utils.formatDate(sub.endDate)} (${sub.daysRemaining || 0} days remaining)</div>
                            <div><strong>Status:</strong> ${utils.renderActiveBadge(sub.isActive)}</div>
                        </div>
                    `;
                } else {
                    subContainer.innerHTML = `
                        <div class="text-center" style="padding: 1.5rem 0;">
                            <p class="text-muted mb-2">You don't have an active membership plan.</p>
                            <a href="#subscriptions" class="btn btn-primary btn-sm">Subscribe to a Plan</a>
                        </div>
                    `;
                }
            }
        } catch (e) {
            const subContainer = document.getElementById('active-subscription-details');
            if (subContainer) {
                subContainer.innerHTML = `
                    <div class="text-center" style="padding: 1rem 0;">
                        <p class="text-muted mb-2">No active subscription found.</p>
                        <a href="#subscriptions" class="btn btn-primary btn-sm">Get Subscription</a>
                    </div>
                `;
            }
            const statPlan = document.getElementById('stat-plan');
            if (statPlan) statPlan.textContent = 'None';
        }

        // Load Active Loans Preview
        try {
            const loansRes = await api.loans.getMyLoans({ status: 'CHECKED_OUT', page: 0, size: 5 });
            const loansContainer = document.getElementById('active-loans-table');
            const items = loansRes.content || [];

            if (loansContainer) {
                if (items.length === 0) {
                    loansContainer.innerHTML = `
                        <p class="text-muted text-center" style="padding: 2rem 0;">No active loans right now. You can borrow available books anytime!</p>
                    `;
                } else {
                    loansContainer.innerHTML = `
                        <div class="table-responsive">
                            <table class="table">
                                <thead>
                                    <tr>
                                        <th>Book</th>
                                        <th>Checked Out</th>
                                        <th>Due Date</th>
                                        <th>Remaining</th>
                                        <th>Status</th>
                                        <th>Action</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    ${items.map(loan => `
                                        <tr>
                                            <td>
                                                <strong>${utils.escapeHtml(loan.bookTitle || 'Book #' + loan.bookId)}</strong>
                                                <div class="text-muted text-sm">${utils.escapeHtml(loan.bookAuthor || '')}</div>
                                            </td>
                                            <td>${utils.formatDate(loan.checkoutDate)}</td>
                                            <td>${utils.formatDate(loan.dueDate)}</td>
                                            <td>${loan.remainingDays !== undefined ? loan.remainingDays + ' days' : '-'}</td>
                                            <td>${utils.renderLoanStatusBadge(loan.status)}</td>
                                            <td>
                                                <a href="#loans" class="btn btn-outline btn-sm">Manage</a>
                                            </td>
                                        </tr>
                                    `).join('')}
                                </tbody>
                            </table>
                        </div>
                    `;
                }
            }
        } catch (e) {
            const loansContainer = document.getElementById('active-loans-table');
            if (loansContainer) loansContainer.innerHTML = '<p class="text-muted">Unable to load loans.</p>';
        }
    }
};
