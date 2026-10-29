/**
 * ReTech - Shared Application Core Module
 * Provides API client with CSRF handling, session management, UI utilities, and navigation synchronization.
 */

const ReTech = (() => {
  // --- COOKIE & CSRF HELPERS ---
  function getCookie(name) {
    const value = `; ${document.cookie}`;
    const parts = value.split(`; ${name}=`);
    if (parts.length === 2) return parts.pop().split(';').shift();
    return null;
  }

  // --- API CLIENT ---
  async function api(endpoint, options = {}) {
    const url = endpoint.startsWith('/') ? endpoint : `/${endpoint}`;
    const defaultHeaders = {
      'Content-Type': 'application/json',
      'Accept': 'application/json'
    };

    // Attach CSRF token if mutating request (POST, PUT, PATCH, DELETE)
    const method = (options.method || 'GET').toUpperCase();
    if (method !== 'GET' && method !== 'HEAD') {
      const xsrfToken = getCookie('XSRF-TOKEN');
      if (xsrfToken) {
        defaultHeaders['X-XSRF-TOKEN'] = xsrfToken;
      }
    }

    const config = {
      ...options,
      headers: {
        ...defaultHeaders,
        ...(options.headers || {})
      },
      credentials: 'same-origin'
    };

    try {
      const res = await fetch(url, config);
      if (res.status === 401 && !url.includes('/api/auth/')) {
        // Session expired or unauthenticated
        sessionStorage.removeItem('retech_user');
        window.location.href = '/login.html?expired=true';
        throw new Error('Session expired. Please sign in again.');
      }
      if (res.status === 403 && !url.includes('/api/auth/')) {
        window.location.href = '/403.html';
        throw new Error('Access denied. Insufficient permissions.');
      }

      const contentType = res.headers.get('content-type') || '';
      if (contentType.includes('text/csv')) {
        return res.blob();
      }

      const data = await res.json();
      if (!res.ok) {
        const errorMsg = data.message || (data.validationErrors ? Object.values(data.validationErrors).join(', ') : 'Request failed');
        const error = new Error(errorMsg);
        error.status = res.status;
        error.data = data;
        throw error;
      }
      return data;
    } catch (err) {
      console.error('API Error:', err);
      throw err;
    }
  }

  // --- SESSION & AUTH ---
  let currentUser = null;

  async function initAuth() {
    try {
      const res = await api('/api/auth/me');
      if (res && res.success && res.data) {
        currentUser = res.data;
        sessionStorage.setItem('retech_user', JSON.stringify(currentUser));
      } else {
        currentUser = null;
        sessionStorage.removeItem('retech_user');
      }
    } catch (e) {
      currentUser = null;
      sessionStorage.removeItem('retech_user');
    }
    updateNav();
    return currentUser;
  }

  function getCachedUser() {
    if (currentUser) return currentUser;
    const cached = sessionStorage.getItem('retech_user');
    if (cached) {
      try {
        currentUser = JSON.parse(cached);
        return currentUser;
      } catch (e) {
        return null;
      }
    }
    return null;
  }

  async function logout() {
    try {
      await api('/api/auth/logout', { method: 'POST' });
    } catch (e) {
      console.warn('Logout API error:', e);
    } finally {
      currentUser = null;
      sessionStorage.removeItem('retech_user');
      window.location.href = '/login.html?logged_out=true';
    }
  }

  function requireRole(allowedRoles = []) {
    const user = getCachedUser();
    if (!user) {
      window.location.href = `/login.html?redirect=${encodeURIComponent(window.location.pathname)}`;
      return false;
    }
    if (allowedRoles.length > 0 && !allowedRoles.includes(user.role)) {
      window.location.href = '/403.html';
      return false;
    }
    return true;
  }

  // --- NAVIGATION RENDERING ---
  function updateNav() {
    const user = getCachedUser();
    const navUserContainer = document.getElementById('nav-user-container');
    const navLinks = document.getElementById('nav-links');

    if (!navUserContainer) return;

    if (user) {
      // Role display & route
      let dashboardUrl = '/dashboard.html';
      if (user.role === 'ROLE_AGENT') dashboardUrl = '/agent.html';
      if (user.role === 'ROLE_OPERATOR') dashboardUrl = '/facility.html';
      if (user.role === 'ROLE_ADMIN') dashboardUrl = '/admin.html';

      navUserContainer.innerHTML = `
        <div class="user-badge">
          <span>${escapeHtml(user.fullName)}</span>
          <span class="role-pill">${escapeHtml(user.roleDisplayName || user.role.replace('ROLE_', ''))}</span>
        </div>
        <button id="nav-logout-btn" class="btn btn-secondary btn-sm" title="Sign Out">Sign Out</button>
      `;

      document.getElementById('nav-logout-btn')?.addEventListener('click', logout);

      // If on landing page or elsewhere, add Portal button
      if (navLinks) {
        const portalLink = document.getElementById('nav-portal-link');
        if (!portalLink) {
          const li = document.createElement('li');
          li.innerHTML = `<a id="nav-portal-link" href="${dashboardUrl}">My Portal</a>`;
          navLinks.appendChild(li);
        }
      }
    } else {
      navUserContainer.innerHTML = `
        <a href="/login.html" class="btn btn-outline btn-sm">Sign In</a>
        <a href="/register.html" class="btn btn-primary btn-sm">Register</a>
      `;
    }
  }

  // --- UI TOAST & MODAL UTILITIES ---
  function showToast(message, type = 'info') {
    let container = document.getElementById('toast-container');
    if (!container) {
      container = document.createElement('div');
      container.id = 'toast-container';
      container.className = 'toast-container';
      document.body.appendChild(container);
    }

    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;
    toast.innerHTML = `
      <span>${escapeHtml(message)}</span>
      <button style="background:none;border:none;cursor:pointer;color:#888;font-size:1.1rem">&times;</button>
    `;

    toast.querySelector('button').addEventListener('click', () => toast.remove());
    container.appendChild(toast);

    setTimeout(() => {
      toast.style.opacity = '0';
      toast.style.transform = 'translateX(100%)';
      toast.style.transition = 'all 0.3s ease';
      setTimeout(() => toast.remove(), 300);
    }, 4500);
  }

  function openModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) modal.classList.add('active');
  }

  function closeModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) modal.classList.remove('active');
  }

  function escapeHtml(str) {
    if (str === null || str === undefined) return '';
    return String(str)
      .replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;')
      .replace(/"/g, '&quot;')
      .replace(/'/g, '&#39;');
  }

  function formatDate(dateStr) {
    if (!dateStr) return 'N/A';
    try {
      const d = new Date(dateStr);
      return d.toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric'
      });
    } catch (e) {
      return dateStr;
    }
  }

  function formatDateTime(dateTimeStr) {
    if (!dateTimeStr) return 'N/A';
    try {
      const d = new Date(dateTimeStr);
      return d.toLocaleString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
      });
    } catch (e) {
      return dateTimeStr;
    }
  }

  function getStatusBadge(status, label) {
    const text = label || status.replace(/_/g, ' ');
    return `<span class="status-pill status-${status}">${escapeHtml(text)}</span>`;
  }

  // Auto-init on DOMContentLoaded
  document.addEventListener('DOMContentLoaded', () => {
    initAuth();

    // Close modals when clicking backdrop or close buttons
    document.querySelectorAll('.modal-overlay').forEach(modal => {
      modal.addEventListener('click', (e) => {
        if (e.target === modal) modal.classList.remove('active');
      });
      modal.querySelectorAll('.modal-close, [data-modal-close]').forEach(btn => {
        btn.addEventListener('click', () => modal.classList.remove('active'));
      });
    });
  });

  return {
    api,
    initAuth,
    getCachedUser,
    logout,
    requireRole,
    updateNav,
    showToast,
    openModal,
    closeModal,
    escapeHtml,
    formatDate,
    formatDateTime,
    getStatusBadge
  };
})();
