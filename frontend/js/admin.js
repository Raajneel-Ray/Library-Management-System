// Administrator Portal Suite (Books, Loans, Genres, Plans, Subscriptions, Users, Payments)

const admin = {
    // -------------------------------------------------------------
    // 1. Manage Books
    // -------------------------------------------------------------
    async renderBooks() {
        const container = document.getElementById('app-content');
        if (!container) return;

        container.innerHTML = `
            <div class="page-header">
                <div>
                    <h1 class="page-title">Book Inventory Management</h1>
                    <p class="page-subtitle">Add, edit, bulk import, and manage library titles</p>
                </div>
                <div style="display: flex; gap: 0.5rem;">
                    <button class="btn btn-outline" onclick="admin.openBulkBookModal()">📥 Bulk Import</button>
                    <button class="btn btn-primary" onclick="admin.openCreateBookModal()">+ Add New Book</button>
                </div>
            </div>

            <!-- Search Bar -->
            <div class="filter-bar">
                <input type="text" id="admin-book-search" class="form-control" style="flex: 1 1 300px;" placeholder="Search books by title, author, or ISBN...">
                <button class="btn btn-primary" onclick="admin.searchBooks()">Search</button>
            </div>

            <div id="admin-books-table-container">
                <div class="spinner" style="margin: 3rem auto;"></div>
            </div>
            <div id="admin-books-pagination" class="pagination"></div>
        `;

        this.loadBooksTable();
    },

    async loadBooksTable(page = 0, searchTerm = '') {
        const tableContainer = document.getElementById('admin-books-table-container');
        if (!tableContainer) return;

        tableContainer.innerHTML = '<div class="spinner" style="margin: 3rem auto;"></div>';

        try {
            const pageData = searchTerm 
                ? await api.books.advancedSearch({ searchTerm, page, size: 15 })
                : await api.books.search({ page, size: 15, activeOnly: false });

            const bookList = pageData.content || [];

            if (bookList.length === 0) {
                tableContainer.innerHTML = '<div class="card text-center" style="padding: 2.5rem;"><p class="text-muted">No books found.</p></div>';
                return;
            }

            tableContainer.innerHTML = `
                <div class="table-responsive">
                    <table class="table">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Title & Author</th>
                                <th>ISBN</th>
                                <th>Genre</th>
                                <th>Available / Total</th>
                                <th>Status</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            ${bookList.map(b => `
                                <tr>
                                    <td>#${b.id}</td>
                                    <td>
                                        <strong>${utils.escapeHtml(b.title)}</strong>
                                        <div class="text-muted text-sm">${utils.escapeHtml(b.author)}</div>
                                    </td>
                                    <td><code>${utils.escapeHtml(b.isbn)}</code></td>
                                    <td>${utils.escapeHtml(b.genreName || '-')}</td>
                                    <td>
                                        <strong>${b.availableCopies}</strong> / ${b.totalCopies}
                                    </td>
                                    <td>${utils.renderActiveBadge(b.active)}</td>
                                    <td>
                                        <div style="display: flex; gap: 0.35rem; flex-wrap: wrap;">
                                            <button class="btn btn-outline btn-sm" onclick="admin.openEditBookModal(${b.id})">Edit</button>
                                            <button class="btn btn-outline btn-sm" style="color: var(--warning); border-color: var(--warning);" onclick="admin.confirmSoftDeleteBook(${b.id}, '${utils.escapeHtml(b.title.replace(/'/g, "\\'"))}')">Deactivate</button>
                                            <button class="btn btn-danger btn-sm" onclick="admin.confirmHardDeleteBook(${b.id}, '${utils.escapeHtml(b.title.replace(/'/g, "\\'"))}')">Delete</button>
                                        </div>
                                    </td>
                                </tr>
                            `).join('')}
                        </tbody>
                    </table>
                </div>
            `;
        } catch (e) {
            tableContainer.innerHTML = `<div class="card text-center" style="padding: 2rem;"><p class="text-danger">Failed to load books: ${utils.escapeHtml(e.message)}</p></div>`;
        }
    },

    searchBooks() {
        const input = document.getElementById('admin-book-search');
        const term = input ? input.value.trim() : '';
        this.loadBooksTable(0, term);
    },

    async openCreateBookModal() {
        let genres = [];
        try {
            genres = await api.genres.getAll();
        } catch (e) {}

        utils.showModal({
            title: 'Add New Book',
            bodyHtml: `
                <form id="create-book-form">
                    <div class="form-row">
                        <div class="form-group">
                            <label class="form-label" for="book-title">Title *</label>
                            <input type="text" id="book-title" class="form-control" required>
                        </div>
                        <div class="form-group">
                            <label class="form-label" for="book-author">Author *</label>
                            <input type="text" id="book-author" class="form-control" required>
                        </div>
                    </div>
                    <div class="form-row">
                        <div class="form-group">
                            <label class="form-label" for="book-isbn">ISBN *</label>
                            <input type="text" id="book-isbn" class="form-control" placeholder="978-..." required>
                        </div>
                        <div class="form-group">
                            <label class="form-label" for="book-genre">Genre *</label>
                            <select id="book-genre" class="form-control" required>
                                <option value="">Select Genre</option>
                                ${genres.map(g => `<option value="${g.id}">${utils.escapeHtml(g.name)}</option>`).join('')}
                            </select>
                        </div>
                    </div>
                    <div class="form-row">
                        <div class="form-group">
                            <label class="form-label" for="book-total">Total Copies *</label>
                            <input type="number" id="book-total" class="form-control" min="0" value="5" required>
                        </div>
                        <div class="form-group">
                            <label class="form-label" for="book-avail">Available Copies *</label>
                            <input type="number" id="book-avail" class="form-control" min="0" value="5" required>
                        </div>
                        <div class="form-group">
                            <label class="form-label" for="book-price">Price</label>
                            <input type="number" id="book-price" class="form-control" step="0.01" value="299.00">
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="form-label" for="book-cover">Cover Image URL</label>
                        <input type="url" id="book-cover" class="form-control" placeholder="https://...">
                    </div>
                    <div class="form-group">
                        <label class="form-label" for="book-desc">Description</label>
                        <textarea id="book-desc" class="form-control" rows="3"></textarea>
                    </div>
                </form>
            `,
            footerHtml: `
                <button class="btn btn-outline" onclick="utils.closeModal()">Cancel</button>
                <button class="btn btn-primary" id="save-book-btn">Create Book</button>
            `
        });

        const saveBtn = document.getElementById('save-book-btn');
        if (saveBtn) {
            saveBtn.onclick = async () => {
                const title = document.getElementById('book-title').value.trim();
                const author = document.getElementById('book-author').value.trim();
                const isbn = document.getElementById('book-isbn').value.trim();
                const genreId = document.getElementById('book-genre').value;
                const totalCopies = parseInt(document.getElementById('book-total').value);
                const availableCopies = parseInt(document.getElementById('book-avail').value);
                const price = parseFloat(document.getElementById('book-price').value) || null;
                const coverImageUrl = document.getElementById('book-cover').value.trim() || null;
                const description = document.getElementById('book-desc').value.trim() || null;

                if (!title || !author || !isbn || !genreId || isNaN(totalCopies)) {
                    utils.toast('Please fill out all mandatory fields.', 'warning');
                    return;
                }

                try {
                    saveBtn.disabled = true;
                    saveBtn.textContent = 'Saving...';
                    await api.adminBooks.create({
                        title, author, isbn, genreId: parseInt(genreId),
                        totalCopies, availableCopies, price, coverImageUrl, description,
                        active: true
                    });
                    utils.closeModal();
                    utils.toast('Book created successfully!', 'success');
                    admin.loadBooksTable();
                } catch (e) {
                    utils.toast(e.message || 'Failed to create book', 'error');
                } finally {
                    saveBtn.disabled = false;
                    saveBtn.textContent = 'Create Book';
                }
            };
        }
    },

    async openEditBookModal(bookId) {
        let book, genres = [];
        try {
            [book, genres] = await Promise.all([
                api.books.getById(bookId),
                api.genres.getAll()
            ]);
        } catch (e) {
            utils.toast('Failed to load book data: ' + e.message, 'error');
            return;
        }

        utils.showModal({
            title: `Edit Book #${bookId}`,
            bodyHtml: `
                <form id="edit-book-form">
                    <div class="form-row">
                        <div class="form-group">
                            <label class="form-label" for="edit-title">Title</label>
                            <input type="text" id="edit-title" class="form-control" value="${utils.escapeHtml(book.title)}" required>
                        </div>
                        <div class="form-group">
                            <label class="form-label" for="edit-author">Author</label>
                            <input type="text" id="edit-author" class="form-control" value="${utils.escapeHtml(book.author)}" required>
                        </div>
                    </div>
                    <div class="form-row">
                        <div class="form-group">
                            <label class="form-label" for="edit-isbn">ISBN</label>
                            <input type="text" id="edit-isbn" class="form-control" value="${utils.escapeHtml(book.isbn)}" required>
                        </div>
                        <div class="form-group">
                            <label class="form-label" for="edit-genre">Genre</label>
                            <select id="edit-genre" class="form-control">
                                ${genres.map(g => `<option value="${g.id}" ${g.id === book.genreId ? 'selected' : ''}>${utils.escapeHtml(g.name)}</option>`).join('')}
                            </select>
                        </div>
                    </div>
                    <div class="form-row">
                        <div class="form-group">
                            <label class="form-label" for="edit-total">Total Copies</label>
                            <input type="number" id="edit-total" class="form-control" value="${book.totalCopies || 0}" min="0" required>
                        </div>
                        <div class="form-group">
                            <label class="form-label" for="edit-avail">Available Copies</label>
                            <input type="number" id="edit-avail" class="form-control" value="${book.availableCopies || 0}" min="0" required>
                        </div>
                        <div class="form-group">
                            <label class="form-label" for="edit-active">Status</label>
                            <select id="edit-active" class="form-control">
                                <option value="true" ${book.active ? 'selected' : ''}>Active</option>
                                <option value="false" ${!book.active ? 'selected' : ''}>Inactive</option>
                            </select>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="form-label" for="edit-cover">Cover Image URL</label>
                        <input type="url" id="edit-cover" class="form-control" value="${utils.escapeHtml(book.coverImageUrl || '')}">
                    </div>
                    <div class="form-group">
                        <label class="form-label" for="edit-desc">Description</label>
                        <textarea id="edit-desc" class="form-control" rows="3">${utils.escapeHtml(book.description || '')}</textarea>
                    </div>
                </form>
            `,
            footerHtml: `
                <button class="btn btn-outline" onclick="utils.closeModal()">Cancel</button>
                <button class="btn btn-primary" id="update-book-btn">Save Changes</button>
            `
        });

        const updateBtn = document.getElementById('update-book-btn');
        if (updateBtn) {
            updateBtn.onclick = async () => {
                const updatedData = {
                    title: document.getElementById('edit-title').value.trim(),
                    author: document.getElementById('edit-author').value.trim(),
                    isbn: document.getElementById('edit-isbn').value.trim(),
                    genreId: parseInt(document.getElementById('edit-genre').value),
                    totalCopies: parseInt(document.getElementById('edit-total').value),
                    availableCopies: parseInt(document.getElementById('edit-avail').value),
                    active: document.getElementById('edit-active').value === 'true',
                    coverImageUrl: document.getElementById('edit-cover').value.trim() || null,
                    description: document.getElementById('edit-desc').value.trim() || null
                };

                try {
                    updateBtn.disabled = true;
                    updateBtn.textContent = 'Saving...';
                    await api.adminBooks.update(bookId, updatedData);
                    utils.closeModal();
                    utils.toast('Book updated successfully!', 'success');
                    admin.loadBooksTable();
                } catch (e) {
                    utils.toast(e.message || 'Failed to update book', 'error');
                } finally {
                    updateBtn.disabled = false;
                    updateBtn.textContent = 'Save Changes';
                }
            };
        }
    },

    openBulkBookModal() {
        utils.showModal({
            title: 'Bulk Import Books (JSON)',
            bodyHtml: `
                <p style="font-size: 0.85rem; color: var(--text-secondary); margin-bottom: 0.75rem;">
                    Paste a JSON array of BookDTO objects containing: <code>isbn, title, author, genreId, totalCopies, availableCopies</code>.
                </p>
                <textarea id="bulk-json-input" class="form-control" rows="10" placeholder='[
  {
    "isbn": "9780134685991",
    "title": "Effective Java",
    "author": "Joshua Bloch",
    "genreId": 1,
    "totalCopies": 10,
    "availableCopies": 10
  }
]'></textarea>
            `,
            footerHtml: `
                <button class="btn btn-outline" onclick="utils.closeModal()">Cancel</button>
                <button class="btn btn-primary" id="confirm-bulk-btn">Import Books</button>
            `
        });

        const confirmBtn = document.getElementById('confirm-bulk-btn');
        if (confirmBtn) {
            confirmBtn.onclick = async () => {
                const raw = document.getElementById('bulk-json-input').value.trim();
                let parsed;
                try {
                    parsed = JSON.parse(raw);
                    if (!Array.isArray(parsed)) throw new Error('Must be a JSON array of books');
                } catch (err) {
                    utils.toast('Invalid JSON format: ' + err.message, 'error');
                    return;
                }

                try {
                    confirmBtn.disabled = true;
                    confirmBtn.textContent = 'Importing...';
                    const created = await api.adminBooks.createBulk(parsed);
                    utils.closeModal();
                    utils.toast(`Successfully imported ${created.length || parsed.length} books!`, 'success');
                    admin.loadBooksTable();
                } catch (e) {
                    utils.toast(e.message || 'Bulk import failed', 'error');
                } finally {
                    confirmBtn.disabled = false;
                    confirmBtn.textContent = 'Import Books';
                }
            };
        }
    },

    confirmSoftDeleteBook(id, title) {
        if (confirm(`Deactivate book "${title}"? It will no longer appear in search.`)) {
            api.books.deleteSoft(id)
                .then(() => {
                    utils.toast('Book deactivated', 'info');
                    admin.loadBooksTable();
                })
                .catch(e => utils.toast(e.message || 'Deactivation failed', 'error'));
        }
    },

    confirmHardDeleteBook(id, title) {
        if (confirm(`PERMANENT DELETE: Are you sure you want to permanently remove "${title}"? This cannot be undone.`)) {
            api.adminBooks.deletePermanent(id)
                .then(() => {
                    utils.toast('Book permanently deleted from database.', 'success');
                    admin.loadBooksTable();
                })
                .catch(e => utils.toast(e.message || 'Permanent delete failed', 'error'));
        }
    },

    // -------------------------------------------------------------
    // 2. Manage Loans (Search, Filter, Overdue Job, Staff Checkout)
    // -------------------------------------------------------------
    async renderLoans(params = {}) {
        const container = document.getElementById('app-content');
        if (!container) return;

        container.innerHTML = `
            <div class="page-header">
                <div>
                    <h1 class="page-title">Global Book Loans</h1>
                    <p class="page-subtitle">Monitor loans across all members, update overdues, and perform staff checkouts</p>
                </div>
                <div style="display: flex; gap: 0.5rem;">
                    <button class="btn btn-warning" id="admin-overdue-job-btn" onclick="admin.runOverdueJob()">⚡ Update Overdue Loans</button>
                    <button class="btn btn-primary" onclick="admin.openStaffCheckoutModal()">+ Staff Checkout</button>
                </div>
            </div>

            <!-- Search Filters -->
            <div class="filter-bar">
                <input type="number" id="loan-user-filter" class="form-control" style="flex: 1 1 120px;" placeholder="User ID">
                <input type="number" id="loan-book-filter" class="form-control" style="flex: 1 1 120px;" placeholder="Book ID">
                <select id="loan-status-filter" class="form-control" style="flex: 1 1 150px;">
                    <option value="">All Statuses</option>
                    <option value="CHECKED_OUT">Checked Out</option>
                    <option value="OVERDUE">Overdue</option>
                    <option value="RETURNED">Returned</option>
                    <option value="LOST">Lost</option>
                    <option value="DAMAGED">Damaged</option>
                </select>
                <label style="display: flex; align-items: center; gap: 0.35rem; font-size: 0.875rem;">
                    <input type="checkbox" id="loan-overdue-only"> Overdue Only
                </label>
                <button class="btn btn-primary" onclick="admin.loadLoansTable()">Filter</button>
            </div>

            <div id="admin-loans-table-container">
                <div class="spinner" style="margin: 3rem auto;"></div>
            </div>
        `;

        this.loadLoansTable();
    },

    async loadLoansTable() {
        const tableContainer = document.getElementById('admin-loans-table-container');
        if (!tableContainer) return;

        tableContainer.innerHTML = '<div class="spinner" style="margin: 3rem auto;"></div>';

        const userId = document.getElementById('loan-user-filter')?.value;
        const bookId = document.getElementById('loan-book-filter')?.value;
        const status = document.getElementById('loan-status-filter')?.value;
        const overdueOnly = document.getElementById('loan-overdue-only')?.checked;

        try {
            const pageData = await api.loans.search({
                userId: userId ? parseInt(userId) : null,
                bookId: bookId ? parseInt(bookId) : null,
                status: status || null,
                overdueOnly: overdueOnly || false,
                page: 0,
                size: 25,
                sortBy: 'createdAt',
                sortDirection: 'DESC'
            });

            const loanList = pageData.content || [];

            if (loanList.length === 0) {
                tableContainer.innerHTML = '<div class="card text-center" style="padding: 2.5rem;"><p class="text-muted">No loans match current criteria.</p></div>';
                return;
            }

            tableContainer.innerHTML = `
                <div class="table-responsive">
                    <table class="table">
                        <thead>
                            <tr>
                                <th>Loan ID</th>
                                <th>Borrower</th>
                                <th>Book Title</th>
                                <th>Checked Out</th>
                                <th>Due Date</th>
                                <th>Status</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            ${loanList.map(loan => `
                                <tr>
                                    <td>#${loan.id}</td>
                                    <td>
                                        <strong>${utils.escapeHtml(loan.userName || 'User #' + loan.userId)}</strong>
                                        <div class="text-muted text-sm">${utils.escapeHtml(loan.userEmail || '')}</div>
                                    </td>
                                    <td>
                                        <strong>${utils.escapeHtml(loan.bookTitle || 'Book #' + loan.bookId)}</strong>
                                        <div class="text-muted text-sm">${utils.escapeHtml(loan.bookAuthor || '')}</div>
                                    </td>
                                    <td>${utils.formatDate(loan.checkoutDate)}</td>
                                    <td>
                                        <strong>${utils.formatDate(loan.dueDate)}</strong>
                                        ${loan.isOverdue ? `<div class="badge badge-danger text-sm mt-1">${loan.overdueDays || 0}d Overdue</div>` : ''}
                                    </td>
                                    <td>${utils.renderLoanStatusBadge(loan.status)}</td>
                                    <td>
                                        <div style="display: flex; gap: 0.35rem; flex-wrap: wrap;">
                                            ${loan.status === 'CHECKED_OUT' || loan.status === 'OVERDUE' ? `
                                                <button class="btn btn-success btn-sm" onclick="loans.openCheckinModal(${loan.id}, '${utils.escapeHtml(loan.bookTitle ? loan.bookTitle.replace(/'/g, "\\'") : '')}')">Check In</button>
                                                <button class="btn btn-outline btn-sm" onclick="loans.openRenewModal(${loan.id}, '${utils.escapeHtml(loan.bookTitle ? loan.bookTitle.replace(/'/g, "\\'") : '')}')">Renew</button>
                                            ` : ''}
                                            <button class="btn btn-outline btn-sm" title="Assess Fine or Penalty" onclick="admin.openCreateFineModal(${loan.id})">⚖️ Fine</button>
                                        </div>
                                    </td>
                                </tr>
                            `).join('')}
                        </tbody>
                    </table>
                </div>
            `;
        } catch (e) {
            tableContainer.innerHTML = `<div class="card text-center" style="padding: 2rem;"><p class="text-danger">Failed to load loans: ${utils.escapeHtml(e.message)}</p></div>`;
        }
    },

    async runOverdueJob() {
        const btn = document.getElementById('admin-overdue-job-btn');
        try {
            if (btn) {
                btn.disabled = true;
                btn.textContent = 'Updating...';
            }
            const res = await api.loans.updateOverdueAdmin();
            utils.toast(res.message || 'Overdue loans scanned and updated successfully!', 'success');
            admin.loadLoansTable();
        } catch (e) {
            utils.toast(e.message || 'Overdue job failed', 'error');
        } finally {
            if (btn) {
                btn.disabled = false;
                btn.textContent = '⚡ Update Overdue Loans';
            }
        }
    },

    openStaffCheckoutModal() {
        utils.showModal({
            title: 'Staff Checkout For User',
            bodyHtml: `
                <form id="staff-checkout-form">
                    <div class="form-group">
                        <label class="form-label" for="staff-user-id">Target User ID *</label>
                        <input type="number" id="staff-user-id" class="form-control" placeholder="e.g. 1" required>
                    </div>
                    <div class="form-group">
                        <label class="form-label" for="staff-book-id">Book ID *</label>
                        <input type="number" id="staff-book-id" class="form-control" placeholder="e.g. 1" required>
                    </div>
                    <div class="form-group">
                        <label class="form-label" for="staff-days">Checkout Days</label>
                        <input type="number" id="staff-days" class="form-control" placeholder="Leave blank for plan default">
                    </div>
                    <div class="form-group">
                        <label class="form-label" for="staff-notes">Librarian Notes</label>
                        <textarea id="staff-notes" class="form-control" rows="2" placeholder="Manual checkout by staff..."></textarea>
                    </div>
                </form>
            `,
            footerHtml: `
                <button class="btn btn-outline" onclick="utils.closeModal()">Cancel</button>
                <button class="btn btn-primary" id="confirm-staff-checkout-btn">Checkout</button>
            `
        });

        const confirmBtn = document.getElementById('confirm-staff-checkout-btn');
        if (confirmBtn) {
            confirmBtn.onclick = async () => {
                const userId = document.getElementById('staff-user-id').value;
                const bookId = document.getElementById('staff-book-id').value;
                const days = document.getElementById('staff-days').value;
                const notes = document.getElementById('staff-notes').value.trim();

                if (!userId || !bookId) {
                    utils.toast('User ID and Book ID are required', 'warning');
                    return;
                }

                try {
                    confirmBtn.disabled = true;
                    confirmBtn.textContent = 'Processing...';
                    await api.loans.checkoutForUser(parseInt(userId), {
                        bookId: parseInt(bookId),
                        checkoutDays: days ? parseInt(days) : null,
                        notes: notes || undefined
                    });
                    utils.closeModal();
                    utils.toast('Book loan created successfully for user!', 'success');
                    admin.loadLoansTable();
                } catch (e) {
                    utils.toast(e.message || 'Staff checkout failed', 'error');
                } finally {
                    confirmBtn.disabled = false;
                    confirmBtn.textContent = 'Checkout';
                }
            };
        }
    },

    // -------------------------------------------------------------
    // 3. Manage Genres
    // -------------------------------------------------------------
    async renderGenres() {
        const container = document.getElementById('app-content');
        if (!container) return;

        container.innerHTML = `
            <div class="page-header">
                <div>
                    <h1 class="page-title">Genres Management</h1>
                    <p class="page-subtitle">Organize books into genres, hierarchical categories, and classifications</p>
                </div>
                <button class="btn btn-primary" onclick="admin.openCreateGenreModal()">+ Add New Genre</button>
            </div>

            <div id="admin-genres-container">
                <div class="spinner" style="margin: 3rem auto;"></div>
            </div>
        `;

        this.loadGenres();
    },

    async loadGenres() {
        const container = document.getElementById('admin-genres-container');
        if (!container) return;

        try {
            const genresList = await api.genres.getAll();

            if (genresList.length === 0) {
                container.innerHTML = '<div class="card text-center" style="padding: 2.5rem;"><p class="text-muted">No genres configured.</p></div>';
                return;
            }

            container.innerHTML = `
                <div class="table-responsive">
                    <table class="table">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Genre Name</th>
                                <th>Code</th>
                                <th>Description</th>
                                <th>Display Order</th>
                                <th>Status</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            ${genresList.map(g => `
                                <tr>
                                    <td>#${g.id}</td>
                                    <td><strong>${utils.escapeHtml(g.name)}</strong></td>
                                    <td><code>${utils.escapeHtml(g.code || '-')}</code></td>
                                    <td>${utils.escapeHtml(g.description || '-')}</td>
                                    <td>${g.displayOrder || 0}</td>
                                    <td>${utils.renderActiveBadge(g.active !== false)}</td>
                                    <td>
                                        <div style="display: flex; gap: 0.35rem;">
                                            <button class="btn btn-outline btn-sm" onclick="admin.openEditGenreModal(${g.id}, '${utils.escapeHtml(g.name.replace(/'/g, "\\'"))}', '${utils.escapeHtml((g.code || '').replace(/'/g, "\\'"))}', '${utils.escapeHtml((g.description || '').replace(/'/g, "\\'"))}', ${g.displayOrder || 0})">Edit</button>
                                            <button class="btn btn-outline btn-sm" style="color: var(--warning); border-color: var(--warning);" onclick="admin.softDeleteGenre(${g.id})">Deactivate</button>
                                            <button class="btn btn-danger btn-sm" onclick="admin.hardDeleteGenre(${g.id})">Delete</button>
                                        </div>
                                    </td>
                                </tr>
                            `).join('')}
                        </tbody>
                    </table>
                </div>
            `;
        } catch (e) {
            container.innerHTML = `<div class="card text-center"><p class="text-danger">Failed to load genres: ${utils.escapeHtml(e.message)}</p></div>`;
        }
    },

    openCreateGenreModal() {
        utils.showModal({
            title: 'Create Genre',
            bodyHtml: `
                <form id="create-genre-form">
                    <div class="form-group">
                        <label class="form-label" for="genre-name">Genre Name *</label>
                        <input type="text" id="genre-name" class="form-control" placeholder="e.g. Science Fiction" required>
                    </div>
                    <div class="form-group">
                        <label class="form-label" for="genre-code">Code</label>
                        <input type="text" id="genre-code" class="form-control" placeholder="e.g. SCI_FI">
                    </div>
                    <div class="form-group">
                        <label class="form-label" for="genre-order">Display Order</label>
                        <input type="number" id="genre-order" class="form-control" value="0" min="0">
                    </div>
                    <div class="form-group">
                        <label class="form-label" for="genre-desc">Description</label>
                        <textarea id="genre-desc" class="form-control" rows="2"></textarea>
                    </div>
                </form>
            `,
            footerHtml: `
                <button class="btn btn-outline" onclick="utils.closeModal()">Cancel</button>
                <button class="btn btn-primary" id="save-genre-btn">Create Genre</button>
            `
        });

        const saveBtn = document.getElementById('save-genre-btn');
        if (saveBtn) {
            saveBtn.onclick = async () => {
                const name = document.getElementById('genre-name').value.trim();
                const code = document.getElementById('genre-code').value.trim();
                const displayOrder = parseInt(document.getElementById('genre-order').value) || 0;
                const description = document.getElementById('genre-desc').value.trim();

                if (!name) {
                    utils.toast('Genre name is mandatory', 'warning');
                    return;
                }

                try {
                    saveBtn.disabled = true;
                    await api.genres.create({ name, code: code || undefined, displayOrder, description, active: true });
                    utils.closeModal();
                    utils.toast('Genre created successfully!', 'success');
                    admin.loadGenres();
                } catch (e) {
                    utils.toast(e.message || 'Failed to create genre', 'error');
                } finally {
                    saveBtn.disabled = false;
                }
            };
        }
    },

    openEditGenreModal(id, name, code, desc, order) {
        utils.showModal({
            title: `Edit Genre #${id}`,
            bodyHtml: `
                <form id="edit-genre-form">
                    <div class="form-group">
                        <label class="form-label" for="edit-genre-name">Genre Name</label>
                        <input type="text" id="edit-genre-name" class="form-control" value="${name}" required>
                    </div>
                    <div class="form-group">
                        <label class="form-label" for="edit-genre-code">Code</label>
                        <input type="text" id="edit-genre-code" class="form-control" value="${code}">
                    </div>
                    <div class="form-group">
                        <label class="form-label" for="edit-genre-order">Display Order</label>
                        <input type="number" id="edit-genre-order" class="form-control" value="${order}">
                    </div>
                    <div class="form-group">
                        <label class="form-label" for="edit-genre-desc">Description</label>
                        <textarea id="edit-genre-desc" class="form-control" rows="2">${desc}</textarea>
                    </div>
                </form>
            `,
            footerHtml: `
                <button class="btn btn-outline" onclick="utils.closeModal()">Cancel</button>
                <button class="btn btn-primary" id="update-genre-btn">Save Changes</button>
            `
        });

        const updateBtn = document.getElementById('update-genre-btn');
        if (updateBtn) {
            updateBtn.onclick = async () => {
                const updated = {
                    name: document.getElementById('edit-genre-name').value.trim(),
                    code: document.getElementById('edit-genre-code').value.trim(),
                    displayOrder: parseInt(document.getElementById('edit-genre-order').value) || 0,
                    description: document.getElementById('edit-genre-desc').value.trim()
                };

                try {
                    updateBtn.disabled = true;
                    await api.genres.update(id, updated);
                    utils.closeModal();
                    utils.toast('Genre updated!', 'success');
                    admin.loadGenres();
                } catch (e) {
                    utils.toast(e.message || 'Failed to update genre', 'error');
                } finally {
                    updateBtn.disabled = false;
                }
            };
        }
    },

    softDeleteGenre(id) {
        if (confirm('Deactivate genre?')) {
            api.genres.delete(id)
                .then(() => {
                    utils.toast('Genre deactivated', 'info');
                    admin.loadGenres();
                })
                .catch(e => utils.toast(e.message || 'Deactivation failed', 'error'));
        }
    },

    hardDeleteGenre(id) {
        if (confirm('Permanently delete this genre?')) {
            api.genres.deleteHard(id)
                .then(() => {
                    utils.toast('Genre permanently deleted', 'success');
                    admin.loadGenres();
                })
                .catch(e => utils.toast(e.message || 'Delete failed', 'error'));
        }
    },

    // -------------------------------------------------------------
    // 4. Manage Subscription Plans
    // -------------------------------------------------------------
    async renderPlans() {
        const container = document.getElementById('app-content');
        if (!container) return;

        container.innerHTML = `
            <div class="page-header">
                <div>
                    <h1 class="page-title">Subscription Plans Management</h1>
                    <p class="page-subtitle">Configure membership tiers, duration limits, pricing, and borrow permissions</p>
                </div>
                <button class="btn btn-primary" onclick="admin.openCreatePlanModal()">+ Create New Plan</button>
            </div>

            <div id="admin-plans-container">
                <div class="spinner" style="margin: 3rem auto;"></div>
            </div>
        `;

        this.loadPlans();
    },

    async loadPlans() {
        const container = document.getElementById('admin-plans-container');
        if (!container) return;

        try {
            const plans = await api.plans.getAll();

            if (plans.length === 0) {
                container.innerHTML = '<div class="card text-center" style="padding: 2.5rem;"><p class="text-muted">No subscription plans created yet.</p></div>';
                return;
            }

            container.innerHTML = `
                <div class="table-responsive">
                    <table class="table">
                        <thead>
                            <tr>
                                <th>Plan Code</th>
                                <th>Name</th>
                                <th>Price</th>
                                <th>Duration</th>
                                <th>Max Books</th>
                                <th>Max Days/Book</th>
                                <th>Status</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            ${plans.map(p => `
                                <tr>
                                    <td><code>${utils.escapeHtml(p.planCode)}</code></td>
                                    <td>
                                        <strong>${utils.escapeHtml(p.planName)}</strong>
                                        ${p.isFeatured ? '<span class="badge badge-warning" style="margin-left: 0.35rem;">Featured</span>' : ''}
                                    </td>
                                    <td>${utils.formatCurrency(p.price, p.currency || 'INR')}</td>
                                    <td>${p.durationDays} days</td>
                                    <td>${p.maximumBooksAllowed}</td>
                                    <td>${p.maxDaysPerBook} days</td>
                                    <td>${utils.renderActiveBadge(p.isActive)}</td>
                                    <td>
                                        <div style="display: flex; gap: 0.35rem;">
                                            <button class="btn btn-outline btn-sm" onclick="admin.openEditPlanModal(${p.id})">Edit</button>
                                            <button class="btn btn-danger btn-sm" onclick="admin.deletePlan(${p.id}, '${utils.escapeHtml(p.planName.replace(/'/g, "\\'"))}')">Delete</button>
                                        </div>
                                    </td>
                                </tr>
                            `).join('')}
                        </tbody>
                    </table>
                </div>
            `;
        } catch (e) {
            container.innerHTML = `<div class="card text-center"><p class="text-danger">Failed to load plans: ${utils.escapeHtml(e.message)}</p></div>`;
        }
    },

    openCreatePlanModal() {
        utils.showModal({
            title: 'Create Subscription Plan',
            bodyHtml: `
                <form id="create-plan-form">
                    <div class="form-row">
                        <div class="form-group">
                            <label class="form-label" for="plan-name">Plan Name *</label>
                            <input type="text" id="plan-name" class="form-control" placeholder="e.g. Premium Scholar" required>
                        </div>
                        <div class="form-group">
                            <label class="form-label" for="plan-code">Plan Code *</label>
                            <input type="text" id="plan-code" class="form-control" placeholder="e.g. PREM_SCHOLAR" required>
                        </div>
                    </div>
                    <div class="form-row">
                        <div class="form-group">
                            <label class="form-label" for="plan-price">Price (in INR) *</label>
                            <input type="number" id="plan-price" class="form-control" value="499" min="1" required>
                        </div>
                        <div class="form-group">
                            <label class="form-label" for="plan-duration">Duration (Days) *</label>
                            <input type="number" id="plan-duration" class="form-control" value="90" min="1" required>
                        </div>
                    </div>
                    <div class="form-row">
                        <div class="form-group">
                            <label class="form-label" for="plan-max-books">Max Books Allowed *</label>
                            <input type="number" id="plan-max-books" class="form-control" value="5" min="1" required>
                        </div>
                        <div class="form-group">
                            <label class="form-label" for="plan-max-days">Max Days Per Book *</label>
                            <input type="number" id="plan-max-days" class="form-control" value="21" min="1" required>
                        </div>
                    </div>
                    <div class="form-row">
                        <div class="form-group">
                            <label class="form-label" for="plan-badge">Badge Text</label>
                            <input type="text" id="plan-badge" class="form-control" placeholder="e.g. Most Popular">
                        </div>
                        <div class="form-group" style="display: flex; align-items: flex-end; padding-bottom: 0.5rem;">
                            <label style="display: flex; align-items: center; gap: 0.5rem; cursor: pointer;">
                                <input type="checkbox" id="plan-featured"> Mark as Featured
                            </label>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="form-label" for="plan-desc">Description</label>
                        <textarea id="plan-desc" class="form-control" rows="2"></textarea>
                    </div>
                </form>
            `,
            footerHtml: `
                <button class="btn btn-outline" onclick="utils.closeModal()">Cancel</button>
                <button class="btn btn-primary" id="save-plan-btn">Create Plan</button>
            `
        });

        const saveBtn = document.getElementById('save-plan-btn');
        if (saveBtn) {
            saveBtn.onclick = async () => {
                const planName = document.getElementById('plan-name').value.trim();
                const planCode = document.getElementById('plan-code').value.trim();
                const price = parseInt(document.getElementById('plan-price').value);
                const durationDays = parseInt(document.getElementById('plan-duration').value);
                const maximumBooksAllowed = parseInt(document.getElementById('plan-max-books').value);
                const maxDaysPerBook = parseInt(document.getElementById('plan-max-days').value);
                const badgeText = document.getElementById('plan-badge').value.trim() || null;
                const isFeatured = document.getElementById('plan-featured').checked;
                const description = document.getElementById('plan-desc').value.trim() || null;

                if (!planName || !planCode || isNaN(price) || isNaN(durationDays)) {
                    utils.toast('Please provide all mandatory plan values.', 'warning');
                    return;
                }

                try {
                    saveBtn.disabled = true;
                    await api.plans.createAdmin({
                        planName, planCode, price, durationDays, maximumBooksAllowed,
                        maxDaysPerBook, badgeText, isFeatured, description,
                        currency: 'INR', isActive: true
                    });
                    utils.closeModal();
                    utils.toast('Subscription plan created!', 'success');
                    admin.loadPlans();
                } catch (e) {
                    utils.toast(e.message || 'Failed to create plan', 'error');
                } finally {
                    saveBtn.disabled = false;
                }
            };
        }
    },

    async openEditPlanModal(planId) {
        let plan;
        try {
            const all = await api.plans.getAll();
            plan = all.find(p => p.id === planId);
            if (!plan) throw new Error('Plan not found');
        } catch (e) {
            utils.toast('Failed to load plan: ' + e.message, 'error');
            return;
        }

        utils.showModal({
            title: `Edit Plan: ${plan.planName}`,
            bodyHtml: `
                <form id="edit-plan-form">
                    <div class="form-row">
                        <div class="form-group">
                            <label class="form-label" for="edit-p-name">Plan Name</label>
                            <input type="text" id="edit-p-name" class="form-control" value="${utils.escapeHtml(plan.planName)}" required>
                        </div>
                        <div class="form-group">
                            <label class="form-label" for="edit-p-code">Plan Code</label>
                            <input type="text" id="edit-p-code" class="form-control" value="${utils.escapeHtml(plan.planCode)}" required>
                        </div>
                    </div>
                    <div class="form-row">
                        <div class="form-group">
                            <label class="form-label" for="edit-p-price">Price (INR)</label>
                            <input type="number" id="edit-p-price" class="form-control" value="${plan.price}" required>
                        </div>
                        <div class="form-group">
                            <label class="form-label" for="edit-p-dur">Duration (Days)</label>
                            <input type="number" id="edit-p-dur" class="form-control" value="${plan.durationDays}" required>
                        </div>
                    </div>
                    <div class="form-row">
                        <div class="form-group">
                            <label class="form-label" for="edit-p-books">Max Books</label>
                            <input type="number" id="edit-p-books" class="form-control" value="${plan.maximumBooksAllowed}" required>
                        </div>
                        <div class="form-group">
                            <label class="form-label" for="edit-p-days">Max Days Per Book</label>
                            <input type="number" id="edit-p-days" class="form-control" value="${plan.maxDaysPerBook}" required>
                        </div>
                    </div>
                    <div class="form-row">
                        <div class="form-group">
                            <label class="form-label" for="edit-p-badge">Badge Text</label>
                            <input type="text" id="edit-p-badge" class="form-control" value="${utils.escapeHtml(plan.badgeText || '')}">
                        </div>
                        <div class="form-group" style="display: flex; align-items: flex-end; padding-bottom: 0.5rem;">
                            <label style="display: flex; align-items: center; gap: 0.5rem; cursor: pointer;">
                                <input type="checkbox" id="edit-p-featured" ${plan.isFeatured ? 'checked' : ''}> Featured
                            </label>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="form-label" for="edit-p-desc">Description</label>
                        <textarea id="edit-p-desc" class="form-control" rows="2">${utils.escapeHtml(plan.description || '')}</textarea>
                    </div>
                </form>
            `,
            footerHtml: `
                <button class="btn btn-outline" onclick="utils.closeModal()">Cancel</button>
                <button class="btn btn-primary" id="update-plan-btn">Save Plan</button>
            `
        });

        const updateBtn = document.getElementById('update-plan-btn');
        if (updateBtn) {
            updateBtn.onclick = async () => {
                const updated = {
                    planName: document.getElementById('edit-p-name').value.trim(),
                    planCode: document.getElementById('edit-p-code').value.trim(),
                    price: parseInt(document.getElementById('edit-p-price').value),
                    durationDays: parseInt(document.getElementById('edit-p-dur').value),
                    maximumBooksAllowed: parseInt(document.getElementById('edit-p-books').value),
                    maxDaysPerBook: parseInt(document.getElementById('edit-p-days').value),
                    badgeText: document.getElementById('edit-p-badge').value.trim() || null,
                    isFeatured: document.getElementById('edit-p-featured').checked,
                    description: document.getElementById('edit-p-desc').value.trim() || null,
                    currency: 'INR',
                    isActive: true
                };

                try {
                    updateBtn.disabled = true;
                    await api.plans.updateAdmin(planId, updated);
                    utils.closeModal();
                    utils.toast('Plan updated successfully!', 'success');
                    admin.loadPlans();
                } catch (e) {
                    utils.toast(e.message || 'Failed to update plan', 'error');
                } finally {
                    updateBtn.disabled = false;
                }
            };
        }
    },

    deletePlan(planId, name) {
        if (confirm(`Delete subscription plan "${name}"?`)) {
            api.plans.deleteAdmin(planId)
                .then(() => {
                    utils.toast('Plan deleted', 'success');
                    admin.loadPlans();
                })
                .catch(e => utils.toast(e.message || 'Delete failed', 'error'));
        }
    },

    // -------------------------------------------------------------
    // 5. Manage Member Subscriptions
    // -------------------------------------------------------------
    async renderSubscriptions() {
        const container = document.getElementById('app-content');
        if (!container) return;

        container.innerHTML = `
            <div class="page-header">
                <div>
                    <h1 class="page-title">Member Subscriptions</h1>
                    <p class="page-subtitle">View member subscriptions and deactivate expired memberships</p>
                </div>
                <button class="btn btn-warning" id="deactivate-expired-btn" onclick="admin.runDeactivateExpired()">
                    ⚡ Deactivate Expired Subscriptions
                </button>
            </div>

            <div id="admin-subs-table-container">
                <div class="spinner" style="margin: 3rem auto;"></div>
            </div>
        `;

        this.loadSubscriptionsTable();
    },

    async loadSubscriptionsTable() {
        const tableContainer = document.getElementById('admin-subs-table-container');
        if (!tableContainer) return;

        try {
            const subsList = await api.subscriptions.getAllAdmin();

            if (subsList.length === 0) {
                tableContainer.innerHTML = '<div class="card text-center" style="padding: 2.5rem;"><p class="text-muted">No member subscriptions found.</p></div>';
                return;
            }

            tableContainer.innerHTML = `
                <div class="table-responsive">
                    <table class="table">
                        <thead>
                            <tr>
                                <th>Sub ID</th>
                                <th>Member</th>
                                <th>Plan</th>
                                <th>Start Date</th>
                                <th>End Date</th>
                                <th>Max Books</th>
                                <th>Days Left</th>
                                <th>Status</th>
                                <th>Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            ${subsList.map(s => `
                                <tr>
                                    <td>#${s.id}</td>
                                    <td>
                                        <strong>${utils.escapeHtml(s.userName || 'User #' + s.userId)}</strong>
                                        <div class="text-muted text-sm">${utils.escapeHtml(s.userEmail || '')}</div>
                                    </td>
                                    <td><strong>${utils.escapeHtml(s.planName || s.planCode)}</strong></td>
                                    <td>${utils.formatDate(s.startDate)}</td>
                                    <td>${utils.formatDate(s.endDate)}</td>
                                    <td>${s.maxBooksAllowed} books</td>
                                    <td>${s.daysRemaining !== undefined ? s.daysRemaining + ' days' : '-'}</td>
                                    <td>${utils.renderActiveBadge(s.isActive)}</td>
                                    <td>
                                        ${s.isActive ? `
                                            <button class="btn btn-outline btn-sm" style="color: var(--danger); border-color: var(--danger);" onclick="subscriptions.openCancelModal(${s.id}, '${utils.escapeHtml((s.planName || s.planCode).replace(/'/g, "\\'"))}')">
                                                Cancel
                                            </button>
                                        ` : '<span class="text-muted text-sm">Expired / Cancelled</span>'}
                                    </td>
                                </tr>
                            `).join('')}
                        </tbody>
                    </table>
                </div>
            `;
        } catch (e) {
            tableContainer.innerHTML = `<div class="card text-center"><p class="text-danger">Failed to load subscriptions: ${utils.escapeHtml(e.message)}</p></div>`;
        }
    },

    async runDeactivateExpired() {
        const btn = document.getElementById('deactivate-expired-btn');
        try {
            if (btn) {
                btn.disabled = true;
                btn.textContent = 'Processing...';
            }
            const res = await api.subscriptions.deactivateExpiredAdmin();
            utils.toast(res.message || 'Expired subscriptions successfully deactivated!', 'success');
            admin.loadSubscriptionsTable();
        } catch (e) {
            utils.toast(e.message || 'Task failed', 'error');
        } finally {
            if (btn) {
                btn.disabled = false;
                btn.textContent = '⚡ Deactivate Expired Subscriptions';
            }
        }
    },

    // -------------------------------------------------------------
    // 6. Manage Users
    // -------------------------------------------------------------
    async renderUsers() {
        const container = document.getElementById('app-content');
        if (!container) return;

        container.innerHTML = `
            <div class="page-header">
                <div>
                    <h1 class="page-title">Registered Users</h1>
                    <p class="page-subtitle">View system users and administrative accounts</p>
                </div>
            </div>

            <div id="admin-users-container">
                <div class="spinner" style="margin: 3rem auto;"></div>
            </div>
        `;

        try {
            const usersList = await api.users.getAll();
            const usersContainer = document.getElementById('admin-users-container');

            if (!usersContainer) return;

            if (usersList.length === 0) {
                usersContainer.innerHTML = '<div class="card text-center" style="padding: 2.5rem;"><p class="text-muted">No users found.</p></div>';
                return;
            }

            usersContainer.innerHTML = `
                <div class="table-responsive">
                    <table class="table">
                        <thead>
                            <tr>
                                <th>User ID</th>
                                <th>Full Name</th>
                                <th>Email</th>
                                <th>Phone</th>
                                <th>Role</th>
                                <th>Last Login</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            ${usersList.map(u => `
                                <tr>
                                    <td>#${u.id}</td>
                                    <td><strong>${utils.escapeHtml(u.fullName)}</strong></td>
                                    <td>${utils.escapeHtml(u.email)}</td>
                                    <td>${utils.escapeHtml(u.phone || '-')}</td>
                                    <td>
                                        ${u.role === 'ROLE_ADMIN' 
                                            ? '<span class="badge badge-danger">ADMIN</span>' 
                                            : '<span class="badge badge-info">USER</span>'}
                                    </td>
                                    <td>${utils.formatDateTime(u.lastLogin)}</td>
                                    <td>
                                        <button class="btn btn-outline btn-sm" onclick="admin.openCheckoutForSpecificUser(${u.id})">
                                            Borrow for User
                                        </button>
                                    </td>
                                </tr>
                            `).join('')}
                        </tbody>
                    </table>
                </div>
            `;
        } catch (e) {
            const usersContainer = document.getElementById('admin-users-container');
            if (usersContainer) {
                usersContainer.innerHTML = `<div class="card text-center"><p class="text-danger">Failed to load users: ${utils.escapeHtml(e.message)}</p></div>`;
            }
        }
    },

    openCheckoutForSpecificUser(userId) {
        utils.showModal({
            title: `Checkout for User #${userId}`,
            bodyHtml: `
                <form id="user-checkout-form">
                    <div class="form-group">
                        <label class="form-label" for="user-book-id">Book ID *</label>
                        <input type="number" id="user-book-id" class="form-control" placeholder="Enter book ID to loan" required>
                    </div>
                    <div class="form-group">
                        <label class="form-label" for="user-checkout-days">Loan Duration (Days)</label>
                        <input type="number" id="user-checkout-days" class="form-control" placeholder="Leave blank for plan default">
                    </div>
                    <div class="form-group">
                        <label class="form-label" for="user-checkout-notes">Notes</label>
                        <textarea id="user-checkout-notes" class="form-control" rows="2" placeholder="Desk checkout notes..."></textarea>
                    </div>
                </form>
            `,
            footerHtml: `
                <button class="btn btn-outline" onclick="utils.closeModal()">Cancel</button>
                <button class="btn btn-primary" id="confirm-user-co-btn">Confirm Loan</button>
            `
        });

        const confirmBtn = document.getElementById('confirm-user-co-btn');
        if (confirmBtn) {
            confirmBtn.onclick = async () => {
                const bookId = document.getElementById('user-book-id').value;
                const days = document.getElementById('user-checkout-days').value;
                const notes = document.getElementById('user-checkout-notes').value.trim();

                if (!bookId) {
                    utils.toast('Book ID is required', 'warning');
                    return;
                }

                try {
                    confirmBtn.disabled = true;
                    await api.loans.checkoutForUser(userId, {
                        bookId: parseInt(bookId),
                        checkoutDays: days ? parseInt(days) : null,
                        notes: notes || undefined
                    });
                    utils.closeModal();
                    utils.toast(`Loan created successfully for User #${userId}`, 'success');
                } catch (e) {
                    utils.toast(e.message || 'Checkout failed', 'error');
                } finally {
                    confirmBtn.disabled = false;
                }
            };
        }
    },

    // -------------------------------------------------------------
    // 7. Manage Payments
    // -------------------------------------------------------------
    async renderPayments(params = {}) {
        const container = document.getElementById('app-content');
        if (!container) return;

        container.innerHTML = `
            <div class="page-header">
                <div>
                    <h1 class="page-title">Payment & Financial Transactions</h1>
                    <p class="page-subtitle">Audit logs for subscription charges, gateway statuses, and payments</p>
                </div>
            </div>

            <div id="admin-payments-container">
                <div class="spinner" style="margin: 3rem auto;"></div>
            </div>
        `;

        try {
            const pageData = await api.payments.getAll({ page: 0, size: 25 });
            const paymentsContainer = document.getElementById('admin-payments-container');

            if (!paymentsContainer) return;

            const list = pageData.content || [];

            if (list.length === 0) {
                paymentsContainer.innerHTML = '<div class="card text-center" style="padding: 2.5rem;"><p class="text-muted">No payment logs recorded yet.</p></div>';
                return;
            }

            paymentsContainer.innerHTML = `
                <div class="table-responsive">
                    <table class="table">
                        <thead>
                            <tr>
                                <th>Payment ID</th>
                                <th>User</th>
                                <th>Type</th>
                                <th>Gateway</th>
                                <th>Amount</th>
                                <th>Status</th>
                                <th>Initiated At</th>
                                <th>Completed At</th>
                            </tr>
                        </thead>
                        <tbody>
                            ${list.map(p => {
                                const statusCls = p.status === 'SUCCESS' ? 'badge-success' : (p.status === 'FAILED' ? 'badge-danger' : 'badge-warning');
                                return `
                                    <tr>
                                        <td>#${p.id}</td>
                                        <td>
                                            <strong>${utils.escapeHtml(p.userName || 'User #' + p.userId)}</strong>
                                            <div class="text-muted text-sm">${utils.escapeHtml(p.userEmail || '')}</div>
                                        </td>
                                        <td><span class="badge badge-secondary">${utils.escapeHtml(p.paymentType || '-')}</span></td>
                                        <td><code>${utils.escapeHtml(p.gateway || 'RAZORPAY')}</code></td>
                                        <td><strong>${utils.formatCurrency(p.amount ? p.amount / 100 : 0)}</strong></td>
                                        <td><span class="badge ${statusCls}">${utils.escapeHtml(p.status || 'PENDING')}</span></td>
                                        <td>${utils.formatDateTime(p.initiatedAt)}</td>
                                        <td>${utils.formatDateTime(p.completedAt)}</td>
                                    </tr>
                                `;
                            }).join('')}
                        </tbody>
                    </table>
                </div>
            `;
        } catch (e) {
            const paymentsContainer = document.getElementById('admin-payments-container');
            if (paymentsContainer) {
                paymentsContainer.innerHTML = `<div class="card text-center"><p class="text-danger">Failed to load payments: ${utils.escapeHtml(e.message)}</p></div>`;
            }
        }
    },

    // -------------------------------------------------------------
    // 8. Fine & Penalty Management
    // -------------------------------------------------------------
    adminFinesState: {
        page: 0,
        size: 15,
        status: '',
        type: '',
        userId: '',
        cachedList: []
    },

    async renderFines(params = {}) {
        const container = document.getElementById('app-content');
        if (!container) return;

        if (params.page !== undefined) this.adminFinesState.page = parseInt(params.page);
        if (params.status !== undefined) this.adminFinesState.status = params.status;
        if (params.type !== undefined) this.adminFinesState.type = params.type;
        if (params.userId !== undefined) this.adminFinesState.userId = params.userId;

        container.innerHTML = `
            <div class="page-header">
                <div>
                    <h1 class="page-title">Fine & Penalty Records</h1>
                    <p class="page-subtitle">Assess fines, process payments, waive fees, and audit member penalties</p>
                </div>
                <div style="display: flex; gap: 0.5rem; flex-wrap: wrap;">
                    <button class="btn btn-primary" onclick="admin.openCreateFineModal()">+ Issue New Fine</button>
                    <button class="btn btn-outline" onclick="admin.renderFines()">🔄 Refresh</button>
                </div>
            </div>

            <!-- Filter Controls -->
            <div class="filter-bar" style="display: flex; gap: 0.75rem; flex-wrap: wrap; align-items: flex-end;">
                <div class="form-group" style="margin: 0; min-width: 140px;">
                    <label class="form-label" style="font-size: 0.75rem; text-transform: uppercase;">Status</label>
                    <select id="admin-fine-filter-status" class="form-control" style="padding: 0.45rem 0.65rem;">
                        <option value="" ${this.adminFinesState.status === '' ? 'selected' : ''}>All Statuses</option>
                        <option value="PENDING" ${this.adminFinesState.status === 'PENDING' ? 'selected' : ''}>Pending</option>
                        <option value="PARTIALLY_PAID" ${this.adminFinesState.status === 'PARTIALLY_PAID' ? 'selected' : ''}>Partially Paid</option>
                        <option value="PAID" ${this.adminFinesState.status === 'PAID' ? 'selected' : ''}>Paid</option>
                        <option value="WAIVED" ${this.adminFinesState.status === 'WAIVED' ? 'selected' : ''}>Waived</option>
                    </select>
                </div>

                <div class="form-group" style="margin: 0; min-width: 140px;">
                    <label class="form-label" style="font-size: 0.75rem; text-transform: uppercase;">Type</label>
                    <select id="admin-fine-filter-type" class="form-control" style="padding: 0.45rem 0.65rem;">
                        <option value="" ${this.adminFinesState.type === '' ? 'selected' : ''}>All Types</option>
                        <option value="OVERDUE" ${this.adminFinesState.type === 'OVERDUE' ? 'selected' : ''}>Overdue</option>
                        <option value="DAMAGE" ${this.adminFinesState.type === 'DAMAGE' ? 'selected' : ''}>Damage</option>
                        <option value="LOSS" ${this.adminFinesState.type === 'LOSS' ? 'selected' : ''}>Lost Book</option>
                        <option value="PROCESSING" ${this.adminFinesState.type === 'PROCESSING' ? 'selected' : ''}>Processing</option>
                    </select>
                </div>

                <div class="form-group" style="margin: 0; min-width: 120px;">
                    <label class="form-label" style="font-size: 0.75rem; text-transform: uppercase;">User ID</label>
                    <input type="number" id="admin-fine-filter-user-id" class="form-control" placeholder="User ID" value="${this.adminFinesState.userId || ''}" style="padding: 0.45rem 0.65rem;" />
                </div>

                <div style="display: flex; gap: 0.35rem;">
                    <button class="btn btn-primary btn-sm" style="padding: 0.55rem 1rem;" onclick="admin.applyFineFilters()">Filter</button>
                    <button class="btn btn-outline btn-sm" style="padding: 0.55rem 0.75rem;" onclick="admin.resetFineFilters()">Reset</button>
                </div>
            </div>

            <!-- Fines Table Container -->
            <div id="admin-fines-table-container">
                <div class="spinner" style="margin: 3rem auto;"></div>
            </div>

            <!-- Pagination Container -->
            <div id="admin-fines-pagination" class="pagination"></div>
        `;

        this.loadFinesTable();
    },

    applyFineFilters() {
        const statusEl = document.getElementById('admin-fine-filter-status');
        const typeEl = document.getElementById('admin-fine-filter-type');
        const userEl = document.getElementById('admin-fine-filter-user-id');

        this.adminFinesState.status = statusEl ? statusEl.value : '';
        this.adminFinesState.type = typeEl ? typeEl.value : '';
        this.adminFinesState.userId = userEl && userEl.value.trim() ? userEl.value.trim() : '';
        this.adminFinesState.page = 0;

        this.loadFinesTable();
    },

    resetFineFilters() {
        this.adminFinesState.status = '';
        this.adminFinesState.type = '';
        this.adminFinesState.userId = '';
        this.adminFinesState.page = 0;

        const statusEl = document.getElementById('admin-fine-filter-status');
        const typeEl = document.getElementById('admin-fine-filter-type');
        const userEl = document.getElementById('admin-fine-filter-user-id');
        if (statusEl) statusEl.value = '';
        if (typeEl) typeEl.value = '';
        if (userEl) userEl.value = '';

        this.loadFinesTable();
    },

    async loadFinesTable() {
        const tableContainer = document.getElementById('admin-fines-table-container');
        const paginationContainer = document.getElementById('admin-fines-pagination');
        if (!tableContainer) return;

        tableContainer.innerHTML = '<div class="spinner" style="margin: 3rem auto;"></div>';

        try {
            const pageData = await api.fines.getAll({
                page: this.adminFinesState.page,
                size: this.adminFinesState.size,
                status: this.adminFinesState.status || undefined,
                type: this.adminFinesState.type || undefined,
                userId: this.adminFinesState.userId ? parseInt(this.adminFinesState.userId) : undefined
            });

            const list = pageData.content || [];
            this.adminFinesState.cachedList = list;

            if (list.length === 0) {
                tableContainer.innerHTML = `
                    <div class="card text-center" style="padding: 3rem 1.5rem;">
                        <span style="font-size: 2.5rem;">⚖️</span>
                        <h3 class="mt-2">No fine records found</h3>
                        <p class="text-muted text-sm mt-1">Try adjusting your filters or issue a new fine.</p>
                        <button class="btn btn-primary btn-sm mt-3" onclick="admin.openCreateFineModal()">+ Issue Fine</button>
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
                                <th>Fine ID</th>
                                <th>Member</th>
                                <th>Loan / Book Info</th>
                                <th>Type</th>
                                <th>Amount</th>
                                <th>Outstanding</th>
                                <th>Status</th>
                                <th>Created At</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            ${list.map(f => {
                                const isPending = f.status === 'PENDING' || f.status === 'PARTIALLY_PAID';

                                return `
                                    <tr>
                                        <td><strong>#${f.id}</strong></td>
                                        <td>
                                            <strong>${utils.escapeHtml(f.userName || 'User #' + f.userId)}</strong>
                                            <div class="text-muted text-sm">${utils.escapeHtml(f.userEmail || '')}</div>
                                            <div class="text-muted text-sm" style="font-size: 0.75rem;">User ID: #${f.userId}</div>
                                        </td>
                                        <td>
                                            <strong>${utils.escapeHtml(f.bookTitle || 'Book Loan #' + f.bookLoanId)}</strong>
                                            ${f.bookIsbn ? `<div class="text-muted text-sm">ISBN: <code>${utils.escapeHtml(f.bookIsbn)}</code></div>` : ''}
                                            <div class="text-muted text-sm" style="font-size: 0.75rem;">Loan ID: #${f.bookLoanId}</div>
                                        </td>
                                        <td>
                                            ${utils.renderFineTypeBadge(f.type)}
                                            ${f.reason ? `<div class="text-muted text-sm" style="max-width: 180px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap;" title="${utils.escapeHtml(f.reason)}">${utils.escapeHtml(f.reason)}</div>` : ''}
                                        </td>
                                        <td><strong>${utils.formatCurrency(f.amount)}</strong></td>
                                        <td>
                                            ${f.amountOutstanding !== undefined && f.amountOutstanding !== null
                                                ? (f.amountOutstanding > 0 
                                                    ? `<span style="color: var(--danger); font-weight: 700;">${utils.formatCurrency(f.amountOutstanding)}</span>` 
                                                    : `<span style="color: var(--success); font-weight: 600;">₹0.00</span>`)
                                                : (isPending ? `<span style="color: var(--danger); font-weight: 700;">${utils.formatCurrency(f.amount)}</span>` : '₹0.00')}
                                        </td>
                                        <td>${utils.renderFineStatusBadge(f.status)}</td>
                                        <td>${utils.formatDateTime(f.createdAt)}</td>
                                        <td>
                                            <div style="display: flex; gap: 0.35rem; flex-wrap: wrap;">
                                                <button class="btn btn-outline btn-sm" onclick="admin.openFineDetailsModal(${f.id})">Details</button>
                                                ${isPending ? `
                                                    <button class="btn btn-success btn-sm" title="Mark Paid / Settle" onclick="admin.openMarkFinePaidModal(${f.id})">✓ Settle</button>
                                                    <button class="btn btn-outline btn-sm" style="color: var(--warning); border-color: var(--warning);" title="Waive this penalty" onclick="admin.openWaiveFineModal(${f.id})">Waive</button>
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

            // Render Pagination
            if (paginationContainer) {
                const totalPages = pageData.totalPages || 1;
                const currentPage = pageData.pageNumber || 0;

                if (totalPages > 1) {
                    let pagHtml = `
                        <button class="btn btn-outline btn-sm" ${pageData.first ? 'disabled' : ''} onclick="admin.changeFinePage(${currentPage - 1})">
                            &laquo; Prev
                        </button>
                        <span class="text-sm text-muted" style="margin: 0 0.5rem;">
                            Page <strong>${currentPage + 1}</strong> of <strong>${totalPages}</strong> (${pageData.totalElements} records)
                        </span>
                        <button class="btn btn-outline btn-sm" ${pageData.last ? 'disabled' : ''} onclick="admin.changeFinePage(${currentPage + 1})">
                            Next &raquo;
                        </button>
                    `;
                    paginationContainer.innerHTML = pagHtml;
                } else {
                    paginationContainer.innerHTML = `<span class="text-sm text-muted">Showing ${pageData.totalElements} fine records</span>`;
                }
            }
        } catch (err) {
            console.error('Failed to load admin fines:', err);
            tableContainer.innerHTML = `
                <div class="card text-center" style="padding: 2.5rem;">
                    <p class="text-danger">Failed to load fine records: ${utils.escapeHtml(err.message || 'Server error')}</p>
                    <button class="btn btn-outline btn-sm mt-2" onclick="admin.loadFinesTable()">Try Again</button>
                </div>
            `;
        }
    },

    changeFinePage(newPage) {
        if (newPage < 0) return;
        this.adminFinesState.page = newPage;
        this.loadFinesTable();
    },

    openCreateFineModal(prefillLoanId = null) {
        utils.showModal({
            title: 'Issue New Fine / Penalty',
            bodyHtml: `
                <form id="create-fine-form" onsubmit="event.preventDefault(); admin.submitCreateFine();">
                    <div class="form-group">
                        <label class="form-label" for="fine-book-loan-id">Book Loan ID <span style="color: red;">*</span></label>
                        <input type="number" id="fine-book-loan-id" class="form-control" placeholder="e.g. 101" value="${prefillLoanId || ''}" required />
                        <small class="text-muted">The active or returned loan record to assess the penalty against</small>
                    </div>

                    <div class="form-row">
                        <div class="form-group">
                            <label class="form-label" for="fine-type">Penalty Type <span style="color: red;">*</span></label>
                            <select id="fine-type" class="form-control" required>
                                <option value="OVERDUE">OVERDUE - Late return penalty</option>
                                <option value="DAMAGE">DAMAGE - Book damaged condition</option>
                                <option value="LOSS">LOSS - Book reported lost</option>
                                <option value="PROCESSING">PROCESSING - Administrative fee</option>
                            </select>
                        </div>

                        <div class="form-group">
                            <label class="form-label" for="fine-amount">Fine Amount (₹) <span style="color: red;">*</span></label>
                            <input type="number" id="fine-amount" class="form-control" min="1" step="1" placeholder="e.g. 50" required />
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="form-label" for="fine-reason">Reason for Fine <span style="color: red;">*</span></label>
                        <input type="text" id="fine-reason" class="form-control" placeholder="e.g. 5 days overdue, cover torn, missing pages" required />
                    </div>

                    <div class="form-group">
                        <label class="form-label" for="fine-notes">Staff Notes (Optional)</label>
                        <textarea id="fine-notes" class="form-control" rows="2" placeholder="Internal remarks or inspection comments..."></textarea>
                    </div>

                    <div id="create-fine-error" class="text-danger text-sm mb-2" style="display: none;"></div>
                </form>
            `,
            footerHtml: `
                <button type="button" class="btn btn-outline" onclick="utils.closeModal()">Cancel</button>
                <button type="button" class="btn btn-primary" id="btn-submit-fine" onclick="admin.submitCreateFine()">Issue Fine</button>
            `
        });
    },

    async submitCreateFine() {
        const loanIdInput = document.getElementById('fine-book-loan-id');
        const typeInput = document.getElementById('fine-type');
        const amountInput = document.getElementById('fine-amount');
        const reasonInput = document.getElementById('fine-reason');
        const notesInput = document.getElementById('fine-notes');
        const errorEl = document.getElementById('create-fine-error');
        const submitBtn = document.getElementById('btn-submit-fine');

        const bookLoanId = loanIdInput ? parseInt(loanIdInput.value) : null;
        const type = typeInput ? typeInput.value : '';
        const amount = amountInput ? parseInt(amountInput.value) : null;
        const reason = reasonInput ? reasonInput.value.trim() : '';
        const notes = notesInput ? notesInput.value.trim() : '';

        if (!bookLoanId || isNaN(bookLoanId)) {
            if (errorEl) { errorEl.textContent = 'Please enter a valid Book Loan ID.'; errorEl.style.display = 'block'; }
            return;
        }
        if (!amount || amount <= 0) {
            if (errorEl) { errorEl.textContent = 'Amount must be greater than zero.'; errorEl.style.display = 'block'; }
            return;
        }
        if (!reason) {
            if (errorEl) { errorEl.textContent = 'Please specify a reason for this fine.'; errorEl.style.display = 'block'; }
            return;
        }

        if (submitBtn) {
            submitBtn.disabled = true;
            submitBtn.textContent = 'Issuing...';
        }

        try {
            const created = await api.fines.create({
                bookLoanId,
                type,
                amount,
                reason,
                notes: notes || undefined
            });

            utils.closeModal();
            utils.toast(`Fine #${created.id} issued successfully for ₹${created.amount}!`, 'success');
            admin.loadFinesTable();
        } catch (err) {
            console.error('Failed to issue fine:', err);
            if (errorEl) {
                errorEl.textContent = err.message || 'Failed to issue fine.';
                errorEl.style.display = 'block';
            }
            if (submitBtn) {
                submitBtn.disabled = false;
                submitBtn.textContent = 'Issue Fine';
            }
        }
    },

    openWaiveFineModal(fineId) {
        const fine = this.adminFinesState.cachedList.find(f => f.id === fineId);
        const amount = fine ? fine.amount : '';
        const userName = fine ? fine.userName : '';

        utils.showModal({
            title: `Waive Fine #${fineId}`,
            bodyHtml: `
                <div style="padding: 0.5rem 0;">
                    <div style="background: #fffbeb; border: 1px solid #fef3c7; border-radius: var(--radius-md); padding: 0.85rem; margin-bottom: 1rem;">
                        <p style="font-size: 0.875rem; color: #92400e; margin: 0; line-height: 1.4;">
                            ⚠️ You are about to waive penalty <strong>#${fineId}</strong> ${amount ? `(₹${amount})` : ''} for <strong>${utils.escapeHtml(userName)}</strong>. This will set the outstanding balance to ₹0.00 and cannot be undone.
                        </p>
                    </div>

                    <div class="form-group">
                        <label class="form-label" for="waive-reason-input">Reason for Waiver <span style="color: red;">*</span></label>
                        <textarea id="waive-reason-input" class="form-control" rows="3" placeholder="e.g. Approved medical excuse, first-time student waiver, library administrative error..." required></textarea>
                        <small class="text-muted">A clear justification is required for auditing and compliance.</small>
                    </div>

                    <div id="waive-fine-error" class="text-danger text-sm mb-2" style="display: none;"></div>
                </div>
            `,
            footerHtml: `
                <button type="button" class="btn btn-outline" onclick="utils.closeModal()">Cancel</button>
                <button type="button" class="btn btn-warning" id="btn-submit-waive" onclick="admin.submitWaiveFine(${fineId})">Confirm Waiver</button>
            `
        });
    },

    async submitWaiveFine(fineId) {
        const input = document.getElementById('waive-reason-input');
        const reason = input ? input.value.trim() : '';
        const errorEl = document.getElementById('waive-fine-error');
        const submitBtn = document.getElementById('btn-submit-waive');

        if (!reason) {
            if (errorEl) {
                errorEl.textContent = 'Please provide a reason for waiving this fine.';
                errorEl.style.display = 'block';
            }
            if (input) input.focus();
            return;
        }

        if (submitBtn) {
            submitBtn.disabled = true;
            submitBtn.textContent = 'Waiving...';
        }

        try {
            await api.fines.waive({
                fineId: fineId,
                reason: reason
            });

            utils.closeModal();
            utils.toast(`Fine #${fineId} has been successfully waived!`, 'success');
            admin.loadFinesTable();
        } catch (err) {
            console.error('Failed to waive fine:', err);
            if (errorEl) {
                errorEl.textContent = err.message || 'Failed to waive fine.';
                errorEl.style.display = 'block';
            }
            if (submitBtn) {
                submitBtn.disabled = false;
                submitBtn.textContent = 'Confirm Waiver';
            }
        }
    },

    openMarkFinePaidModal(fineId) {
        const fine = this.adminFinesState.cachedList.find(f => f.id === fineId);
        const amount = fine ? (fine.amountOutstanding || fine.amount) : 0;
        const userName = fine ? fine.userName : '';

        utils.showModal({
            title: `Settle Fine #${fineId}`,
            bodyHtml: `
                <div style="padding: 0.5rem 0;">
                    <div style="background: #f0fdf4; border: 1px solid #bbf7d0; border-radius: var(--radius-md); padding: 0.85rem; margin-bottom: 1rem;">
                        <div style="display: flex; justify-content: space-between; align-items: baseline;">
                            <span style="font-size: 0.875rem; color: #166534;">Member: <strong>${utils.escapeHtml(userName)}</strong></span>
                            <span style="font-size: 1.15rem; font-weight: 700; color: #15803d;">${utils.formatCurrency(amount)}</span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="form-label" for="admin-settle-txn-id">Payment Reference / Receipt ID</label>
                        <input type="text" id="admin-settle-txn-id" class="form-control" placeholder="e.g. CASH-DESK-${fineId} or UPI/0987654321" />
                        <small class="text-muted">Enter cash receipt number, POS terminal auth code, or UPI reference.</small>
                    </div>

                    <div id="settle-fine-error" class="text-danger text-sm mb-2" style="display: none;"></div>
                </div>
            `,
            footerHtml: `
                <button type="button" class="btn btn-outline" onclick="utils.closeModal()">Cancel</button>
                <button type="button" class="btn btn-success" id="btn-submit-settle" onclick="admin.submitMarkFinePaid(${fineId})">Mark as Paid</button>
            `
        });
    },

    async submitMarkFinePaid(fineId) {
        const input = document.getElementById('admin-settle-txn-id');
        const txnId = input && input.value.trim() ? input.value.trim() : `DESK-CASH-${Date.now()}`;
        const errorEl = document.getElementById('settle-fine-error');
        const submitBtn = document.getElementById('btn-submit-settle');

        if (submitBtn) {
            submitBtn.disabled = true;
            submitBtn.textContent = 'Processing...';
        }

        try {
            const res = await api.fines.pay(fineId, txnId);
            utils.closeModal();
            utils.toast(res.message || `Fine #${fineId} marked as paid successfully!`, 'success');
            admin.loadFinesTable();
        } catch (err) {
            console.error('Failed to mark fine paid:', err);
            if (errorEl) {
                errorEl.textContent = err.message || 'Failed to record payment.';
                errorEl.style.display = 'block';
            }
            if (submitBtn) {
                submitBtn.disabled = false;
                submitBtn.textContent = 'Mark as Paid';
            }
        }
    },

    openFineDetailsModal(fineId) {
        const fine = this.adminFinesState.cachedList.find(f => f.id === fineId);
        if (!fine) return;

        utils.showModal({
            title: `Audit Details: Fine #${fine.id}`,
            bodyHtml: `
                <div style="display: flex; flex-direction: column; gap: 1rem; padding: 0.5rem 0;">
                    <div style="display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 0.5rem;">
                        <span style="font-size: 1.15rem; font-weight: 700;">Penalty Record #${fine.id}</span>
                        <div>
                            ${utils.renderFineTypeBadge(fine.type)}
                            ${utils.renderFineStatusBadge(fine.status)}
                        </div>
                    </div>

                    <!-- Member & Loan Box -->
                    <div class="card" style="background: var(--bg-main); border: 1px solid var(--border); margin: 0; padding: 1rem;">
                        <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 0.85rem; font-size: 0.875rem;">
                            <div>
                                <span class="text-muted">Member Name:</span>
                                <div><strong>${utils.escapeHtml(fine.userName || '-')}</strong></div>
                            </div>
                            <div>
                                <span class="text-muted">Email & ID:</span>
                                <div>${utils.escapeHtml(fine.userEmail || '-')} (User #${fine.userId})</div>
                            </div>
                            <div>
                                <span class="text-muted">Associated Book:</span>
                                <div><strong>${utils.escapeHtml(fine.bookTitle || '-')}</strong></div>
                            </div>
                            <div>
                                <span class="text-muted">ISBN & Loan ID:</span>
                                <div><code>${utils.escapeHtml(fine.bookIsbn || '-')}</code> (Loan #${fine.bookLoanId || '-'})</div>
                            </div>
                            <div>
                                <span class="text-muted">Total Assessed:</span>
                                <div><strong style="font-size: 1.1rem; color: var(--primary);">${utils.formatCurrency(fine.amount)}</strong></div>
                            </div>
                            <div>
                                <span class="text-muted">Balance Due:</span>
                                <div><strong style="font-size: 1.1rem; color: ${fine.status === 'PAID' || fine.status === 'WAIVED' ? 'var(--success)' : 'var(--danger)'};">${utils.formatCurrency(fine.amountOutstanding || 0)}</strong></div>
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
                            <strong style="font-size: 0.85rem; color: var(--text-secondary);">Staff / Inspection Notes:</strong>
                            <p style="background: #f8fafc; padding: 0.65rem 0.85rem; border-radius: var(--radius-sm); border: 1px solid var(--border); margin-top: 0.25rem; font-size: 0.875rem;">
                                ${utils.escapeHtml(fine.notes)}
                            </p>
                        </div>
                    ` : ''}

                    <!-- Audit Trail Timestamps -->
                    <div style="background: #f8fafc; border: 1px solid var(--border); border-radius: var(--radius-md); padding: 0.85rem; font-size: 0.825rem; display: grid; grid-template-columns: repeat(auto-fit, minmax(180px, 1fr)); gap: 0.5rem;">
                        <div>Created At: <strong>${utils.formatDateTime(fine.createdAt)}</strong></div>
                        <div>Last Updated: <strong>${utils.formatDateTime(fine.updatedAt)}</strong></div>
                        ${fine.processByUserName ? `<div>Processed By: <strong>${utils.escapeHtml(fine.processByUserName)}</strong></div>` : ''}
                    </div>

                    ${fine.status === 'PAID' ? `
                        <div style="background: #f0fdf4; border: 1px solid #bbf7d0; border-radius: var(--radius-md); padding: 0.85rem; font-size: 0.85rem; color: #15803d;">
                            <strong>Payment Settlement Details:</strong>
                            <div style="margin-top: 0.35rem;">Settled At: <strong>${utils.formatDateTime(fine.paidAt)}</strong></div>
                            <div style="margin-top: 0.25rem;">Transaction Ref: <code>${utils.escapeHtml(fine.transactionId || 'Direct Payment')}</code></div>
                        </div>
                    ` : ''}

                    ${fine.status === 'WAIVED' ? `
                        <div style="background: #f1f5f9; border: 1px solid var(--border); border-radius: var(--radius-md); padding: 0.85rem; font-size: 0.85rem; color: var(--text-secondary);">
                            <strong style="color: var(--text-primary);">Waiver Details:</strong>
                            <div style="margin-top: 0.35rem;">Waived At: <strong>${utils.formatDateTime(fine.waivedAt)}</strong></div>
                            ${fine.waivedByUserName ? `<div>Waived By: <strong>${utils.escapeHtml(fine.waivedByUserName)}</strong></div>` : ''}
                            <div style="margin-top: 0.25rem;">Waiver Justification: <em style="color: var(--text-primary);">"${utils.escapeHtml(fine.waiverReason || 'No reason specified')}"</em></div>
                        </div>
                    ` : ''}
                </div>
            `,
            footerHtml: `
                <button type="button" class="btn btn-outline" onclick="utils.closeModal()">Close</button>
                ${fine.status === 'PENDING' || fine.status === 'PARTIALLY_PAID' ? `
                    <button type="button" class="btn btn-warning" onclick="admin.openWaiveFineModal(${fine.id})">Waive Fine</button>
                    <button type="button" class="btn btn-success" onclick="admin.openMarkFinePaidModal(${fine.id})">Settle Payment</button>
                ` : ''}
            `
        });
    }
};
