import { api, formatMoney, getCart, saveCart, setupNav, showAlert, validateSession } from "/js/common.js";

const cartItemsEl = document.getElementById("cart-items");
const cartTotalEl = document.getElementById("cart-total");
const checkoutBtn = document.getElementById("checkout-btn");

function computeTotal(items) {
    return items.reduce((sum, item) => {
        const extras = (item.giftWrap ? 5 : 0) + (item.expressShipping ? 15 : 0);
        return sum + (item.price + extras) * item.quantity;
    }, 0);
}

function updateItem(id, updater) {
    const items = getCart();
    const target = items.find((item) => item.id === id);
    if (!target) return;
    updater(target);
    saveCart(items);
    renderCart();
    setupNav();
}

function removeItem(id) {
    const items = getCart().filter((item) => item.id !== id);
    saveCart(items);
    renderCart();
    setupNav();
}

function renderCart() {
    const items = getCart();
    if (!items.length) {
        cartItemsEl.innerHTML = "<p class='empty'>Your cart is empty. Go back to the catalog.</p>";
        cartTotalEl.textContent = "";
        return;
    }

    cartItemsEl.innerHTML = items
        .map((item) => `
            <article class="card compact">
                <div class="row between center">
                    <strong>${item.name}</strong>
                    <button class="danger" data-remove="${item.id}">Remove</button>
                </div>
                <p>${formatMoney(item.price)} each</p>
                <div class="row gap center wrap">
                    <label>Qty</label>
                    <input type="number" min="1" value="${item.quantity}" data-qty="${item.id}" class="qty-input">
                    <label><input type="checkbox" data-gift="${item.id}" ${item.giftWrap ? "checked" : ""}> Gift Wrap (+$5)</label>
                    <label><input type="checkbox" data-express="${item.id}" ${item.expressShipping ? "checked" : ""}> Express Shipping (+$15)</label>
                </div>
            </article>
        `)
        .join("");

    cartTotalEl.textContent = `Estimated total: ${formatMoney(computeTotal(items))}`;

    cartItemsEl.querySelectorAll("[data-remove]").forEach((button) => {
        button.addEventListener("click", () => removeItem(Number(button.getAttribute("data-remove"))));
    });

    cartItemsEl.querySelectorAll("[data-qty]").forEach((input) => {
        input.addEventListener("change", () => {
            const id = Number(input.getAttribute("data-qty"));
            const value = Math.max(1, Number(input.value || 1));
            updateItem(id, (item) => {
                item.quantity = value;
            });
        });
    });

    cartItemsEl.querySelectorAll("[data-gift]").forEach((input) => {
        input.addEventListener("change", () => {
            const id = Number(input.getAttribute("data-gift"));
            updateItem(id, (item) => {
                item.giftWrap = input.checked;
            });
        });
    });

    cartItemsEl.querySelectorAll("[data-express]").forEach((input) => {
        input.addEventListener("change", () => {
            const id = Number(input.getAttribute("data-express"));
            updateItem(id, (item) => {
                item.expressShipping = input.checked;
            });
        });
    });
}

checkoutBtn.addEventListener("click", async () => {
    const auth = await validateSession();
    if (!auth) return;

    const items = getCart();
    if (!items.length) {
        showAlert("Your cart is empty.", false);
        return;
    }

    const payload = {
        pricingStrategy: document.getElementById("pricing-strategy").value,
        items: items.map((item) => ({
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
        saveCart([]);
        setupNav();
        renderCart();
        showAlert(`Order #${order.id} placed successfully.`);
    } catch (error) {
        showAlert(error.message, false);
    }
});

setupNav();
renderCart();

validateSession().then((auth) => {
    if (!auth) return;
    setupNav();
});


