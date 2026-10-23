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

            <!-- Active Subscription Banner -->
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

    async loadCurrentSubscription() {
        const banner = document.getElementById('current-subscription-banner');
        if (!banner) return;

        try {
            const sub = await api.subscriptions.getActive();
            if (sub && sub.isActive) {
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
            } else {
                banner.innerHTML = `
                    <div class="card" style="border-left: 4px solid var(--warning); background: #fffbeb;">
                        <div style="display: flex; align-items: center; gap: 1rem;">
                            <span style="font-size: 2rem;">⚠️</span>
                            <div>
                                <h3 style="font-size: 1.05rem; font-weight: 600; color: #92400e;">No Active Membership</h3>
                                <p style="font-size: 0.875rem; color: #b45309; margin-top: 0.2rem;">
                                    You need an active subscription plan to checkout and borrow books from the library. Please select one of the plans below.
                                </p>
                            </div>
                        </div>
                    </div>
                `;
            }
        } catch (e) {
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
        }
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
                        Clicking proceed will initiate payment processing. In test mode, you can complete verification immediately.
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

                    // If Razorpay order is initiated
                    if (paymentRes.razorpayOrderId && window.Razorpay) {
                        subscriptions.openRazorpayModal(paymentRes, planId);
                    } else {
                        // Direct/Test activation modal
                        subscriptions.openTestActivationModal(paymentRes);
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
                    // Verify payment
                    await api.payments.verify({
                        razorpayPaymentId: response.razorpay_payment_id
                    });

                    // Activate subscription
                    if (paymentData.paymentId) {
                        await api.subscriptions.activate(paymentData.paymentId, paymentData.paymentId);
                    }

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
            console.warn('Razorpay open failed, offering manual activation:', e);
            subscriptions.openTestActivationModal(paymentData);
        }
    },

    openTestActivationModal(paymentData) {
        utils.showModal({
            title: 'Payment Verification',
            bodyHtml: `
                <div style="padding: 1rem 0;">
                    <p style="font-size: 0.9rem; color: var(--text-secondary); margin-bottom: 1rem;">
                        Payment session #${paymentData.paymentId || 'N/A'} was generated successfully.
                    </p>
                    <div class="form-group">
                        <label class="form-label" for="manual-payment-id">Gateway Payment ID</label>
                        <input type="text" id="manual-payment-id" class="form-control" value="pay_test_${Date.now()}" required>
                    </div>
                </div>
            `,
            footerHtml: `
                <button class="btn btn-outline" onclick="utils.closeModal()">Close</button>
                <button class="btn btn-success" id="confirm-activate-btn">Verify & Activate Plan</button>
            `
        });

        const activateBtn = document.getElementById('confirm-activate-btn');
        if (activateBtn) {
            activateBtn.onclick = async () => {
                const payId = document.getElementById('manual-payment-id').value.trim();
                try {
                    activateBtn.disabled = true;
                    activateBtn.textContent = 'Activating...';
                    
                    // Verify payment
                    await api.payments.verify({
                        razorpayPaymentId: payId
                    });

                    // Activate
                    if (paymentData.paymentId) {
                        await api.subscriptions.activate(paymentData.paymentId, paymentData.paymentId);
                    }

                    utils.closeModal();
                    utils.toast('Subscription activated successfully!', 'success');
                    subscriptions.render();
                } catch (e) {
                    utils.toast(e.message || 'Activation failed', 'error');
                } finally {
                    activateBtn.disabled = false;
                    activateBtn.textContent = 'Verify & Activate Plan';
                }
            };
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
