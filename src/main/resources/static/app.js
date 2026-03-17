const state = {
    token: null,
    user: null,
    mode: "login",
    products: [],
    cart: []
};

const alertsEl = document.getElementById("alerts");
const productsEl = document.getElementById("products");
const cartItemsEl = document.getElementById("cart-items");
const cartTotalEl = document.getElementById("cart-total");
const cartBtn = document.getElementById("cart-btn");
const userBadge = document.getElementById("user-badge");
const adminLink = document.getElementById("admin-link");
const logoutBtn = document.getElementById("logout-btn");
const authModal = document.getElementById("auth-modal");

const sections = {
    catalog: document.getElementById("catalog-section"),
    cart: document.getElementById("cart-section"),
    orders: document.getElementById("orders-section"),
    admin: document.getElementById("admin-section")
};

function showAlert(message, ok = true) {
    alertsEl.innerHTML = `<div class="notice ${ok ? "ok" : "err"}">${message}</div>`;
}

function readAuth() {
    const raw = localStorage.getItem("mm_auth");
    if (!raw) return;
    try {
        const parsed = JSON.parse(raw);
        state.token = parsed.token || null;
        state.user = parsed.user || null;
    } catch (e) {
        localStorage.removeItem("mm_auth");
    }
}

function storeAuth(authResponse) {
    state.token = authResponse.token;
    state.user = {
        email: authResponse.email,
        role: authResponse.role,
        name: authResponse.name
    };
    localStorage.setItem("mm_auth", JSON.stringify({ token: state.token, user: state.user }));
}

function clearAuth() {
    state.token = null;
    state.user = null;
    localStorage.removeItem("mm_auth");
}

async function api(path, options = {}) {
    const headers = {
        "Content-Type": "application/json",
        ...(options.headers || {})
    };
    if (state.token) {
        headers.Authorization = `Bearer ${state.token}`;
    }

    const response = await fetch(path, { ...options, headers });
    if (response.status === 401) {
        clearAuth();
        updateAuthUI();
        throw new Error("Unauthorized. Please log in again.");
    }

    const text = await response.text();
    const data = text ? JSON.parse(text) : null;
    if (!response.ok) {
        const message = data && data.message ? data.message : `Request failed: ${response.status}`;
        throw new Error(message);
    }
    return data;
}

function formatMoney(value) {
    const n = Number(value || 0);
    return `$${n.toFixed(2)}`;
}

function updateAuthUI() {
    const label = state.user ? `${state.user.name} (${state.user.role})` : "Guest";
    userBadge.textContent = label;
    const loggedIn = Boolean(state.user);
    document.getElementById("login-btn").classList.toggle("hidden", loggedIn);
    document.getElementById("register-btn").classList.toggle("hidden", loggedIn);
    logoutBtn.classList.toggle("hidden", !loggedIn);
    adminLink.classList.toggle("hidden", !(loggedIn && state.user.role === "ROLE_ADMIN"));
}

function cartTotal() {
    return state.cart.reduce((sum, item) => {
        const extras = (item.giftWrap ? 5 : 0) + (item.expressShipping ? 15 : 0);
        return sum + ((item.price + extras) * item.quantity);
    }, 0);
}

function renderCart() {
    cartBtn.textContent = `Cart (${state.cart.length})`;
    if (state.cart.length === 0) {
        cartItemsEl.innerHTML = "<p>Your cart is empty.</p>";
        cartTotalEl.textContent = "";
        return;
    }

    cartItemsEl.innerHTML = state.cart.map(item => `
        <div class="cart-item">
            <div class="row space-between center">
                <strong>${item.name}</strong>
                <button class="danger" onclick="removeFromCart(${item.id})">Remove</button>
            </div>
            <p>${formatMoney(item.price)} x ${item.quantity}</p>
            <label><input type="checkbox" ${item.giftWrap ? "checked" : ""} onchange="toggleExtra(${item.id}, 'giftWrap', this.checked)"> Gift wrap (+$5)</label>
            <label><input type="checkbox" ${item.expressShipping ? "checked" : ""} onchange="toggleExtra(${item.id}, 'expressShipping', this.checked)"> Express shipping (+$15)</label>
        </div>
    `).join("");
    cartTotalEl.textContent = `Estimated total: ${formatMoney(cartTotal())}`;
}

function addToCart(productId) {
    const found = state.products.find(p => p.id === productId);
    if (!found) return;
    const existing = state.cart.find(i => i.id === productId);
    if (existing) {
        existing.quantity += 1;
    } else {
        state.cart.push({
            id: found.id,
            name: found.name,
            price: Number(found.price),
            quantity: 1,
            giftWrap: false,
            expressShipping: false
        });
    }
    renderCart();
}

