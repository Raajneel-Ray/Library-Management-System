// UI Utilities, Toasts, Modals, Formatters

const utils = {
    // Toast Notification System
    toast(message, type = 'info', duration = 3500) {
        const container = document.getElementById('toast-container');
        if (!container) return;

        const toast = document.createElement('div');
        toast.className = `toast toast-${type}`;

        const iconMap = {
            success: '✅',
            error: '❌',
            warning: '⚠️',
            info: 'ℹ️'
        };

        toast.innerHTML = `
            <div style="display: flex; align-items: center; gap: 0.65rem;">
                <span>${iconMap[type] || 'ℹ️'}</span>
                <span>${utils.escapeHtml(message)}</span>
            </div>
            <button style="background: none; border: none; font-size: 1.1rem; cursor: pointer; color: #94a3b8;" title="Close">&times;</button>
        `;

        const closeBtn = toast.querySelector('button');
        closeBtn.onclick = () => {
            toast.remove();
        };

        container.appendChild(toast);

        setTimeout(() => {
            if (toast.parentElement) {
                toast.style.opacity = '0';
                toast.style.transition = 'opacity 0.3s ease';
                setTimeout(() => toast.remove(), 300);
            }
        }, duration);
    },

    // Modal System
    showModal({ title, bodyHtml, footerHtml = '', onClose = null }) {
        const overlay = document.getElementById('modal-overlay');
        const content = document.getElementById('modal-content');
        if (!overlay || !content) return;

        content.innerHTML = `
            <div class="modal-header">
                <h3 class="modal-title">${utils.escapeHtml(title)}</h3>
                <button class="modal-close" id="modal-close-btn">&times;</button>
            </div>
            <div class="modal-body">
                ${bodyHtml}
            </div>
            ${footerHtml ? `<div class="modal-footer">${footerHtml}</div>` : ''}
        `;

        overlay.classList.remove('hidden');

        const closeBtn = document.getElementById('modal-close-btn');
        const closeModal = () => {
            overlay.classList.add('hidden');
            content.innerHTML = '';
            if (onClose) onClose();
        };

        closeBtn.onclick = closeModal;
        overlay.onclick = (e) => {
            if (e.target === overlay) closeModal();
        };
    },

    closeModal() {
        const overlay = document.getElementById('modal-overlay');
        if (overlay) {
            overlay.classList.add('hidden');
            const content = document.getElementById('modal-content');
            if (content) content.innerHTML = '';
        }
    },

    // Date & Currency Formatters
    formatDate(dateStr) {
        if (!dateStr) return '-';
        try {
            const date = new Date(dateStr);
            return date.toLocaleDateString('en-US', {
                year: 'numeric',
                month: 'short',
                day: 'numeric'
            });
        } catch (e) {
            return dateStr;
        }
    },

    formatDateTime(dateTimeStr) {
        if (!dateTimeStr) return '-';
        try {
            const date = new Date(dateTimeStr);
            return date.toLocaleDateString('en-US', {
                year: 'numeric',
                month: 'short',
                day: 'numeric',
                hour: '2-digit',
                minute: '2-digit'
            });
        } catch (e) {
            return dateTimeStr;
        }
    },

    formatCurrency(amount, currency = 'INR') {
        if (amount === undefined || amount === null) return '-';
        return new Intl.NumberFormat('en-IN', {
            style: 'currency',
            currency: currency
        }).format(amount);
    },

    // Badges
    renderLoanStatusBadge(status) {
        const statusMap = {
            CHECKED_OUT: { label: 'Checked Out', cls: 'badge-info' },
            RETURNED: { label: 'Returned', cls: 'badge-success' },
            OVERDUE: { label: 'Overdue', cls: 'badge-danger' },
            LOST: { label: 'Lost', cls: 'badge-danger' },
            DAMAGED: { label: 'Damaged', cls: 'badge-warning' }
        };
        const item = statusMap[status] || { label: status || 'Unknown', cls: 'badge-secondary' };
        return `<span class="badge ${item.cls}">${item.label}</span>`;
    },

    renderFineStatusBadge(status) {
        const statusMap = {
            PENDING: { label: 'Pending Payment', cls: 'badge-warning' },
            PARTIALLY_PAID: { label: 'Partially Paid', cls: 'badge-info' },
            PAID: { label: 'Paid', cls: 'badge-success' },
            WAIVED: { label: 'Waived', cls: 'badge-secondary' }
        };
        const item = statusMap[status] || { label: status || 'Unknown', cls: 'badge-secondary' };
        return `<span class="badge ${item.cls}">${item.label}</span>`;
    },

    renderFineTypeBadge(type) {
        const typeMap = {
            OVERDUE: { label: 'Overdue Penalty', cls: 'badge-danger' },
            DAMAGE: { label: 'Book Damage', cls: 'badge-warning' },
            LOSS: { label: 'Lost Book', cls: 'badge-danger' },
            PROCESSING: { label: 'Processing Fee', cls: 'badge-info' }
        };
        const item = typeMap[type] || { label: type || 'Fine', cls: 'badge-secondary' };
        return `<span class="badge ${item.cls}">${item.label}</span>`;
    },

    renderActiveBadge(isActive) {
        return isActive
            ? `<span class="badge badge-success">Active</span>`
            : `<span class="badge badge-secondary">Inactive</span>`;
    },

    // HTML escape to prevent XSS
    escapeHtml(str) {
        if (str === null || str === undefined) return '';
        return String(str)
            .replace(/&/g, '&amp;')
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;')
            .replace(/"/g, '&quot;')
            .replace(/'/g, '&#039;');
    },

    // Placeholder badge for roadmap items requested by user
    renderFutureFeatureNote(featureName) {
        return `
            <div class="card mt-4" style="background: #fffbeb; border-color: #fef3c7;">
                <div style="display: flex; align-items: center; gap: 0.75rem;">
                    <span style="font-size: 1.5rem;">🚀</span>
                    <div>
                        <strong style="color: #92400e;">Upcoming Feature: ${utils.escapeHtml(featureName)}</strong>
                        <p style="font-size: 0.85rem; color: #b45309; margin-top: 0.2rem;">
                            This module is currently part of the backend development roadmap (Fine Entity, Book Reviews, Reservations & Wishlist). Full API hooks are prepared.
                        </p>
                    </div>
                </div>
            </div>
        `;
    }
};
