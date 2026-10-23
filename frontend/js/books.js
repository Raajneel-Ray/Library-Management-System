// Book Browsing, Searching and Book Detail View

const books = {
    currentFilters: {
        searchTerm: '',
        genreId: '',
        availableOnly: false,
        page: 0,
        size: 12,
        sortBy: 'createdAt',
        sortDirection: 'DESC'
    },

    async renderList(params = {}) {
        const container = document.getElementById('app-content');
        if (!container) return;

        if (params.page !== undefined) this.currentFilters.page = parseInt(params.page);

        container.innerHTML = `
            <div class="page-header">
                <div>
                    <h1 class="page-title">Library Collection</h1>
                    <p class="page-subtitle">Search, explore, and borrow books from our library</p>
                </div>
            </div>

            <!-- Search & Filters -->
            <div class="filter-bar">
                <div style="flex: 2 1 240px;">
                    <input type="text" id="book-search-input" class="form-control" placeholder="Search by title, author, or ISBN..." value="${utils.escapeHtml(this.currentFilters.searchTerm)}">
                </div>
                <div style="flex: 1 1 180px;">
                    <select id="book-genre-filter" class="form-control">
                        <option value="">All Genres</option>
                    </select>
                </div>
                <div style="display: flex; align-items: center; gap: 0.5rem; white-space: nowrap;">
                    <label style="display: flex; align-items: center; gap: 0.35rem; font-size: 0.875rem; cursor: pointer;">
                        <input type="checkbox" id="book-available-only" ${this.currentFilters.availableOnly ? 'checked' : ''}>
                        Available only
                    </label>
                </div>
                <div style="flex: 1 1 140px;">
                    <select id="book-sort-by" class="form-control">
                        <option value="createdAt" ${this.currentFilters.sortBy === 'createdAt' ? 'selected' : ''}>Latest Added</option>
                        <option value="title" ${this.currentFilters.sortBy === 'title' ? 'selected' : ''}>Title (A-Z)</option>
                        <option value="availableCopies" ${this.currentFilters.sortBy === 'availableCopies' ? 'selected' : ''}>Availability</option>
                    </select>
                </div>
                <button id="book-search-btn" class="btn btn-primary">Search</button>
                <button id="book-reset-btn" class="btn btn-outline">Reset</button>
            </div>

            <!-- Books Grid Container -->
            <div id="books-grid-container">
                <div class="spinner" style="margin: 3rem auto;"></div>
            </div>

            <!-- Pagination Container -->
            <div id="books-pagination" class="pagination"></div>
        `;

        this.loadGenresDropdown();
        this.setupFilterListeners();
        this.loadBooks();
    },

    async loadGenresDropdown() {
        try {
            const genresList = await api.genres.getAll();
            const select = document.getElementById('book-genre-filter');
            if (!select) return;

            genresList.forEach(g => {
                const opt = document.createElement('option');
                opt.value = g.id;
                opt.textContent = g.name;
                if (String(g.id) === String(this.currentFilters.genreId)) {
                    opt.selected = true;
                }
                select.appendChild(opt);
            });
        } catch (e) {
            console.error('Failed to load genres:', e);
        }
    },

    setupFilterListeners() {
        const searchInput = document.getElementById('book-search-input');
        const genreSelect = document.getElementById('book-genre-filter');
        const availCheck = document.getElementById('book-available-only');
        const sortSelect = document.getElementById('book-sort-by');
        const searchBtn = document.getElementById('book-search-btn');
        const resetBtn = document.getElementById('book-reset-btn');

        const triggerSearch = () => {
            this.currentFilters.searchTerm = searchInput.value.trim();
            this.currentFilters.genreId = genreSelect.value;
            this.currentFilters.availableOnly = availCheck.checked;
            this.currentFilters.sortBy = sortSelect.value;
            this.currentFilters.page = 0;
            this.loadBooks();
        };

        if (searchBtn) searchBtn.onclick = triggerSearch;
        if (searchInput) {
            searchInput.onkeydown = (e) => {
                if (e.key === 'Enter') triggerSearch();
            };
        }
        if (genreSelect) genreSelect.onchange = triggerSearch;
        if (availCheck) availCheck.onchange = triggerSearch;
        if (sortSelect) sortSelect.onchange = triggerSearch;

        if (resetBtn) {
            resetBtn.onclick = () => {
                this.currentFilters = {
                    searchTerm: '',
                    genreId: '',
                    availableOnly: false,
                    page: 0,
                    size: 12,
                    sortBy: 'createdAt',
                    sortDirection: 'DESC'
                };
                searchInput.value = '';
                genreSelect.value = '';
                availCheck.checked = false;
                sortSelect.value = 'createdAt';
                this.loadBooks();
            };
        }
    },

    async loadBooks() {
        const gridContainer = document.getElementById('books-grid-container');
        const paginationContainer = document.getElementById('books-pagination');
        if (!gridContainer) return;

        gridContainer.innerHTML = '<div class="spinner" style="margin: 3rem auto;"></div>';

        try {
            // Use advancedSearch POST if searchTerm exists, else search GET
            let pageData;
            if (this.currentFilters.searchTerm) {
                pageData = await api.books.advancedSearch({
                    searchTerm: this.currentFilters.searchTerm,
                    genreId: this.currentFilters.genreId ? parseInt(this.currentFilters.genreId) : null,
                    availableOnly: this.currentFilters.availableOnly,
                    page: this.currentFilters.page,
                    size: this.currentFilters.size,
                    sortBy: this.currentFilters.sortBy,
                    sortDirection: this.currentFilters.sortDirection
                });
            } else {
                pageData = await api.books.search({
                    genreId: this.currentFilters.genreId || undefined,
                    availableOnly: this.currentFilters.availableOnly,
                    page: this.currentFilters.page,
                    size: this.currentFilters.size,
                    sortBy: this.currentFilters.sortBy,
                    sortDirection: this.currentFilters.sortDirection
                });
            }

            const booksList = pageData.content || [];

            if (booksList.length === 0) {
                gridContainer.innerHTML = `
                    <div class="card text-center" style="padding: 3rem 1.5rem;">
                        <span style="font-size: 2.5rem;">🔍</span>
                        <h3 class="mt-2">No books found</h3>
                        <p class="text-muted text-sm mt-1">Try adjusting your filters or search keywords.</p>
                    </div>
                `;
                if (paginationContainer) paginationContainer.innerHTML = '';
                return;
            }

            gridContainer.innerHTML = `
                <div class="books-grid">
                    ${booksList.map(book => {
                        const isAvailable = (book.availableCopies || 0) > 0;
                        const coverImg = book.coverImageUrl 
                            ? `<img src="${utils.escapeHtml(book.coverImageUrl)}" alt="${utils.escapeHtml(book.title)}" onerror="this.onerror=null; this.parentElement.innerHTML='📖';">` 
                            : '📖';

                        return `
                            <div class="book-card">
                                <div class="book-cover">${coverImg}</div>
                                <div class="book-body">
                                    <div class="badge ${isAvailable ? 'badge-success' : 'badge-danger'}" style="align-self: flex-start; margin-bottom: 0.5rem;">
                                        ${isAvailable ? `${book.availableCopies} available` : 'Out of stock'}
                                    </div>
                                    <h3 class="book-title" title="${utils.escapeHtml(book.title)}">${utils.escapeHtml(book.title)}</h3>
                                    <div class="book-author">by ${utils.escapeHtml(book.author)}</div>
                                    ${book.genreName ? `<span class="badge badge-secondary" style="align-self: flex-start; margin-bottom: 0.5rem;">${utils.escapeHtml(book.genreName)}</span>` : ''}
                                    <div class="book-meta">
                                        <span>ISBN: ${utils.escapeHtml(book.isbn)}</span>
                                        <span>Total: ${book.totalCopies || 0}</span>
                                    </div>
                                    <div class="book-actions">
                                        <button class="btn btn-outline btn-sm w-full" onclick="router.navigate('book-detail', { id: ${book.id} })">Details</button>
                                        ${isAvailable 
                                            ? `<button class="btn btn-primary btn-sm w-full" onclick="books.openBorrowModal(${book.id}, '${utils.escapeHtml(book.title.replace(/'/g, "\\'"))}')">Borrow</button>` 
                                            : `<button class="btn btn-secondary btn-sm w-full" onclick="books.showReservationPlaceholder('${utils.escapeHtml(book.title.replace(/'/g, "\\'"))}')">Reserve</button>`
                                        }
                                    </div>
                                </div>
                            </div>
                        `;
                    }).join('')}
                </div>
            `;

            // Render Pagination
            if (paginationContainer && pageData.totalPages > 1) {
                let pagHtml = `
                    <button class="page-btn" ${pageData.first ? 'disabled' : ''} onclick="books.changePage(${pageData.pageNumber - 1})">&laquo; Prev</button>
                `;
                for (let i = 0; i < pageData.totalPages; i++) {
                    if (i === 0 || i === pageData.totalPages - 1 || Math.abs(i - pageData.pageNumber) <= 2) {
                        pagHtml += `
                            <button class="page-btn ${i === pageData.pageNumber ? 'active' : ''}" onclick="books.changePage(${i})">${i + 1}</button>
                        `;
                    } else if (i === 1 || i === pageData.totalPages - 2) {
                        pagHtml += `<span style="padding: 0 0.25rem;">...</span>`;
                    }
                }
                pagHtml += `
                    <button class="page-btn" ${pageData.last ? 'disabled' : ''} onclick="books.changePage(${pageData.pageNumber + 1})">Next &raquo;</button>
                `;
                paginationContainer.innerHTML = pagHtml;
            } else if (paginationContainer) {
                paginationContainer.innerHTML = '';
            }

        } catch (e) {
            console.error('Failed to load books:', e);
            gridContainer.innerHTML = `<div class="card text-center" style="padding: 2rem;"><p class="text-muted">Failed to load books collection. ${utils.escapeHtml(e.message)}</p></div>`;
        }
    },

    changePage(newPage) {
        this.currentFilters.page = newPage;
        this.loadBooks();
        window.scrollTo({ top: 0, behavior: 'smooth' });
    },

    async renderDetail(params = {}) {
        const container = document.getElementById('app-content');
        if (!container) return;

        const bookId = params.id;
        if (!bookId) {
            router.navigate('books');
            return;
        }

        container.innerHTML = '<div class="spinner" style="margin: 3rem auto;"></div>';

        try {
            const book = await api.books.getById(bookId);
            const isAvailable = (book.availableCopies || 0) > 0;
            const isAdmin = api.isAdmin();

            container.innerHTML = `
                <div class="mb-4">
                    <a href="#books" class="btn btn-outline btn-sm">&larr; Back to Catalog</a>
                </div>

                <div class="card" style="padding: 2rem;">
                    <div style="display: flex; gap: 2rem; flex-wrap: wrap;">
                        <div style="width: 240px; height: 340px; background: #e2e8f0; border-radius: var(--radius-md); overflow: hidden; display: flex; align-items: center; justify-content: center; font-size: 4rem; color: #94a3b8; flex-shrink: 0;">
                            ${book.coverImageUrl ? `<img src="${utils.escapeHtml(book.coverImageUrl)}" style="width: 100%; height: 100%; object-fit: cover;">` : '📖'}
                        </div>

                        <div style="flex: 1 1 300px; display: flex; flex-direction: column;">
                            <div style="display: flex; gap: 0.5rem; align-items: center; margin-bottom: 0.5rem;">
                                <span class="badge ${isAvailable ? 'badge-success' : 'badge-danger'}">
                                    ${isAvailable ? `${book.availableCopies} Available` : 'Out of Stock'}
                                </span>
                                ${book.genreName ? `<span class="badge badge-secondary">${utils.escapeHtml(book.genreName)}</span>` : ''}
                                ${book.active ? `<span class="badge badge-info">Active</span>` : `<span class="badge badge-secondary">Inactive</span>`}
                            </div>

                            <h1 style="font-size: 1.8rem; font-weight: 700; margin-bottom: 0.35rem;">${utils.escapeHtml(book.title)}</h1>
                            <div style="font-size: 1.05rem; color: var(--text-secondary); margin-bottom: 1.25rem;">
                                by <strong>${utils.escapeHtml(book.author)}</strong>
                            </div>

                            <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(140px, 1fr)); gap: 1rem; background: var(--bg-main); padding: 1rem; border-radius: var(--radius-md); margin-bottom: 1.25rem; font-size: 0.85rem;">
                                <div><span class="text-muted">ISBN:</span> <div>${utils.escapeHtml(book.isbn)}</div></div>
                                <div><span class="text-muted">Publisher:</span> <div>${utils.escapeHtml(book.publisher || '-')}</div></div>
                                <div><span class="text-muted">Language:</span> <div>${utils.escapeHtml(book.language || 'English')}</div></div>
                                <div><span class="text-muted">Pages:</span> <div>${book.pages || '-'}</div></div>
                                <div><span class="text-muted">Total Copies:</span> <div>${book.totalCopies || 0}</div></div>
                                <div><span class="text-muted">Price:</span> <div>${book.price ? utils.formatCurrency(book.price) : '-'}</div></div>
                            </div>

                            <div style="margin-bottom: 1.5rem;">
                                <h3 style="font-size: 1rem; font-weight: 600; margin-bottom: 0.5rem;">Book Description</h3>
                                <p style="font-size: 0.9rem; color: var(--text-secondary); line-height: 1.6;">
                                    ${utils.escapeHtml(book.description || 'No description provided for this book.')}
                                </p>
                            </div>

                            <!-- Actions -->
                            <div style="display: flex; gap: 0.75rem; flex-wrap: wrap; margin-top: auto;">
                                ${isAvailable 
                                    ? `<button class="btn btn-primary btn-lg" onclick="books.openBorrowModal(${book.id}, '${utils.escapeHtml(book.title.replace(/'/g, "\\'"))}')">📖 Borrow Book</button>` 
                                    : `<button class="btn btn-secondary btn-lg" onclick="books.showReservationPlaceholder('${utils.escapeHtml(book.title.replace(/'/g, "\\'"))}')">📌 Reserve Book</button>`
                                }
                                <button class="btn btn-outline" onclick="books.showWishlistPlaceholder('${utils.escapeHtml(book.title.replace(/'/g, "\\'"))}')">🤍 Add to Wishlist</button>
                                <button class="btn btn-outline" onclick="books.showReviewPlaceholder('${utils.escapeHtml(book.title.replace(/'/g, "\\'"))}')">⭐ Reviews</button>
                                
                                ${isAdmin ? `
                                    <button class="btn btn-outline" style="border-color: #f59e0b; color: #b45309;" onclick="admin.openEditBookModal(${book.id})">✏️ Edit (Admin)</button>
                                ` : ''}
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Roadmap Notice for Book Detail -->
                ${utils.renderFutureFeatureNote('Book Reviews, Ratings, and Reservation Queue')}
            `;
        } catch (e) {
            console.error('Failed to load book detail:', e);
            container.innerHTML = `
                <div class="card text-center" style="padding: 2rem;">
                    <p class="text-danger">Failed to load book details. ${utils.escapeHtml(e.message)}</p>
                    <a href="#books" class="btn btn-outline btn-sm mt-2">&larr; Return to Catalog</a>
                </div>
            `;
        }
    },

    // Borrow Modal (calls POST /api/book-loans/checkout)
    openBorrowModal(bookId, bookTitle) {
        utils.showModal({
            title: `Borrow: ${bookTitle}`,
            bodyHtml: `
                <form id="borrow-form">
                    <p style="font-size: 0.9rem; color: var(--text-secondary); margin-bottom: 1.25rem;">
                        Due date will default to your subscription plan's maximum loan duration, or you can specify shorter checkout days below.
                    </p>
                    <div class="form-group">
                        <label class="form-label" for="borrow-days">Checkout Duration (Days)</label>
                        <input type="number" id="borrow-days" class="form-control" placeholder="Leave blank to use plan default" min="1" max="180">
                        <small class="text-muted">Optional: specify number of days you wish to borrow.</small>
                    </div>
                    <div class="form-group">
                        <label class="form-label" for="borrow-notes">Notes / Purpose</label>
                        <textarea id="borrow-notes" class="form-control" rows="2" placeholder="e.g. For academic research..."></textarea>
                    </div>
                </form>
            `,
            footerHtml: `
                <button class="btn btn-outline" onclick="utils.closeModal()">Cancel</button>
                <button class="btn btn-primary" id="confirm-borrow-btn">Confirm Checkout</button>
            `
        });

        const confirmBtn = document.getElementById('confirm-borrow-btn');
        if (confirmBtn) {
            confirmBtn.onclick = async () => {
                const daysInput = document.getElementById('borrow-days').value.trim();
                const notes = document.getElementById('borrow-notes').value.trim();

                const checkoutReq = {
                    bookId: bookId,
                    checkoutDays: daysInput ? parseInt(daysInput) : null,
                    notes: notes || undefined
                };

                try {
                    confirmBtn.disabled = true;
                    confirmBtn.textContent = 'Processing...';
                    const loan = await api.loans.checkout(checkoutReq);
                    utils.closeModal();
                    utils.toast(`Book loan created successfully! Due date: ${utils.formatDate(loan.dueDate)}`, 'success');
                    router.navigate('loans');
                } catch (err) {
                    utils.toast(err.message || 'Checkout failed. Ensure you have an active subscription.', 'error');
                } finally {
                    confirmBtn.disabled = false;
                    confirmBtn.textContent = 'Confirm Checkout';
                }
            };
        }
    },

    // Roadmap Placeholders
    showReservationPlaceholder(title) {
        utils.toast(`Reservation for "${title}" will be activated in the upcoming Book Reservation release.`, 'info');
    },

    showWishlistPlaceholder(title) {
        utils.toast(`"${title}" added to local wishlist preview (Wishlist Entity integration coming soon).`, 'info');
    },

    showReviewPlaceholder(title) {
        utils.toast(`Book reviews and ratings module for "${title}" is part of upcoming phase.`, 'info');
    }
};
