import { api, getCart, saveCart, setupNav, showAlert, formatMoney } from "/js/common.js";

const productsEl = document.getElementById("products");
const searchInput = document.getElementById("search-input");

function addToCart(product) {
    const cart = getCart();
    const existing = cart.find((item) => item.id === product.id);
    if (existing) {
        existing.quantity += 1;
    } else {
        cart.push({
            id: product.id,
            name: product.name,
            price: Number(product.price),
            quantity: 1,
            giftWrap: false,
            expressShipping: false
        });
    }
    saveCart(cart);
    setupNav();
    showAlert(`${product.name} added to cart.`);
}

function renderProducts(products) {
    if (!products.length) {
        productsEl.innerHTML = "<p class='empty'>No products found.</p>";
        return;
    }

    productsEl.innerHTML = products
        .map((product) => `
            <article class="card">
                <h3>${product.name}</h3>
                <p>${product.description || "No description"}</p>
                <p><strong>${formatMoney(product.price)}</strong></p>
                <p>Stock: ${product.stock}</p>
                <button class="primary" data-id="${product.id}">Add to Cart</button>
            </article>
        `)
        .join("");

    productsEl.querySelectorAll("button[data-id]").forEach((button) => {
        button.addEventListener("click", () => {
            const id = Number(button.getAttribute("data-id"));
            const product = products.find((p) => p.id === id);
            if (product) addToCart(product);
        });
    });
}

async function loadProducts(search = "") {
    try {
        const query = search ? `?search=${encodeURIComponent(search)}` : "";
        const page = await api(`/api/products${query}`);
        renderProducts(page.content || []);
    } catch (error) {
        showAlert(error.message, false);
    }
}

setupNav();
loadProducts();

if (searchInput) {
    searchInput.addEventListener("input", () => loadProducts(searchInput.value));
}

