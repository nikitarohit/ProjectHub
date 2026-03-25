/**
 * auth.js — Session + JWT helper
 * Include in pages that still use auth.js
 * New pages use inline guard instead
 */

function getUser() {
    var raw = localStorage.getItem('user');
    if (!raw) { window.location.replace('login.html'); return null; }
    return JSON.parse(raw);
}

function getToken() {
    return localStorage.getItem('token');
}

function logout() {
    localStorage.removeItem('user');
    localStorage.removeItem('token');
    window.location.replace('login.html');
}

// ── Authenticated fetch ───────────────────────────────────────
// Use this instead of plain fetch() for all API calls
// Automatically adds JWT token to every request
function authFetch(url, options) {
    options = options || {};
    options.headers = options.headers || {};
    var token = getToken();
    if (token) {
        options.headers['Authorization'] = 'Bearer ' + token;
    }
    return fetch(url, options).then(function(res) {
        // If 401, token expired — logout
        if (res.status === 401) {
            localStorage.removeItem('user');
            localStorage.removeItem('token');
            window.location.replace('login.html');
        }
        return res;
    });
}

function requireAdmin() {
    var user = getUser();
    if (user && user.role !== 'admin') { window.location.replace('index.html'); return null; }
    return user;
}

function requireClient() {
    var user = getUser();
    if (user && user.role !== 'client') { window.location.replace('Admin.html'); return null; }
    return user;
}

function fillSidebarUser(user) {
    var nameEl = document.getElementById('sb-user-name');
    var roleEl = document.getElementById('sb-user-role');
    var avEl   = document.getElementById('sb-avatar');
    if (nameEl) nameEl.textContent = user.name  || 'User';
    if (roleEl) roleEl.textContent = user.role  || 'client';
    if (avEl)   avEl.textContent   = getInitials(user.name);
}

function getInitials(name) {
    if (!name) return '?';
    return name.trim().split(' ').map(function(w){return w[0];}).join('').slice(0,2).toUpperCase();
}

function formatDate(d) {
    if (!d) return '—';
    return new Date(d).toLocaleDateString('en-GB',{day:'numeric',month:'short',year:'numeric'});
}

function daysUntil(d) {
    if (!d) return null;
    return Math.ceil((new Date(d) - new Date()) / 86400000);
}

function formatTime(ts) {
    if (!ts) return '';
    return new Date(ts).toLocaleTimeString('en-GB',{hour:'2-digit',minute:'2-digit'});
}

function statusClass(s) {
    if (!s) return 'pending';
    var l = s.toLowerCase();
    if (l === 'active') return 'active';
    if (l === 'completed' || l === 'done') return 'completed';
    return 'pending';
}

function escapeHtml(text) {
    var d = document.createElement('div');
    d.appendChild(document.createTextNode(text || ''));
    return d.innerHTML;
}