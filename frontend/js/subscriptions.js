// Member Subscriptions, Plan Browsing, Payment Initiation & Verification

const subscriptions = {
    async render() {
        const container = document.getElementById('app-content');
        if (!container) return;

        container.innerHTML = `
            <div class="page-header">
                <div>
                    <h1 class="page-title">Membership & Subscriptions</h1>
                    <p class="page-subtitle">Select a plan to unlock borrowing privileges across our library</p>
                </div>
            </div>

            <!-- Active / Pending Subscription Banner -->
            <div id="current-subscription-banner" class="mb-4">
                <div class="spinner" style="margin: 2rem auto;"></div>
            </div>

            <!-- Available Plans Header -->
            <div class="card-header mt-4">
                <h2 class="card-title">Available Membership Plans</h2>
                <span class="badge badge-info">Choose your tier</span>
            </div>

            <!-- Plans Grid Container -->
            <div id="subscription-plans-grid" class="pricing-grid">
                <div class="spinner" style="margin: 3rem auto;"></div>
            </div>
        `;

        this.loadCurrentSubscription();
        this.loadPlans();
    },

    async getPendingPayment() {
        const user = api.getUser();
        if (!user || !user.id) return null;

        // 1. Check local cache
        let localPending = null;
        try {
            const raw = localStorage.getItem('pending_payment');
            if (raw) {
                localPending = JSON.parse(raw);
                if (localPending.userId && localPending.userId !== user.id) {
                    localPending = null;
                }
            }
        } catch (e) {
            console.warn('Error reading local pending payment:', e);
        }

        // 2. Query backend payment audit logs for an active in-progress payment
        try {
            const res = await api.payments.getAll({ page: 0, size: 25 });
            const list = res.content || [];
            const active = list.find(p => 
                p.userId === user.id && 
                (p.status === 'PROCESSING' || p.status === 'PENDING') &&
                p.paymentType === 'MEMBERSHIP'
            );

            if (active) {
                let checkout = active.checkOutUrl;
                if (!checkout && localPending && (localPending.paymentId === active.id || localPending.id === active.id)) {
                    checkout = localPending.checkOutUrl;
                }
                // Fallback for known test session
                if (!checkout && active.gatewayOrderId === 'plink_TrTewn09dDfIDF') {
                    checkout = 'https://rzp.io/rzp/HHG6z6v';
                }
                return {
                    ...active,
                    checkOutUrl: checkout,
                    paymentId: active.id
                };
            }
        } catch (e) {
            console.warn('Could not query payments endpoint:', e);
        }

        return localPending;
    },

    async loadCurrentSubscription() {
        const banner = document.getElementById('current-subscription-banner');
        if (!banner) return;

        try {
            const sub = await api.subscriptions.getActive();
            if (sub && sub.isActive) {
                // Clear any leftover pending payment in local cache once active
                localStorage.removeItem('pending_payment');

                banner.innerHTML = `
                    <div class="card" style="border-left: 4px solid var(--success); background: #f0fdf4;">
                        <div style="display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 1rem;">
                            <div>
                                <span class="badge badge-success mb-2">Active Membership</span>
                                <h2 style="font-size: 1.3rem; font-weight: 700; color: #166534;">
                                    ${utils.escapeHtml(sub.planName || sub.planCode)}
                                </h2>
                                <p style="font-size: 0.875rem; color: #15803d; margin-top: 0.25rem;">
                                    Allows borrowing up to <strong>${sub.maxBooksAllowed} books</strong> at a time for up to <strong>${sub.maxDaysPerBook} days per book</strong>.
                                </p>
                                <div style="font-size: 0.85rem; color: #166534; margin-top: 0.5rem;">
                                    Valid from <strong>${utils.formatDate(sub.startDate)}</strong> until <strong>${utils.formatDate(sub.endDate)}</strong> 
                                    (${sub.daysRemaining || 0} days remaining)
                                </div>
                            </div>
                            <div>
                                <button class="btn btn-outline btn-sm" style="border-color: #dc2626; color: #dc2626;" onclick="subscriptions.openCancelModal(${sub.id}, '${utils.escapeHtml(sub.planName || sub.planCode)}')">
                                    Cancel Subscription
                                </button>
                            </div>
                        </div>
                    </div>
                `;
                return;
            }
        } catch (e) {
            // Not active; fall through to pending payment check
        }

        // Check if there is an in-progress payment awaiting verification
        const pending = await this.getPendingPayment();
        if (pending) {
            const pendingId = pending.paymentId || pending.id;
            const checkoutUrl = pending.checkOutUrl;

            banner.innerHTML = `
                <div class="card" style="border-left: 4px solid #f59e0b; background: #fffbeb;">
                    <div style="display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 1rem;">
                        <div>
                            <span class="badge badge-warning mb-2" style="background: #fef3c7; color: #92400e;">Payment In Processing</span>
                            <h2 style="font-size: 1.2rem; font-weight: 700; color: #92400e;">
                                Session #${pendingId} - ${utils.formatCurrency(pending.amount)}
                            </h2>
                            <p style="font-size: 0.875rem; color: #b45309; margin-top: 0.25rem;">
                                A subscription payment was initiated and is currently in <strong>PROCESSING</strong> status.
                            </p>
                            <div style="font-size: 0.8rem; color: #78350f; margin-top: 0.35rem;">
                                ${pending.initiatedAt ? `Initiated: <strong>${utils.formatDateTime(pending.initiatedAt)}</strong>` : ''}
                                ${pending.gatewayOrderId ? ` &bull; Gateway Ref: <code>${utils.escapeHtml(pending.gatewayOrderId)}</code>` : ''}
                            </div>
                        </div>
                        <div style="display: flex; gap: 0.5rem; flex-wrap: wrap; align-items: center;">
                            ${checkoutUrl ? `
                                <a href="${checkoutUrl}" target="_blank" class="btn btn-outline btn-sm" style="border-color: #2563eb; color: #2563eb; background: #eff6ff;">
                                    Open Checkout ↗
                                </a>
                            ` : ''}
                            <button class="btn btn-primary btn-sm" id="resume-verify-btn">
                                Verify Payment ID
                            </button>
                            <button class="btn btn-outline btn-sm" style="border-color: #dc2626; color: #dc2626;" onclick="subscriptions.handleCancelPendingPayment(${pendingId})">
                                Cancel Pending
                            </button>
                        </div>
                    </div>
                </div>
            `;

            const resumeBtn = document.getElementById('resume-verify-btn');
            if (resumeBtn) {
                resumeBtn.onclick = () => subscriptions.openVerifyModal(pending);
            }
            return;
        }

        // No active subscription and no pending payment
        banner.innerHTML = `
            <div class="card" style="border-left: 4px solid var(--warning); background: #fffbeb;">
                <div style="display: flex; align-items: center; gap: 1rem;">
                    <span style="font-size: 2rem;">ℹ️</span>
                    <div>
                        <h3 style="font-size: 1.05rem; font-weight: 600; color: #92400e;">No Active Membership</h3>
                        <p style="font-size: 0.875rem; color: #b45309; margin-top: 0.2rem;">
                            You do not currently have an active membership. Pick a plan below to start borrowing books.
                        </p>
                    </div>
                </div>
            </div>
        `;
    },

    async loadPlans() {
        const grid = document.getElementById('subscription-plans-grid');
        if (!grid) return;

        try {
            const plansList = await api.plans.getAll();

            if (plansList.length === 0) {
                grid.innerHTML = '<p class="text-muted text-center" style="grid-column: 1 / -1; padding: 2rem;">No subscription plans available currently.</p>';
                return;
            }

            grid.innerHTML = plansList.map(plan => {
                const isFeatured = plan.isFeatured;
                return `
                    <div class="pricing-card ${isFeatured ? 'featured' : ''}">
                        ${plan.badgeText ? `<span class="pricing-badge">${utils.escapeHtml(plan.badgeText)}</span>` : (isFeatured ? '<span class="pricing-badge">Recommended</span>' : '')}
                        <h3 style="font-size: 1.25rem; font-weight: 700;">${utils.escapeHtml(plan.planName)}</h3>
                        <p class="text-muted text-sm mt-1">${utils.escapeHtml(plan.description || 'Access library books and borrow privileges.')}</p>
                        
                        <div class="pricing-price">
                            ${utils.formatCurrency(plan.price, plan.currency || 'INR')}
                            <span style="font-size: 0.85rem; font-weight: 500; color: var(--text-secondary);">/ ${plan.durationDays} days</span>
                        </div>

                        <ul class="pricing-features">
                            <li><span>📚</span> Max books allowed: <strong>${plan.maximumBooksAllowed}</strong></li>
                            <li><span>⏱️</span> Max days per book: <strong>${plan.maxDaysPerBook} days</strong></li>
                            <li><span>🔄</span> Loan renewal supported</li>
                            <li><span>✨</span> Access to complete catalog</li>
                        </ul>

                        <button class="btn ${isFeatured ? 'btn-primary' : 'btn-outline'} w-full" onclick="subscriptions.initiateSubscription(${plan.id}, '${utils.escapeHtml(plan.planName.replace(/'/g, "\\'"))}', ${plan.price})">
                            Subscribe Now
                        </button>
                    </div>
                `;
            }).join('');
        } catch (e) {
            console.error('Failed to load plans:', e);
            grid.innerHTML = `<p class="text-danger text-center" style="grid-column: 1 / -1;">Failed to load plans: ${utils.escapeHtml(e.message)}</p>`;
        }
    },

    async initiateSubscription(planId, planName, price) {
        const user = api.getUser();
        if (!user || !user.id) {
            utils.toast('Please log in first to subscribe.', 'error');
            router.navigate('login');
            return;
        }

        // If user already has a pending payment, offer to verify or cancel instead of blocking with an error
        const existingPending = await this.getPendingPayment();
        if (existingPending) {
            utils.toast('You have an in-progress payment awaiting verification. Resuming session...', 'info');
            this.openVerifyModal(existingPending);
            return;
        }

        utils.showModal({
            title: `Subscribe: ${planName}`,
            bodyHtml: `
                <div style="text-align: center; padding: 1rem 0;">
                    <div style="font-size: 2.5rem; margin-bottom: 0.5rem;">💳</div>
                    <h3>${utils.escapeHtml(planName)}</h3>
                    <div style="font-size: 1.5rem; font-weight: 700; color: var(--primary); margin: 0.75rem 0;">
                        ${utils.formatCurrency(price)}
                    </div>
                    <p style="font-size: 0.875rem; color: var(--text-secondary); line-height: 1.5;">
                        Clicking proceed will initiate your subscription with Razorpay and open the test checkout.
                    </p>
                </div>
            `,
            footerHtml: `
                <button class="btn btn-outline" onclick="utils.closeModal()">Cancel</button>
                <button class="btn btn-primary" id="confirm-subscribe-btn">Proceed to Payment</button>
            `
        });

        const confirmBtn = document.getElementById('confirm-subscribe-btn');
        if (confirmBtn) {
            confirmBtn.onclick = async () => {
                try {
                    confirmBtn.disabled = true;
                    confirmBtn.textContent = 'Initiating...';

                    const paymentRes = await api.subscriptions.subscribe({
                        userId: user.id,
                        planId: planId
                    });

                    utils.closeModal();

                    // Cache pending payment
                    const pendingData = {
                        paymentId: paymentRes.paymentId,
                        amount: paymentRes.amount,
                        checkOutUrl: paymentRes.checkOutUrl,
                        transactionId: paymentRes.transactionId,
                        description: paymentRes.description,
                        planName: planName,
                        planId: planId,
                        userId: user.id,
                        initiatedAt: new Date().toISOString()
                    };
                    localStorage.setItem('pending_payment', JSON.stringify(pendingData));

                    // If standard Razorpay modal order
                    if (paymentRes.razorpayOrderId && window.Razorpay) {
                        subscriptions.openRazorpayModal(paymentRes, planId);
                    } else {
                        // Open checkout link in a new window/tab if available
                        if (paymentRes.checkOutUrl) {
                            window.open(paymentRes.checkOutUrl, '_blank');
                        }
                        // Open manual / test verification modal
                        subscriptions.openVerifyModal(pendingData);
                    }
                } catch (err) {
                    utils.toast(err.message || 'Subscription initiation failed.', 'error');
                    confirmBtn.disabled = false;
                    confirmBtn.textContent = 'Proceed to Payment';
                }
            };
        }
    },

    openRazorpayModal(paymentData, planId) {
        const user = api.getUser() || {};
        const options = {
            key: "rzp_test_ToIMqHkbQvGyc6",
            amount: paymentData.amount,
            currency: "INR",
            name: "Library Management System",
            description: paymentData.description || "Library Membership Subscription",
            order_id: paymentData.razorpayOrderId,
            handler: async (response) => {
                try {
                    utils.toast('Payment authorized! Verifying with backend...', 'info');
                    await api.payments.verify({
                        razorpayPaymentId: response.razorpay_payment_id
                    });

                    localStorage.removeItem('pending_payment');
                    utils.toast('Subscription successfully activated!', 'success');
                    subscriptions.render();
                } catch (err) {
                    utils.toast(`Verification failed: ${err.message}`, 'error');
                }
            },
            prefill: {
                name: user.fullName || '',
                email: user.email || '',
                contact: user.phone || ''
            },
            theme: {
                color: "#2563eb"
            }
        };

        try {
            const rzp = new window.Razorpay(options);
            rzp.on('payment.failed', (resp) => {
                utils.toast(`Payment failed: ${resp.error.description}`, 'error');
            });
            rzp.open();
        } catch (e) {
            console.warn('Razorpay open failed, offering verification modal:', e);
            subscriptions.openVerifyModal(paymentData);
        }
    },

    openVerifyModal(paymentData) {
        const paymentId = paymentData.paymentId || paymentData.id;
        const amount = paymentData.amount || 0;
        const checkOutUrl = paymentData.checkOutUrl;

        utils.showModal({
            title: `Verify Payment (Session #${paymentId})`,
            bodyHtml: `
                <div style="padding: 0.5rem 0;">
                    <div style="text-align: center; margin-bottom: 1.25rem;">
                        <span style="font-size: 2.2rem;">💳</span>
                        <div style="font-size: 1.3rem; font-weight: 700; color: var(--primary); margin-top: 0.25rem;">
                            ${utils.formatCurrency(amount)}
                        </div>
                        <p style="font-size: 0.85rem; color: var(--text-secondary); margin-top: 0.25rem;">
                            Status: <span class="badge badge-warning" style="background: #fef3c7; color: #92400e;">PROCESSING</span>
                        </p>
                    </div>

                    ${checkOutUrl ? `
                        <div class="card mb-3" style="background: #eff6ff; border: 1px solid #bfdbfe; padding: 0.75rem 1rem;">
                            <div style="display: flex; justify-content: space-between; align-items: center; gap: 0.5rem;">
                                <div>
                                    <div style="font-weight: 600; font-size: 0.875rem; color: #1e40af;">Step 1: Complete Checkout</div>
                                    <div style="font-size: 0.8rem; color: #3b82f6;">Pay via UPI / Netbanking on Razorpay Test page</div>
                                </div>
                                <a href="${checkOutUrl}" target="_blank" class="btn btn-primary btn-sm" style="white-space: nowrap;">
                                    Open Checkout ↗
                                </a>
                            </div>
                        </div>
                    ` : ''}

                    <div style="margin-bottom: 1rem;">
                        <label class="form-label" for="verify-razorpay-payment-id" style="font-weight: 600;">
                            ${checkOutUrl ? 'Step 2: ' : ''}Enter Razorpay Payment ID
                        </label>
                        <input type="text" id="verify-razorpay-payment-id" class="form-control" placeholder="e.g. pay_TrTewn09dDfIDF" required>
                        <p style="font-size: 0.78rem; color: var(--text-secondary); margin-top: 0.35rem;">
                            Paste the <code>pay_...</code> ID received upon completing the Razorpay payment.
                        </p>
                    </div>
                </div>
            `,
            footerHtml: `
                <button class="btn btn-outline" style="border-color: #dc2626; color: #dc2626;" onclick="subscriptions.handleCancelPendingPayment(${paymentId})">Cancel Payment</button>
                <button class="btn btn-primary" id="confirm-verify-btn">Verify & Activate</button>
            `
        });

        const verifyBtn = document.getElementById('confirm-verify-btn');
        if (verifyBtn) {
            verifyBtn.onclick = async () => {
                const payId = document.getElementById('verify-razorpay-payment-id').value.trim();
                if (!payId) {
                    utils.toast('Please enter your Razorpay Payment ID (e.g. pay_...)', 'error');
                    return;
                }
                if (!payId.startsWith('pay_')) {
                    if (!confirm('Valid Razorpay payment IDs typically begin with "pay_". Proceed anyway?')) {
                        return;
                    }
                }

                try {
                    verifyBtn.disabled = true;
                    verifyBtn.textContent = 'Verifying with Gateway...';

                    await api.payments.verify({
                        razorpayPaymentId: payId
                    });

                    localStorage.removeItem('pending_payment');
                    utils.closeModal();
                    utils.toast('Payment verified and subscription activated successfully!', 'success');
                    subscriptions.render();
                } catch (err) {
                    console.error('Verification failed:', err);
                    utils.toast(`Verification failed: ${err.message}`, 'error');
                    verifyBtn.disabled = false;
                    verifyBtn.textContent = 'Verify & Activate';
                }
            };
        }
    },

    async handleCancelPendingPayment(paymentId) {
        if (!confirm('Are you sure you want to cancel this pending payment? This will release the in-progress session so you can subscribe again.')) {
            return;
        }

        try {
            utils.toast('Cancelling pending payment...', 'info');
            await api.payments.cancel(paymentId);
            localStorage.removeItem('pending_payment');
            utils.closeModal();
            utils.toast('Pending payment cancelled successfully.', 'success');
            subscriptions.render();
        } catch (err) {
            console.error('Cancel failed:', err);
            localStorage.removeItem('pending_payment');
            utils.toast(`Failed to cancel: ${err.message}`, 'error');
            subscriptions.render();
        }
    },

    openCancelModal(subId, planName) {
        utils.showModal({
            title: `Cancel: ${planName}`,
            bodyHtml: `
                <form id="cancel-sub-form">
                    <p style="font-size: 0.9rem; color: var(--text-secondary); margin-bottom: 1rem;">
                        Are you sure you want to cancel this subscription? You will lose active borrowing privileges.
                    </p>
                    <div class="form-group">
                        <label class="form-label" for="cancel-reason">Reason for Cancellation</label>
                        <textarea id="cancel-reason" class="form-control" rows="3" placeholder="Optional reason for cancellation..."></textarea>
                    </div>
                </form>
            `,
            footerHtml: `
                <button class="btn btn-outline" onclick="utils.closeModal()">Keep Subscription</button>
                <button class="btn btn-danger" id="confirm-cancel-btn">Confirm Cancellation</button>
            `
        });

        const confirmBtn = document.getElementById('confirm-cancel-btn');
        if (confirmBtn) {
            confirmBtn.onclick = async () => {
                const reason = document.getElementById('cancel-reason').value.trim();
                try {
                    confirmBtn.disabled = true;
                    confirmBtn.textContent = 'Cancelling...';
                    await api.subscriptions.cancel(subId, reason);
                    utils.closeModal();
                    utils.toast('Subscription cancelled successfully.', 'success');
                    subscriptions.render();
                } catch (e) {
                    utils.toast(e.message || 'Failed to cancel subscription', 'error');
                } finally {
                    confirmBtn.disabled = false;
                    confirmBtn.textContent = 'Confirm Cancellation';
                }
            };
        }
    }
};
