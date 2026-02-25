const API = "http://localhost:2027/auth"; // Changed from "/auth/checkapi"

function login() {
    const identifier = document.getElementById("username").value;
    const password = document.getElementById("password").value;

    fetch(API + "/login", {  // Changed from "/checklogin"
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ identifier, password })
    })
    .then(res => res.json())
    .then(data => {
        console.log("Login response:", data);

        if (!data.token) {
            alert(data.message || "Login failed");
            return;
        }

        // 🔥 SAVE TOKEN
        localStorage.clear();
        localStorage.setItem("token", data.token);
        localStorage.setItem("role", data.role);
        localStorage.setItem("id", data.id);
        localStorage.setItem("username", data.username);

        if (data.role === "ADMIN") {
            window.location.href = "admin-dashboard.html";
        }
        else if (data.role === "MANAGER") {
            localStorage.setItem("firstLogin", data.firstLogin);

            if (data.firstLogin === true) {
                window.location.href = "change-password.html";
            } else {
                window.location.href = "manager-dashboard.html";
            }
        }
        else if (data.role === "EMPLOYEE") {
            window.location.href = "employee-dashboard.html";
        }
    })
    .catch((err) => {
        console.error("Login error:", err);
        alert("Server error: " + err.message);
    });
}