function removeFromCart(productId) {
    state.cart = state.cart.filter(i => i.id !== productId);
    renderCart();
}

function toggleExtra(productId, key, value) {
    const item = state.cart.find(i => i.id === productId);
    if (!item) return;
    item[key] = value;
    renderCart();
}

async function loadProducts(search = "") {
    try {
        const query = search ? `?search=${encodeURIComponent(search)}` : "";
        const page = await api(`/api/products${query}`);
        state.products = page.content || [];
        productsEl.innerHTML = state.products.map(p => `
            <article class="card">
                <h3>${p.name}</h3>
                <p>${p.description || "No description"}</p>
                <p><strong>${formatMoney(p.price)}</strong></p>
                <p>Stock: ${p.stock}</p>
                <button class="primary" onclick="addToCart(${p.id})">Add to cart</button>
                ${state.user && state.user.role === "ROLE_ADMIN" ? `<button class="secondary" onclick="editProduct(${p.id})">Edit</button><button class="danger" onclick="deleteProduct(${p.id})">Delete</button>` : ""}
            </article>
        `).join("");
    } catch (e) {
        showAlert(e.message, false);
    }
}

async function checkout() {
    if (!state.user) {
        showAlert("Log in before placing an order.", false);
        return;
    }
    if (state.cart.length === 0) {
        showAlert("Add products to cart first.", false);
        return;
    }

    const payload = {
        pricingStrategy: document.getElementById("strategy").value,
        items: state.cart.map(item => ({
            productId: item.id,
            quantity: item.quantity,
            giftWrap: item.giftWrap,
            expressShipping: item.expressShipping
        }))
    };

    try {
        const order = await api("/api/orders", {
            method: "POST",
            body: JSON.stringify(payload)
        });
        state.cart = [];
        renderCart();
        showAlert(`Order #${order.id} placed successfully.`);
        await loadOrders();
        showSection("orders");
    } catch (e) {
        showAlert(e.message, false);
    }
}

async function loadOrders() {
    const ordersEl = document.getElementById("orders");
    if (!state.user) {
        ordersEl.innerHTML = "<p>Please log in to view your orders.</p>";
        return;
    }

    try {
        const orders = await api("/api/orders");
        ordersEl.innerHTML = orders.length ? orders.map(order => `
            <article class="card order-item">
                <div class="row space-between center">
                    <strong>Order #${order.id}</strong>
                    <span>${order.status}</span>
                </div>
                <p>Total: ${formatMoney(order.totalAmount)}</p>
                <p>Date: ${new Date(order.orderDate).toLocaleString()}</p>
                <small>${(order.items || []).map(i => `${i.productName} x ${i.quantity}`).join(", ")}</small>
            </article>
        `).join("") : "<p>No orders yet.</p>";
    } catch (e) {
        showAlert(e.message, false);
    }
}

function showSection(name) {
    Object.keys(sections).forEach(key => {
        sections[key].classList.toggle("hidden", key !== name);
    });

    if (name === "orders") {
        loadOrders();
    }
    if (name === "admin") {
        loadAdminOrders();
    }
}

function openAuth(mode) {
    state.mode = mode;
    const register = mode === "register";
    document.getElementById("auth-title").textContent = register ? "Register" : "Login";
    document.getElementById("auth-name").classList.toggle("hidden", !register);
    document.getElementById("auth-phone").classList.toggle("hidden", !register);
    document.getElementById("auth-address").classList.toggle("hidden", !register);
    authModal.classList.remove("hidden");
}

function closeAuth() {
    authModal.classList.add("hidden");
    document.getElementById("auth-form").reset();
}

async function submitAuth(event) {
    event.preventDefault();
    const register = state.mode === "register";
    const body = register
        ? {
            name: document.getElementById("auth-name").value,
            email: document.getElementById("auth-email").value,
            password: document.getElementById("auth-password").value,
            phone: document.getElementById("auth-phone").value,
            address: document.getElementById("auth-address").value
        }
        : {
            email: document.getElementById("auth-email").value,
            password: document.getElementById("auth-password").value
        };

    try {
        const data = await api(register ? "/api/auth/register" : "/api/auth/login", {
            method: "POST",
            body: JSON.stringify(body)
        });
        storeAuth(data);
        updateAuthUI();
        closeAuth();
        showAlert(`${register ? "Registered" : "Logged in"} successfully.`);
        await loadProducts();
    } catch (e) {
        showAlert(e.message, false);
    }
}

function logout() {
    clearAuth();
    updateAuthUI();
    showAlert("Logged out.");
}

