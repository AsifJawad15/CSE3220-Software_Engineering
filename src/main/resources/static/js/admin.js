import { api, formatMoney, setupNav, showAlert, validateSession } from "/js/common.js";

const productsEl = document.getElementById("admin-products");
const ordersEl = document.getElementById("admin-orders");
const form = document.getElementById("product-form");
const searchInput = document.getElementById("inventory-search");
const prevBtn = document.getElementById("inventory-prev");
const nextBtn = document.getElementById("inventory-next");
const pageInfo = document.getElementById("inventory-page-info");

const statuses = ["PLACED", "CONFIRMED", "PROCESSING", "SHIPPED", "DELIVERED", "CANCELLED"];
let productsCache = [];
let currentPage = 0;
const pageSize = 8;
let totalPages = 1;

function resetForm() {
    form.reset();
    document.getElementById("product-id").value = "";
}

function fillForm(productId) {
    const product = productsCache.find((p) => p.id === productId);
    if (!product) return;
    document.getElementById("product-id").value = product.id;
    document.getElementById("name").value = product.name;
    document.getElementById("price").value = product.price;
    document.getElementById("stock").value = product.stock;
    document.getElementById("description").value = product.description || "";
}

async function loadProducts() {
    try {
        const search = (searchInput?.value || "").trim();
        const queryParts = [`page=${currentPage}`, `size=${pageSize}`];
        if (search) queryParts.push(`search=${encodeURIComponent(search)}`);
        const page = await api(`/api/products?${queryParts.join("&")}`);
        productsCache = page.content || [];
        totalPages = Math.max(page.totalPages || 1, 1);
        pageInfo.textContent = `Page ${currentPage + 1} of ${totalPages}`;
        prevBtn.disabled = currentPage <= 0;
        nextBtn.disabled = currentPage >= totalPages - 1;

        if (!productsCache.length) {
            productsEl.innerHTML = "<p class='empty'>No inventory items found.</p>";
            return;
        }

        productsEl.innerHTML = productsCache
            .map(
                (product) => `
                <article class="card compact">
                    <div class="row between center">
                        <strong>${product.name}</strong>
                        <div class="row gap">
                            <button class="secondary" data-edit="${product.id}">Edit</button>
                            <button class="danger" data-delete="${product.id}">Delete</button>
                        </div>
                    </div>
                    <p>${formatMoney(product.price)} | stock ${product.stock}</p>
                    <div class="row gap">
                        <button class="secondary" data-stock-minus="${product.id}">-1 stock</button>
                        <button class="secondary" data-stock-plus="${product.id}">+1 stock</button>
                    </div>
                </article>
            `
            )
            .join("");

        productsEl.querySelectorAll("[data-edit]").forEach((button) => {
            button.addEventListener("click", () => fillForm(Number(button.getAttribute("data-edit"))));
        });

        productsEl.querySelectorAll("[data-delete]").forEach((button) => {
            button.addEventListener("click", async () => {
                const id = Number(button.getAttribute("data-delete"));
                if (!confirm("Delete this inventory item?")) return;
                try {
                    await api(`/api/products/${id}`, { method: "DELETE" });
                    showAlert("Product deleted.");
                    await loadProducts();
                } catch (error) {
                    showAlert(error.message, false);
                }
            });
        });

        productsEl.querySelectorAll("[data-stock-minus]").forEach((button) => {
            button.addEventListener("click", () => adjustStock(Number(button.getAttribute("data-stock-minus")), -1));
        });

        productsEl.querySelectorAll("[data-stock-plus]").forEach((button) => {
            button.addEventListener("click", () => adjustStock(Number(button.getAttribute("data-stock-plus")), 1));
        });
    } catch (error) {
        showAlert(error.message, false);
    }
}

async function adjustStock(productId, delta) {
    const product = productsCache.find((p) => p.id === productId);
    if (!product) return;

    const nextStock = Math.max(0, Number(product.stock) + delta);
    const payload = {
        name: product.name,
        price: Number(product.price),
        stock: nextStock,
        description: product.description || ""
    };

    try {
        await api(`/api/products/${productId}`, {
            method: "PUT",
            body: JSON.stringify(payload)
        });
        showAlert(`Stock updated to ${nextStock}.`);
        await loadProducts();
    } catch (error) {
        showAlert(error.message, false);
    }
}

async function loadOrders() {
    try {
        const orders = await api("/api/admin/orders");
        if (!orders.length) {
            ordersEl.innerHTML = "<p class='empty'>No orders found.</p>";
            return;
        }
        ordersEl.innerHTML = orders
            .map(
                (order) => `
                <article class="card compact">
                    <div class="row between center">
                        <strong>Order #${order.id}</strong>
                        <select data-status="${order.id}">
                            ${statuses
                                .map((status) => `<option value="${status}" ${order.status === status ? "selected" : ""}>${status}</option>`)
                                .join("")}
                        </select>
                    </div>
                    <p>${formatMoney(order.totalAmount)}</p>
                </article>
            `
            )
            .join("");

        ordersEl.querySelectorAll("[data-status]").forEach((select) => {
            select.addEventListener("change", async () => {
                const id = Number(select.getAttribute("data-status"));
                try {
                    await api(`/api/admin/orders/${id}/status`, {
                        method: "PATCH",
                        body: JSON.stringify({ status: select.value })
                    });
                    showAlert(`Order #${id} updated to ${select.value}.`);
                } catch (error) {
                    showAlert(error.message, false);
                    await loadOrders();
                }
            });
        });
    } catch (error) {
        showAlert(error.message, false);
    }
}

form.addEventListener("submit", async (event) => {
    event.preventDefault();

    const id = document.getElementById("product-id").value;
    const payload = {
        name: document.getElementById("name").value.trim(),
        price: Number(document.getElementById("price").value),
        stock: Number(document.getElementById("stock").value),
        description: document.getElementById("description").value.trim()
    };

    try {
        await api(id ? `/api/products/${id}` : "/api/products", {
            method: id ? "PUT" : "POST",
            body: JSON.stringify(payload)
        });
        showAlert(id ? "Product updated." : "Product created.");
        resetForm();
        await loadProducts();
    } catch (error) {
        showAlert(error.message, false);
    }
});

document.getElementById("clear-btn").addEventListener("click", resetForm);

searchInput.addEventListener("input", async () => {
    currentPage = 0;
    await loadProducts();
});

prevBtn.addEventListener("click", async () => {
    if (currentPage <= 0) return;
    currentPage -= 1;
    await loadProducts();
});

nextBtn.addEventListener("click", async () => {
    if (currentPage >= totalPages - 1) return;
    currentPage += 1;
    await loadProducts();
});

async function init() {
    const auth = await validateSession("ROLE_ADMIN");
    if (!auth) return;
    setupNav();
    await loadProducts();
    await loadOrders();
}

init();


