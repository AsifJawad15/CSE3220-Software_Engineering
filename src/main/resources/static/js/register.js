import { api, getAuth, setAuth, setupNav, showAlert } from "/js/common.js";

const form = document.getElementById("register-form");

setupNav();

if (getAuth()) {
    location.href = "/";
}

form.addEventListener("submit", async (event) => {
    event.preventDefault();

    const payload = {
        name: document.getElementById("name").value.trim(),
        email: document.getElementById("email").value.trim(),
        password: document.getElementById("password").value,
        phone: document.getElementById("phone").value.trim(),
        address: document.getElementById("address").value.trim()
    };

    try {
        const auth = await api("/api/auth/register", {
            method: "POST",
            body: JSON.stringify(payload)
        });
        setAuth(auth);
        location.href = "/";
    } catch (error) {
        showAlert(error.message, false);
    }
});

