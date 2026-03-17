import { api, formatMoney, setupNav, showAlert, validateSession } from "/js/common.js";

const ordersEl = document.getElementById("orders");

async function loadOrders() {
    const auth = await validateSession();
    if (!auth) return;

    try {
        const orders = await api("/api/orders");
        if (!orders.length) {
            ordersEl.innerHTML = "<p class='empty'>No orders yet.</p>";
            return;
        }

        ordersEl.innerHTML = orders
            .map(
                (order) => `
                <article class="card">
                    <div class="row between center">
                        <h3>Order #${order.id}</h3>
                        <span class="status">${order.status}</span>
                    </div>
                    <p><strong>Total:</strong> ${formatMoney(order.totalAmount)}</p>
                    <p><strong>Date:</strong> ${new Date(order.orderDate).toLocaleString()}</p>
                    <p><strong>Pricing:</strong> ${order.appliedStrategy || "N/A"}</p>
                    <ul>
                        ${(order.items || []).map((item) => `<li>${item.productName} x ${item.quantity} (${formatMoney(item.subtotal)})</li>`).join("")}
                    </ul>
                </article>
            `
            )
            .join("");
    } catch (error) {
        showAlert(error.message, false);
    }
}

async function init() {
    setupNav();
    await loadOrders();
}

init();


