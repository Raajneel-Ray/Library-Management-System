// Member Book Loans Management (Checkout, Checkin/Return, Renew)

const loans = {
    currentStatus: '',
    currentPage: 0,
    pageSize: 15,

    async renderMyLoans(params = {}) {
        const container = document.getElementById('app-content');
        if (!container) return;

        if (params.status !== undefined) this.currentStatus = params.status;
        if (params.page !== undefined) this.currentPage = parseInt(params.page);

        container.innerHTML = `
            <div class="page-header">
                <div>
                    <h1 class="page-title">My Book Loans</h1>
                    <p class="page-subtitle">Track your active loans, renewal history, and returns</p>
                </div>
                <a href="#books" class="btn btn-primary">+ Borrow New Book</a>
            </div>

            <!-- Status Tabs -->
            <div class="filter-bar" style="padding: 0.5rem 1rem;">
                <button class="btn btn-sm ${this.currentStatus === '' ? 'btn-primary' : 'btn-outline'}" onclick="loans.filterStatus('')">All Loans</button>
                <button class="btn btn-sm ${this.currentStatus === 'CHECKED_OUT' ? 'btn-primary' : 'btn-outline'}" onclick="loans.filterStatus('CHECKED_OUT')">Checked Out</button>
                <button class="btn btn-sm ${this.currentStatus === 'OVERDUE' ? 'btn-primary' : 'btn-outline'}" onclick="loans.filterStatus('OVERDUE')">Overdue</button>
                <button class="btn btn-sm ${this.currentStatus === 'RETURNED' ? 'btn-primary' : 'btn-outline'}" onclick="loans.filterStatus('RETURNED')">Returned</button>
                <button class="btn btn-sm ${this.currentStatus === 'LOST' ? 'btn-primary' : 'btn-outline'}" onclick="loans.filterStatus('LOST')">Lost</button>
                <button class="btn btn-sm ${this.currentStatus === 'DAMAGED' ? 'btn-primary' : 'btn-outline'}" onclick="loans.filterStatus('DAMAGED')">Damaged</button>
            </div>

            <!-- Loans Table Container -->
            <div id="loans-table-container">
                <div class="spinner" style="margin: 3rem auto;"></div>
            </div>

            <!-- Pagination -->
            <div id="loans-pagination" class="pagination"></div>

            <!-- Fines Quick Banner -->
            <div class="card mt-4" style="background: #f8fafc; border: 1px solid var(--border);">
                <div style="display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 1rem;">
                    <div style="display: flex; align-items: center; gap: 0.85rem;">
                        <span style="font-size: 2rem;">💸</span>
                        <div>
                            <strong style="font-size: 1rem; color: var(--text-primary);">Library Fine & Penalty Policy</strong>
                            <p class="text-sm text-muted" style="margin-top: 0.2rem;">
                                Overdue, damaged, or lost books are tracked in the Fine Service. You can review and settle fees online anytime.
                            </p>
                        </div>
                    </div>
                    <a href="#fines" class="btn btn-outline btn-sm">Go to My Fines &raquo;</a>
                </div>
            </div>
        `;

        this.loadLoans();
    },

    filterStatus(status) {
        this.currentStatus = status;
        this.currentPage = 0;
        this.renderMyLoans();
    },

    async loadLoans() {
        const tableContainer = document.getElementById('loans-table-container');
        const paginationContainer = document.getElementById('loans-pagination');
        if (!tableContainer) return;

        tableContainer.innerHTML = '<div class="spinner" style="margin: 3rem auto;"></div>';

        try {
            const pageData = await api.loans.getMyLoans({
                status: this.currentStatus || undefined,
                page: this.currentPage,
                size: this.pageSize
            });

            const loanItems = pageData.content || [];

            if (loanItems.length === 0) {
                tableContainer.innerHTML = `
                    <div class="card text-center" style="padding: 3rem 1.5rem;">
                        <span style="font-size: 2.5rem;">📑</span>
                        <h3 class="mt-2">No loans found</h3>
                        <p class="text-muted text-sm mt-1">
                            ${this.currentStatus ? `No loans with status "${this.currentStatus}"` : "You haven't borrowed any books yet."}
                        </p>
                        <a href="#books" class="btn btn-primary btn-sm mt-4">Browse Available Books</a>
                    </div>
                `;
                if (paginationContainer) paginationContainer.innerHTML = '';
                return;
            }

            tableContainer.innerHTML = `
                <div class="table-responsive">
                    <table class="table">
                        <thead>
                            <tr>
                                <th>Loan ID</th>
                                <th>Book Details</th>
                                <th>Checked Out</th>
                                <th>Due Date</th>
                                <th>Return Date</th>
                                <th>Renewals</th>
                                <th>Status</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            ${loanItems.map(loan => {
                                const isActive = loan.status === 'CHECKED_OUT' || loan.status === 'OVERDUE';
                                const canRenew = isActive && !loan.isOverdue && (loan.renewalCount < (loan.maxRenewals || 2));

                                return `
                                    <tr>
                                        <td>#${loan.id}</td>
                                        <td>
                                            <strong>${utils.escapeHtml(loan.bookTitle || 'Book #' + loan.bookId)}</strong>
                                            <div class="text-muted text-sm">by ${utils.escapeHtml(loan.bookAuthor || 'Unknown')}</div>
                                            <div class="text-muted text-sm">ISBN: ${utils.escapeHtml(loan.bookIsbn || '-')}</div>
                                        </td>
                                        <td>${utils.formatDate(loan.checkoutDate)}</td>
                                        <td>
                                            <strong>${utils.formatDate(loan.dueDate)}</strong>
                                            ${loan.isOverdue ? `<div class="badge badge-danger text-sm mt-1">${loan.overdueDays || 0} days overdue</div>` : ''}
                                        </td>
                                        <td>${utils.formatDate(loan.returnDate)}</td>
                                        <td>
                                            <span class="badge ${loan.renewalCount > 0 ? 'badge-info' : 'badge-secondary'}">
                                                ${loan.renewalCount || 0} / ${loan.maxRenewals || 2}
                                            </span>
                                        </td>
                                        <td>${utils.renderLoanStatusBadge(loan.status)}</td>
                                        <td>
                                            <div style="display: flex; gap: 0.35rem; flex-wrap: wrap;">
                                                ${isActive ? `
                                                    <button class="btn btn-success btn-sm" onclick="loans.openCheckinModal(${loan.id}, '${utils.escapeHtml(loan.bookTitle ? loan.bookTitle.replace(/'/g, "\\'") : '')}')">
                                                        Return
                                                    </button>
                                                    <button class="btn btn-outline btn-sm" ${canRenew ? '' : 'disabled title="Renewal limit reached or overdue"'} onclick="loans.openRenewModal(${loan.id}, '${utils.escapeHtml(loan.bookTitle ? loan.bookTitle.replace(/'/g, "\\'") : '')}')">
                                                        Renew
                                                    </button>
                                                ` : `
                                                    <span class="text-muted text-sm">Closed</span>
                                                `}
                                                ${loan.isOverdue || loan.status === 'OVERDUE' || loan.status === 'DAMAGED' || loan.status === 'LOST' ? `
                                                    <a href="#fines" class="btn btn-outline btn-sm" style="color: var(--danger); border-color: var(--danger);" title="Check associated fines">💸 Fines</a>
                                                ` : ''}
                                            </div>
                                        </td>
                                    </tr>
                                `;
                            }).join('')}
                        </tbody>
                    </table>
                </div>
            `;

            // Pagination
            if (paginationContainer && pageData.totalPages > 1) {
                let pagHtml = `
                    <button class="page-btn" ${pageData.first ? 'disabled' : ''} onclick="loans.changePage(${pageData.pageNumber - 1})">&laquo; Prev</button>
                `;
                for (let i = 0; i < pageData.totalPages; i++) {
                    if (i === 0 || i === pageData.totalPages - 1 || Math.abs(i - pageData.pageNumber) <= 2) {
                        pagHtml += `
                            <button class="page-btn ${i === pageData.pageNumber ? 'active' : ''}" onclick="loans.changePage(${i})">${i + 1}</button>
                        `;
                    } else if (i === 1 || i === pageData.totalPages - 2) {
                        pagHtml += `<span style="padding: 0 0.25rem;">...</span>`;
                    }
                }
                pagHtml += `
                    <button class="page-btn" ${pageData.last ? 'disabled' : ''} onclick="loans.changePage(${pageData.pageNumber + 1})">Next &raquo;</button>
                `;
                paginationContainer.innerHTML = pagHtml;
            } else if (paginationContainer) {
                paginationContainer.innerHTML = '';
            }

        } catch (e) {
            console.error('Failed to load loans:', e);
            tableContainer.innerHTML = `<div class="card text-center" style="padding: 2rem;"><p class="text-danger">Failed to load loans. ${utils.escapeHtml(e.message)}</p></div>`;
        }
    },

    changePage(newPage) {
        this.currentPage = newPage;
        this.loadLoans();
    },

    // Checkin / Return Book Modal (calls POST /api/book-loans/checkin)
    openCheckinModal(loanId, bookTitle) {
        utils.showModal({
            title: `Return Book: #${loanId}`,
            bodyHtml: `
                <form id="checkin-form">
                    <p style="font-size: 0.9rem; color: var(--text-secondary); margin-bottom: 1rem;">
                        Returning: <strong>${utils.escapeHtml(bookTitle || 'Book Loan #' + loanId)}</strong>
                    </p>
                    <div class="form-group">
                        <label class="form-label" for="return-condition">Book Condition</label>
                        <select id="return-condition" class="form-control">
                            <option value="RETURNED">Good / Normal Condition (RETURNED)</option>
                            <option value="DAMAGED">Damaged (Requires Repair / Penalty)</option>
                            <option value="LOST">Lost (Requires Replacement)</option>
                        </select>
                    </div>
                    <div class="form-group">
                        <label class="form-label" for="return-notes">Return Notes</label>
                        <textarea id="return-notes" class="form-control" rows="2" placeholder="Optional notes on book condition..."></textarea>
                    </div>
                </form>
            `,
            footerHtml: `
                <button class="btn btn-outline" onclick="utils.closeModal()">Cancel</button>
                <button class="btn btn-success" id="confirm-checkin-btn">Confirm Return</button>
            `
        });

        const confirmBtn = document.getElementById('confirm-checkin-btn');
        if (confirmBtn) {
            confirmBtn.onclick = async () => {
                const condition = document.getElementById('return-condition').value;
                const notes = document.getElementById('return-notes').value.trim();

                try {
                    confirmBtn.disabled = true;
                    confirmBtn.textContent = 'Processing...';
                    await api.loans.checkin({
                        bookLoanId: loanId,
                        condition: condition,
                        notes: notes || undefined
                    });
                    utils.closeModal();
                    utils.toast('Book returned successfully! Inventory has been updated.', 'success');
                    loans.loadLoans();
                } catch (err) {
                    utils.toast(err.message || 'Failed to return book', 'error');
                } finally {
                    confirmBtn.disabled = false;
                    confirmBtn.textContent = 'Confirm Return';
                }
            };
        }
    },

    // Renew Modal (calls POST /api/book-loans/renew)
    openRenewModal(loanId, bookTitle) {
        utils.showModal({
            title: `Renew Checkout: #${loanId}`,
            bodyHtml: `
                <form id="renew-form">
                    <p style="font-size: 0.9rem; color: var(--text-secondary); margin-bottom: 1rem;">
                        Renewing: <strong>${utils.escapeHtml(bookTitle || 'Book Loan #' + loanId)}</strong>
                    </p>
                    <div class="form-group">
                        <label class="form-label" for="renew-extension-days">Extension Days</label>
                        <input type="number" id="renew-extension-days" class="form-control" value="14" min="1" max="60" required>
                        <small class="text-muted">Number of days to extend the current due date.</small>
                    </div>
                    <div class="form-group">
                        <label class="form-label" for="renew-notes">Notes</label>
                        <textarea id="renew-notes" class="form-control" rows="2" placeholder="e.g. Requested additional time to finish reading"></textarea>
                    </div>
                </form>
            `,
            footerHtml: `
                <button class="btn btn-outline" onclick="utils.closeModal()">Cancel</button>
                <button class="btn btn-primary" id="confirm-renew-btn">Confirm Renewal</button>
            `
        });

        const confirmBtn = document.getElementById('confirm-renew-btn');
        if (confirmBtn) {
            confirmBtn.onclick = async () => {
                const days = document.getElementById('renew-extension-days').value;
                const notes = document.getElementById('renew-notes').value.trim();

                try {
                    confirmBtn.disabled = true;
                    confirmBtn.textContent = 'Renewing...';
                    const renewed = await api.loans.renew({
                        bookLoanId: loanId,
                        extensionDay: parseInt(days) || 14,
                        notes: notes || undefined
                    });
                    utils.closeModal();
                    utils.toast(`Loan renewed successfully! New due date: ${utils.formatDate(renewed.dueDate)}`, 'success');
                    loans.loadLoans();
                } catch (err) {
                    utils.toast(err.message || 'Renewal failed. Loan may have reached maximum renewals or is overdue.', 'error');
                } finally {
                    confirmBtn.disabled = false;
                    confirmBtn.textContent = 'Confirm Renewal';
                }
            };
        }
    }
};
