/**
 * EventCart - Core Client JavaScript Library
 * Handles AJAX requests, form validation, notifications, and interactive UI states.
 */

const EventCart = (function () {
    'use strict';

    /**
     * Standard Fetch wrapper for asynchronous JSON/Form requests.
     */
    async function request(url, options = {}) {
        const defaultHeaders = {
            'X-Requested-With': 'XMLHttpRequest',
            'Accept': 'application/json'
        };

        const config = {
            ...options,
            headers: {
                ...defaultHeaders,
                ...(options.headers || {})
            }
        };

        try {
            const response = await fetch(url, config);
            const contentType = response.headers.get('content-type');

            let responseData;
            if (contentType && contentType.includes('application/json')) {
                responseData = await response.json();
            } else {
                responseData = await response.text();
            }

            if (!response.ok) {
                const message = (responseData && responseData.message)
                    ? responseData.message
                    : `Request failed with status ${response.status}`;
                throw new Error(message);
            }

            return responseData;
        } catch (error) {
            console.error('[EventCart Error]:', error);
            throw error;
        }
    }

    /**
     * Displays a dynamic floating toast notification.
     */
    function showNotification(message, type = 'info') {
        let container = document.getElementById('toast-container');
        if (!container) {
            container = document.createElement('div');
            container.id = 'toast-container';
            container.className = 'toast-container position-fixed bottom-0 end-0 p-3';
            container.style.zIndex = '1090';
            document.body.appendChild(container);
        }

        const bgClass = type === 'success' ? 'text-bg-success' :
                        type === 'error' ? 'text-bg-danger' :
                        type === 'warning' ? 'text-bg-warning' : 'text-bg-primary';

        const toastEl = document.createElement('div');
        toastEl.className = `toast align-items-center ${bgClass} border-0 shadow`;
        toastEl.setAttribute('role', 'alert');
        toastEl.setAttribute('aria-live', 'assertive');
        toastEl.setAttribute('aria-atomic', 'true');

        toastEl.innerHTML = `
            <div class="d-flex">
                <div class="toast-body">
                    ${message}
                </div>
                <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
            </div>
        `;

        container.appendChild(toastEl);
        if (window.bootstrap && window.bootstrap.Toast) {
            const toast = new bootstrap.Toast(toastEl, { delay: 4000 });
            toast.show();
            toastEl.addEventListener('hidden.bs.toast', () => toastEl.remove());
        } else {
            setTimeout(() => toastEl.remove(), 4000);
        }
    }

    /**
     * Form validation helper to validate fields before submit.
     */
    function initFormValidation(formSelector) {
        const form = document.querySelector(formSelector);
        if (!form) return;

        form.addEventListener('submit', function (event) {
            if (!form.checkValidity()) {
                event.preventDefault();
                event.stopPropagation();
            }
            form.classList.add('was-validated');
        }, false);
    }

    /**
     * Updates the global navbar cart count badge.
     */
    function updateCartBadge(count) {
        const badge = document.getElementById('cart-badge-count');
        if (badge) {
            const num = (count !== undefined && count !== null) ? count : 0;
            badge.textContent = num;
            badge.setAttribute('aria-label', num + ' items in cart');
        }
    }

    /**
     * Asynchronously refreshes the cart count from server.
     */
    async function refreshCartCount(contextPath = '') {
        try {
            const data = await request(`${contextPath}/cart/count`);
            if (data && data.success && data.data) {
                updateCartBadge(data.data.cartCount);
            }
        } catch (e) {
            // Background check failure is non-blocking
        }
    }

    // Public API
    return {
        request,
        showNotification,
        initFormValidation,
        updateCartBadge,
        refreshCartCount
    };
})();

// Auto-initialize standard UI behaviors on DOM load
document.addEventListener('DOMContentLoaded', () => {
    // Auto-dismiss alerts after 5 seconds
    const autoAlerts = document.querySelectorAll('.alert-dismissible');
    autoAlerts.forEach(alert => {
        setTimeout(() => {
            const bsAlert = bootstrap.Alert.getInstance(alert);
            if (bsAlert) {
                bsAlert.close();
            }
        }, 5000);
    });
});
