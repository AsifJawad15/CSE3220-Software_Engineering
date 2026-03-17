const AUTH_KEY = "mm_auth";
const CART_KEY = "mm_cart";

export function getAuth() {
    const raw = localStorage.getItem(AUTH_KEY);
    if (!raw) return null;
    try {
        return JSON.parse(raw);
    } catch (error) {
        localStorage.removeItem(AUTH_KEY);
        return null;
    }
}

export function setAuth(payload) {
    const auth = {
        token: payload.token,
        user: {
            email: payload.email,
            role: payload.role,
            name: payload.name
        }
    };
    localStorage.setItem(AUTH_KEY, JSON.stringify(auth));
}

export function refreshAuthProfile(profile) {
    const auth = getAuth();
    if (!auth || !auth.token) return;
    setAuth({
        token: auth.token,
        email: profile.email,
        role: profile.role,
        name: profile.name
    });
}

export function clearAuth() {
    localStorage.removeItem(AUTH_KEY);
}

export function getCart() {
    const raw = localStorage.getItem(CART_KEY);
    if (!raw) return [];
    try {
        return JSON.parse(raw);
    } catch (error) {
        localStorage.removeItem(CART_KEY);
        return [];
    }
}

export function saveCart(items) {
    localStorage.setItem(CART_KEY, JSON.stringify(items));
}

export function cartCount() {
    return getCart().reduce((sum, item) => sum + Number(item.quantity || 0), 0);
}

export function formatMoney(value) {
    const n = Number(value || 0);
    return `$${n.toFixed(2)}`;
}

export async function api(path, options = {}) {
    const auth = getAuth();
    const headers = {
        ...(options.body ? { "Content-Type": "application/json" } : {}),
        ...(options.headers || {})
    };

    if (auth && auth.token) {
        headers.Authorization = `Bearer ${auth.token}`;
    }

    const response = await fetch(path, { ...options, headers });
    if (response.status === 401) {
        clearAuth();
        throw new Error("Session expired. Please login again.");
    }

    const text = await response.text();
    const data = text ? JSON.parse(text) : null;
    if (!response.ok) {
        throw new Error((data && data.message) || `Request failed: ${response.status}`);
    }

    return data;
}

export function showAlert(message, ok = true) {
    const target = document.getElementById("alerts");
    if (!target) return;
    target.innerHTML = `<div class="notice ${ok ? "ok" : "err"}">${message}</div>`;
}

export function setupNav() {
    const auth = getAuth();
    const userBadge = document.getElementById("user-badge");
    const loginLink = document.getElementById("login-link");
    const registerLink = document.getElementById("register-link");
    const logoutBtn = document.getElementById("logout-btn");
    const adminLink = document.getElementById("admin-link");
    const cartLink = document.getElementById("cart-link");

    if (userBadge) {
        userBadge.textContent = auth ? `${auth.user.name} (${auth.user.role})` : "Guest";
    }

    if (cartLink) {
        cartLink.textContent = `Cart (${cartCount()})`;
    }

    if (loginLink) loginLink.classList.toggle("hidden", Boolean(auth));
    if (registerLink) registerLink.classList.toggle("hidden", Boolean(auth));
    if (logoutBtn) logoutBtn.classList.toggle("hidden", !auth);
    if (adminLink) adminLink.classList.toggle("hidden", !(auth && auth.user.role === "ROLE_ADMIN"));

    if (logoutBtn) {
        logoutBtn.onclick = () => {
            clearAuth();
            location.href = "/";
        };
    }
}

export function requireLogin() {
    const auth = getAuth();
    if (!auth) {
        location.href = `/login?redirect=${encodeURIComponent(location.pathname)}`;
        return null;
    }
    return auth;
}

export function requireAdmin() {
    const auth = requireLogin();
    if (!auth) return null;
    if (auth.user.role !== "ROLE_ADMIN") {
        location.href = "/";
        return null;
    }
    return auth;
}

export async function validateSession(requiredRole = null) {
    const auth = getAuth();
    if (!auth || !auth.token) {
        const redirect = encodeURIComponent(location.pathname);
        location.href = `/login?redirect=${redirect}`;
        return null;
    }

    try {
        const profile = await api("/api/auth/me");
        refreshAuthProfile(profile);

        if (requiredRole && profile.role !== requiredRole) {
            showAlert("You do not have permission to access this page.", false);
            location.href = "/";
            return null;
        }

        return getAuth();
    } catch (error) {
        clearAuth();
        const redirect = encodeURIComponent(location.pathname);
        location.href = `/login?redirect=${redirect}`;
        return null;
    }
}


