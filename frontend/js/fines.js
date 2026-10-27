// Member Fines Management & Payment Portal

const fines = {
    currentStatus: '',
    currentType: '',
    cachedFines: [],

    async renderMyFines(params = {}) {
        const container = document.getElementById('app-content');
        if (!container) return;

        if (params.status !== undefined) this.currentStatus = params.status;
        if (params.type !== undefined) this.currentType = params.type;

        container.innerHTML = `
            <div class="page-header">
                <div>
                    <h1 class="page-title">My Fines & Penalties</h1>
                    <p class="page-subtitle">View and settle overdue fees, damage assessments, and library penalties</p>
                </div>
                <button class="btn btn-outline" onclick="fines.renderMyFines()">🔄 Refresh</button>
            </div>

            <!-- Stats Overview -->
            <div class="stats-grid" id="fines-stats-grid">
                <div class="stat-card">
                    <div class="stat-icon" style="background: var(--danger-light); color: var(--danger);">💸</div>
                    <div>
                        <div class="stat-value" id="stat-fine-outstanding">-</div>
                        <div class="stat-label">Total Outstanding</div>
                    </div>
                </div>
                <div class="stat-card">
                    <div class="stat-icon" style="background: var(--warning-light); color: var(--warning);">⏳</div>
                    <div>
                        <div class="stat-value" id="stat-fine-pending-count">-</div>
                        <div class="stat-label">Pending Fines</div>
                    </div>
                </div>
                <div class="stat-card">
                    <div class="stat-icon" style="background: var(--success-light); color: var(--success);">✅</div>
                    <div>
                        <div class="stat-value" id="stat-fine-paid-total">-</div>
                        <div class="stat-label">Total Amount Paid</div>
                    </div>
                </div>
                <div class="stat-card">
                    <div class="stat-icon" style="background: #f1f5f9; color: var(--secondary);">🛡️</div>
                    <div>
                        <div class="stat-value" id="stat-fine-waived-count">-</div>
                        <div class="stat-label">Waived Penalties</div>
                    </div>
                </div>
            </div>

            <!-- Filters -->
            <div class="filter-bar" style="display: flex; gap: 0.75rem; flex-wrap: wrap; align-items: center; justify-content: space-between;">
                <div style="display: flex; gap: 0.5rem; flex-wrap: wrap;">
                    <button class="btn btn-sm ${this.currentStatus === '' ? 'btn-primary' : 'btn-outline'}" onclick="fines.filterStatus('')">All Fines</button>
                    <button class="btn btn-sm ${this.currentStatus === 'PENDING' ? 'btn-primary' : 'btn-outline'}" onclick="fines.filterStatus('PENDING')">Pending</button>
                    <button class="btn btn-sm ${this.currentStatus === 'PAID' ? 'btn-primary' : 'btn-outline'}" onclick="fines.filterStatus('PAID')">Paid</button>
                    <button class="btn btn-sm ${this.currentStatus === 'WAIVED' ? 'btn-primary' : 'btn-outline'}" onclick="fines.filterStatus('WAIVED')">Waived</button>
                </div>
                <div style="display: flex; gap: 0.5rem; align-items: center;">
                    <label class="text-sm text-muted" style="margin: 0;">Type:</label>
                    <select id="fine-type-filter" class="form-control" style="width: auto; padding: 0.35rem 0.75rem;" onchange="fines.filterType(this.value)">
                        <option value="" ${this.currentType === '' ? 'selected' : ''}>All Types</option>
                        <option value="OVERDUE" ${this.currentType === 'OVERDUE' ? 'selected' : ''}>Overdue</option>
                        <option value="DAMAGE" ${this.currentType === 'DAMAGE' ? 'selected' : ''}>Damage</option>
                        <option value="LOSS" ${this.currentType === 'LOSS' ? 'selected' : ''}>Lost Book</option>
                        <option value="PROCESSING" ${this.currentType === 'PROCESSING' ? 'selected' : ''}>Processing</option>
                    </select>
                </div>
            </div>

            <!-- Fines Table Container -->
            <div id="fines-table-container">
                <div class="spinner" style="margin: 3rem auto;"></div>
            </div>
        `;

        this.loadMyFines();
    },

    filterStatus(status) {
        this.currentStatus = status;
        this.loadMyFines();
    },

    filterType(type) {
        this.currentType = type;
        this.loadMyFines();
    },

    async loadMyFines() {
        const tableContainer = document.getElementById('fines-table-container');
        if (!tableContainer) return;

        tableContainer.innerHTML = '<div class="spinner" style="margin: 3rem auto;"></div>';

        try {
            // First fetch all fines without filter to calculate global summary metrics
            const allUserFines = await api.fines.getMyFines({});
            this.updateSummaryStats(allUserFines || []);

            // Now filter by selected status & type
            let filteredList = allUserFines || [];
            if (this.currentStatus) {
                filteredList = filteredList.filter(f => f.status === this.currentStatus);
            }
            if (this.currentType) {
                filteredList = filteredList.filter(f => f.type === this.currentType);
            }

            this.cachedFines = filteredList;

            if (filteredList.length === 0) {
                tableContainer.innerHTML = `
                    <div class="card text-center" style="padding: 3rem 1.5rem;">
                        <span style="font-size: 2.5rem;">✨</span>
                        <h3 class="mt-2">No fines found</h3>
                        <p class="text-muted text-sm mt-1">
                            ${this.currentStatus || this.currentType 
                                ? 'No fines match the selected filters.' 
                                : 'You currently have no recorded fines or penalties.'}
                        </p>
                    </div>
                `;
                return;
            }

            tableContainer.innerHTML = `
                <div class="table-responsive">
                    <table class="table">
                        <thead>
                            <tr>
                                <th>Fine ID</th>
                                <th>Penalty Type</th>
                                <th>Book & Loan Details</th>
                                <th>Amount</th>
                                <th>Outstanding</th>
                                <th>Status</th>
                                <th>Issued Date</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            ${filteredList.map(fine => {
                                const isPending = fine.status === 'PENDING' || fine.status === 'PARTIALLY_PAID';
                                const isPaid = fine.status === 'PAID';
                                const isWaived = fine.status === 'WAIVED';

                                return `
                                    <tr>
                                        <td><strong>#${fine.id}</strong></td>
                                        <td>
                                            ${utils.renderFineTypeBadge(fine.type)}
                                            ${fine.reason ? `<div class="text-muted text-sm" style="max-width: 220px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap;" title="${utils.escapeHtml(fine.reason)}">${utils.escapeHtml(fine.reason)}</div>` : ''}
                                        </td>
                                        <td>
                                            <strong>${utils.escapeHtml(fine.bookTitle || 'Book Loan #' + (fine.bookLoanId || '-'))}</strong>
                                            ${fine.bookIsbn ? `<div class="text-muted text-sm">ISBN: <code>${utils.escapeHtml(fine.bookIsbn)}</code></div>` : ''}
                                            ${fine.bookLoanId ? `<div class="text-muted text-sm">Loan ID: #${fine.bookLoanId}</div>` : ''}
                                        </td>
                                        <td>
                                            <strong>${utils.formatCurrency(fine.amount)}</strong>
                                        </td>
                                        <td>
                                            ${fine.amountOutstanding !== undefined && fine.amountOutstanding !== null
                                                ? (fine.amountOutstanding > 0 
                                                    ? `<span style="color: var(--danger); font-weight: 700;">${utils.formatCurrency(fine.amountOutstanding)}</span>` 
                                                    : `<span style="color: var(--success); font-weight: 600;">₹0.00</span>`)
                                                : (isPending ? `<span style="color: var(--danger); font-weight: 700;">${utils.formatCurrency(fine.amount)}</span>` : '₹0.00')}
                                        </td>
                                        <td>${utils.renderFineStatusBadge(fine.status)}</td>
                                        <td>
                                            <div>${utils.formatDate(fine.createdAt)}</div>
                                            <div class="text-muted text-sm">${utils.formatDateTime(fine.createdAt).split(',')[1] || ''}</div>
                                        </td>
                                        <td>
                                            <div style="display: flex; gap: 0.35rem; flex-wrap: wrap;">
                                                ${isPending ? `
                                                    <button class="btn btn-primary btn-sm" onclick="fines.openPayModal(${fine.id})">
                                                        💳 Pay Now
                                                    </button>
                                                ` : ''}
                                                <button class="btn btn-outline btn-sm" onclick="fines.openDetailsModal(${fine.id})">
                                                    View Details
                                                </button>
                                            </div>
                                        </td>
                                    </tr>
                                `;
                            }).join('')}
                        </tbody>
                    </table>
                </div>
            `;
        } catch (err) {
            console.error('Failed to load fines:', err);
            tableContainer.innerHTML = `
                <div class="card text-center" style="padding: 2.5rem;">
                    <p class="text-danger">Failed to load fines: ${utils.escapeHtml(err.message || 'Server error')}</p>
                    <button class="btn btn-outline btn-sm mt-2" onclick="fines.loadMyFines()">Try Again</button>
                </div>
            `;
        }
    },

    updateSummaryStats(finesList) {
        let outstanding = 0;
        let pendingCount = 0;
        let paidTotal = 0;
        let waivedCount = 0;

        finesList.forEach(f => {
            if (f.status === 'PENDING' || f.status === 'PARTIALLY_PAID') {
                outstanding += (f.amountOutstanding !== undefined && f.amountOutstanding !== null ? f.amountOutstanding : f.amount);
                pendingCount++;
            } else if (f.status === 'PAID') {
                paidTotal += (f.amountPaid || f.amount || 0);
            } else if (f.status === 'WAIVED') {
                waivedCount++;
            }
        });

        const outEl = document.getElementById('stat-fine-outstanding');
        const penEl = document.getElementById('stat-fine-pending-count');
        const paidEl = document.getElementById('stat-fine-paid-total');
        const waivEl = document.getElementById('stat-fine-waived-count');

        if (outEl) outEl.textContent = utils.formatCurrency(outstanding);
        if (penEl) penEl.textContent = pendingCount;
        if (paidEl) paidEl.textContent = utils.formatCurrency(paidTotal);
        if (waivEl) waivEl.textContent = waivedCount;
    },

    openPayModal(fineId) {
        const fine = this.cachedFines.find(f => f.id === fineId);
        if (!fine) {
            utils.toast('Fine record not found', 'error');
            return;
        }

        const amountToPay = (fine.amountOutstanding !== undefined && fine.amountOutstanding !== null)
            ? fine.amountOutstanding
            : fine.amount;

        utils.showModal({
            title: `Pay Fine #${fine.id} - ${fine.type}`,
            bodyHtml: `
                <div style="padding: 0.5rem 0;">
                    <!-- Fine Summary Card -->
                    <div class="card" style="background: var(--bg-main); border: 1px solid var(--border); margin-bottom: 1.25rem;">
                        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 0.5rem;">
                            <span class="text-sm text-muted">Fine ID: #${fine.id}</span>
                            ${utils.renderFineTypeBadge(fine.type)}
                        </div>
                        <h3 style="font-size: 1.1rem; font-weight: 700; margin-bottom: 0.25rem;">
                            ${utils.escapeHtml(fine.bookTitle || 'Loan #' + fine.bookLoanId)}
                        </h3>
                        ${fine.reason ? `<p class="text-sm text-muted mb-2">Reason: ${utils.escapeHtml(fine.reason)}</p>` : ''}
                        
                        <div style="margin-top: 0.75rem; padding-top: 0.75rem; border-top: 1px dashed var(--border); display: flex; justify-content: space-between; align-items: baseline;">
                            <span style="font-size: 0.95rem; font-weight: 600;">Amount Due:</span>
                            <span style="font-size: 1.5rem; font-weight: 800; color: var(--danger);">
                                ${utils.formatCurrency(amountToPay)}
                            </span>
                        </div>
                    </div>

                    <!-- Payment Mode Selection -->
                    <div style="display: flex; gap: 0.5rem; margin-bottom: 1.25rem; border-bottom: 1px solid var(--border); padding-bottom: 0.5rem;">
                        <button type="button" id="tab-btn-online" class="btn btn-sm btn-primary" onclick="fines.switchPayTab('online')">
                            💳 Online (Razorpay)
                        </button>
                        <button type="button" id="tab-btn-offline" class="btn btn-sm btn-outline" onclick="fines.switchPayTab('offline')">
                            🏦 Direct / Reference ID
                        </button>
                    </div>

                    <!-- Tab 1: Online Payment -->
                    <div id="pay-tab-online">
                        <div style="background: #f0fdf4; border: 1px solid #bbf7d0; border-radius: var(--radius-md); padding: 1rem; margin-bottom: 1rem;">
                            <div style="display: flex; align-items: center; gap: 0.5rem;">
                                <span style="font-size: 1.25rem;">🛡️</span>
                                <strong style="color: #166534;">Instant Online Settlement</strong>
                            </div>
                            <p style="font-size: 0.85rem; color: #15803d; margin-top: 0.25rem; line-height: 1.4;">
                                Secure checkout with Razorpay supporting UPI, Debit/Credit Cards, Net Banking & Wallets. Your fine status updates immediately upon completion.
                            </p>
                        </div>
                        <button class="btn btn-primary w-full" id="btn-submit-online-pay" onclick="fines.processOnlinePayment(${fine.id})">
                            Pay ${utils.formatCurrency(amountToPay)} with Razorpay
                        </button>
                    </div>

                    <!-- Tab 2: Offline / Manual Transaction ID -->
                    <div id="pay-tab-offline" class="hidden">
                        <div style="background: #fffbeb; border: 1px solid #fef3c7; border-radius: var(--radius-md); padding: 0.85rem; margin-bottom: 1rem;">
                            <p style="font-size: 0.85rem; color: #92400e; margin: 0; line-height: 1.4;">
                                Already paid at the library desk or via direct transfer? Enter your bank reference, UPI transaction ID, or cash receipt number below.
                            </p>
                        </div>
                        <div class="form-group">
                            <label class="form-label" for="manual-txn-id">Transaction Reference / Receipt ID <span style="color: red;">*</span></label>
                            <input type="text" id="manual-txn-id" class="form-control" placeholder="e.g. UPI/1234567890 or CASH-REC-450" />
                        </div>
                        <button class="btn btn-success w-full" id="btn-submit-offline-pay" onclick="fines.processManualPayment(${fine.id})">
                            Confirm Settle with Reference ID
                        </button>
                    </div>
                </div>
            `,
            footerHtml: `
                <button class="btn btn-outline" onclick="utils.closeModal()">Cancel</button>
            `
        });
    },

    switchPayTab(tab) {
        const onlineTab = document.getElementById('pay-tab-online');
        const offlineTab = document.getElementById('pay-tab-offline');
        const btnOnline = document.getElementById('tab-btn-online');
        const btnOffline = document.getElementById('tab-btn-offline');

        if (tab === 'online') {
            if (onlineTab) onlineTab.classList.remove('hidden');
            if (offlineTab) offlineTab.classList.add('hidden');
            if (btnOnline) { btnOnline.className = 'btn btn-sm btn-primary'; }
            if (btnOffline) { btnOffline.className = 'btn btn-sm btn-outline'; }
        } else {
            if (onlineTab) onlineTab.classList.add('hidden');
            if (offlineTab) offlineTab.classList.remove('hidden');
            if (btnOnline) { btnOnline.className = 'btn btn-sm btn-outline'; }
            if (btnOffline) { btnOffline.className = 'btn btn-sm btn-primary'; }
        }
    },

    async processOnlinePayment(fineId) {
        const btn = document.getElementById('btn-submit-online-pay');
        if (btn) {
            btn.disabled = true;
            btn.textContent = 'Initiating Gateway...';
        }

        try {
            // Calling payFine without transactionId triggers PaymentService initiation
            const payRes = await api.fines.pay(fineId);

            if (payRes && payRes.checkOutUrl) {
                utils.closeModal();
                window.open(payRes.checkOutUrl, '_blank');

                utils.showModal({
                    title: 'Complete Razorpay Checkout',
                    bodyHtml: `
                        <div style="text-align: center; padding: 1.5rem 0.5rem;">
                            <span style="font-size: 3rem;">🔗</span>
                            <h3 class="mt-2">Payment Session Opened</h3>
                            <p class="text-muted text-sm mt-1">
                                Complete your payment in the opened Razorpay tab. Once done, verify the settlement below.
                            </p>
                            ${payRes.transactionId ? `<div class="mt-3"><small class="text-muted">Tracking Ref: <code>${utils.escapeHtml(payRes.transactionId)}</code></small></div>` : ''}
                            <div class="mt-4">
                                <a href="${payRes.checkOutUrl}" target="_blank" class="btn btn-outline btn-sm">Re-open Checkout Link ↗</a>
                            </div>
                        </div>
                    `,
                    footerHtml: `
                        <button class="btn btn-outline" onclick="utils.closeModal()">Close</button>
                        <button class="btn btn-primary" onclick="fines.confirmPaymentRefresh()">I Have Completed Payment</button>
                    `
                });
            } else if (payRes && payRes.success) {
                utils.closeModal();
                utils.toast(payRes.message || 'Payment successfully processed!', 'success');
                this.loadMyFines();
            } else {
                throw new Error(payRes.message || 'Unable to initiate payment');
            }
        } catch (err) {
            console.error('Online payment error:', err);
            utils.toast(err.message || 'Failed to initiate payment', 'error');
            if (btn) {
                btn.disabled = false;
                btn.textContent = 'Try Again';
            }
        }
    },

    async processManualPayment(fineId) {
        const input = document.getElementById('manual-txn-id');
        const txnId = input ? input.value.trim() : '';

        if (!txnId) {
            utils.toast('Please provide a transaction or receipt reference ID.', 'warning');
            if (input) input.focus();
            return;
        }

        const btn = document.getElementById('btn-submit-offline-pay');
        if (btn) {
            btn.disabled = true;
            btn.textContent = 'Verifying...';
        }

        try {
            const res = await api.fines.pay(fineId, txnId);
            utils.closeModal();
            utils.toast(res.message || 'Fine marked as paid successfully!', 'success');
            this.loadMyFines();
        } catch (err) {
            console.error('Manual settlement error:', err);
            utils.toast(err.message || 'Failed to record payment', 'error');
            if (btn) {
                btn.disabled = false;
                btn.textContent = 'Confirm Settle with Reference ID';
            }
        }
    },

    confirmPaymentRefresh() {
        utils.closeModal();
        utils.toast('Refreshing fine records...', 'info');
        this.loadMyFines();
    },

    openDetailsModal(fineId) {
        const fine = this.cachedFines.find(f => f.id === fineId);
        if (!fine) return;

        utils.showModal({
            title: `Fine Details #${fine.id}`,
            bodyHtml: `
                <div style="display: flex; flex-direction: column; gap: 1rem; padding: 0.5rem 0;">
                    <div style="display: flex; justify-content: space-between; align-items: center;">
                        <span style="font-size: 1.1rem; font-weight: 700;">Fine #${fine.id}</span>
                        <div>
                            ${utils.renderFineTypeBadge(fine.type)}
                            ${utils.renderFineStatusBadge(fine.status)}
                        </div>
                    </div>

                    <div class="card" style="background: var(--bg-main); border: 1px solid var(--border); margin: 0; padding: 1rem;">
                        <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(180px, 1fr)); gap: 0.85rem; font-size: 0.875rem;">
                            <div>
                                <span class="text-muted">Total Amount:</span>
                                <div><strong style="font-size: 1.1rem;">${utils.formatCurrency(fine.amount)}</strong></div>
                            </div>
                            <div>
                                <span class="text-muted">Amount Outstanding:</span>
                                <div><strong style="font-size: 1.1rem; color: ${fine.status === 'PAID' || fine.status === 'WAIVED' ? 'var(--success)' : 'var(--danger)'};">${utils.formatCurrency(fine.amountOutstanding || 0)}</strong></div>
                            </div>
                            <div>
                                <span class="text-muted">Associated Book:</span>
                                <div><strong>${utils.escapeHtml(fine.bookTitle || '-')}</strong></div>
                            </div>
                            <div>
                                <span class="text-muted">ISBN:</span>
                                <div><code>${utils.escapeHtml(fine.bookIsbn || '-')}</code></div>
                            </div>
                            <div>
                                <span class="text-muted">Loan Reference:</span>
                                <div>Loan #${fine.bookLoanId || '-'}</div>
                            </div>
                            <div>
                                <span class="text-muted">Created Date:</span>
                                <div>${utils.formatDateTime(fine.createdAt)}</div>
                            </div>
                        </div>
                    </div>

                    ${fine.reason ? `
                        <div>
                            <strong style="font-size: 0.85rem; color: var(--text-secondary);">Reason for Fine:</strong>
                            <p style="background: #f8fafc; padding: 0.65rem 0.85rem; border-radius: var(--radius-sm); border: 1px solid var(--border); margin-top: 0.25rem; font-size: 0.875rem;">
                                ${utils.escapeHtml(fine.reason)}
                            </p>
                        </div>
                    ` : ''}

                    ${fine.notes ? `
                        <div>
                            <strong style="font-size: 0.85rem; color: var(--text-secondary);">Staff Notes:</strong>
                            <p style="background: #f8fafc; padding: 0.65rem 0.85rem; border-radius: var(--radius-sm); border: 1px solid var(--border); margin-top: 0.25rem; font-size: 0.875rem;">
                                ${utils.escapeHtml(fine.notes)}
                            </p>
                        </div>
                    ` : ''}

                    <!-- Payment Information if Paid -->
                    ${fine.status === 'PAID' ? `
                        <div style="background: #f0fdf4; border: 1px solid #bbf7d0; border-radius: var(--radius-md); padding: 0.85rem;">
                            <strong style="color: #166534; font-size: 0.9rem;">Payment Information</strong>
                            <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(180px, 1fr)); gap: 0.5rem; font-size: 0.85rem; margin-top: 0.5rem; color: #15803d;">
                                <div>Paid Date: <strong>${utils.formatDateTime(fine.paidAt)}</strong></div>
                                <div>Transaction ID: <code>${utils.escapeHtml(fine.transactionId || 'Direct Payment')}</code></div>
                                ${fine.processByUserName ? `<div>Processed By: <strong>${utils.escapeHtml(fine.processByUserName)}</strong></div>` : ''}
                            </div>
                        </div>
                    ` : ''}

                    <!-- Waiver Information if Waived -->
                    ${fine.status === 'WAIVED' ? `
                        <div style="background: #f1f5f9; border: 1px solid var(--border); border-radius: var(--radius-md); padding: 0.85rem;">
                            <strong style="color: var(--text-primary); font-size: 0.9rem;">Waiver Information</strong>
                            <div style="font-size: 0.85rem; margin-top: 0.5rem; color: var(--text-secondary);">
                                <div>Waived At: <strong>${utils.formatDateTime(fine.waivedAt)}</strong></div>
                                ${fine.waivedByUserName ? `<div>Waived By: <strong>${utils.escapeHtml(fine.waivedByUserName)}</strong></div>` : ''}
                                ${fine.waiverReason ? `<div style="margin-top: 0.35rem;">Reason for Waiver: <em style="color: var(--text-primary);">"${utils.escapeHtml(fine.waiverReason)}"</em></div>` : ''}
                            </div>
                        </div>
                    ` : ''}
                </div>
            `,
            footerHtml: `
                <button class="btn btn-outline" onclick="utils.closeModal()">Close</button>
                ${fine.status === 'PENDING' || fine.status === 'PARTIALLY_PAID' ? `
                    <button class="btn btn-primary" onclick="fines.openPayModal(${fine.id})">Proceed to Pay</button>
                ` : ''}
            `
        });
    }
};
