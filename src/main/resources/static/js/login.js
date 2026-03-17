import { api, getAuth, setAuth, setupNav, showAlert } from "/js/common.js";

const form = document.getElementById("login-form");
const redirectParam = new URLSearchParams(location.search).get("redirect") || "/";

setupNav();

if (getAuth()) {
    location.href = redirectParam;
}

form.addEventListener("submit", async (event) => {
    event.preventDefault();

    const payload = {
        email: document.getElementById("email").value.trim(),
        password: document.getElementById("password").value
    };

    try {
        const auth = await api("/api/auth/login", {
            method: "POST",
            body: JSON.stringify(payload)
        });
        setAuth(auth);
        location.href = redirectParam;
    } catch (error) {
        showAlert(error.message, false);
    }
});