function resetProductForm() {
    document.getElementById("product-form").reset();
    document.getElementById("product-id").value = "";
}

async function editProduct(productId) {
    const p = state.products.find(x => x.id === productId);
    if (!p) return;
    showSection("admin");
    document.getElementById("product-id").value = p.id;
    document.getElementById("product-name").value = p.name;
    document.getElementById("product-price").value = p.price;
    document.getElementById("product-stock").value = p.stock;
    document.getElementById("product-description").value = p.description || "";
}

async function saveProduct(event) {
    event.preventDefault();
    if (!state.user || state.user.role !== "ROLE_ADMIN") {
        showAlert("Admin access required.", false);
        return;
    }

    const id = document.getElementById("product-id").value;
    const payload = {
        name: document.getElementById("product-name").value,
        price: Number(document.getElementById("product-price").value),
        stock: Number(document.getElementById("product-stock").value),
        description: document.getElementById("product-description").value
    };

    try {
        await api(id ? `/api/products/${id}` : "/api/products", {
            method: id ? "PUT" : "POST",
            body: JSON.stringify(payload)
        });
        resetProductForm();
        await loadProducts();
        showAlert(`Product ${id ? "updated" : "created"}.`);
    } catch (e) {
        showAlert(e.message, false);
    }
}

async function deleteProduct(productId) {
    if (!state.user || state.user.role !== "ROLE_ADMIN") return;
    try {
        await api(`/api/products/${productId}`, { method: "DELETE" });
        await loadProducts();
        showAlert("Product deleted.");
    } catch (e) {
        showAlert(e.message, false);
    }
}

async function loadAdminOrders() {
    const target = document.getElementById("admin-orders");
    if (!state.user || state.user.role !== "ROLE_ADMIN") {
        target.innerHTML = "<p>Admin only.</p>";
        return;
    }

    try {
        const orders = await api("/api/admin/orders");
        target.innerHTML = orders.length ? orders.map(order => `
            <article class="card order-item">
                <div class="row space-between center">
                    <strong>Order #${order.id}</strong>
                    <select onchange="updateOrderStatus(${order.id}, this.value)">
                        ${["PLACED", "CONFIRMED", "PROCESSING", "SHIPPED", "DELIVERED", "CANCELLED"].map(status => `<option value="${status}" ${order.status === status ? "selected" : ""}>${status}</option>`).join("")}
                    </select>
                </div>
                <p>Total: ${formatMoney(order.totalAmount)}</p>
                <small>${(order.items || []).map(i => `${i.productName} x ${i.quantity}`).join(", ")}</small>
            </article>
        `).join("") : "<p>No orders found.</p>";
    } catch (e) {
        showAlert(e.message, false);
    }
}

async function updateOrderStatus(id, status) {
    try {
        await api(`/api/admin/orders/${id}/status`, {
            method: "PATCH",
            body: JSON.stringify({ status })
        });
        showAlert(`Order #${id} status updated to ${status}.`);
    } catch (e) {
        showAlert(e.message, false);
        loadAdminOrders();
    }
}

function wireEvents() {
    document.querySelectorAll("[data-route]").forEach(btn => {
        btn.addEventListener("click", () => {
            const route = btn.getAttribute("data-route");
            if (route === "swagger") {
                window.location.href = "/swagger-ui.html";
                return;
            }
            if (route === "admin" && (!state.user || state.user.role !== "ROLE_ADMIN")) {
                showAlert("Admin access required.", false);
                return;
            }
            showSection(route);
        });
    });

    document.getElementById("search-input").addEventListener("input", e => loadProducts(e.target.value));
    document.getElementById("login-btn").addEventListener("click", () => openAuth("login"));
    document.getElementById("register-btn").addEventListener("click", () => openAuth("register"));
    document.getElementById("auth-cancel").addEventListener("click", closeAuth);
    document.getElementById("auth-form").addEventListener("submit", submitAuth);
    document.getElementById("checkout-btn").addEventListener("click", checkout);
    document.getElementById("logout-btn").addEventListener("click", logout);
    document.getElementById("cart-btn").addEventListener("click", () => showSection("cart"));
    document.getElementById("product-form").addEventListener("submit", saveProduct);
    document.getElementById("product-reset").addEventListener("click", resetProductForm);
}

window.addToCart = addToCart;
window.removeFromCart = removeFromCart;
window.toggleExtra = toggleExtra;
window.editProduct = editProduct;
window.deleteProduct = deleteProduct;
window.updateOrderStatus = updateOrderStatus;

async function init() {
    readAuth();
    updateAuthUI();
    wireEvents();
    await loadProducts();
    renderCart();
    showSection("catalog");
}

init();

