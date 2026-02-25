const API = "http://localhost:2027/manager/change-password";

function updatePassword() {
    const newPassword = document.getElementById("newPassword").value;
    const confirmPassword = document.getElementById("confirmPassword").value;
    const msg = document.getElementById("msg");

    if (newPassword !== confirmPassword) {
        msg.style.color = "red";
        msg.innerText = "Passwords do not match";
        return;
    }

    fetch(API, {
        method: "PUT",
        headers: {
            "Authorization": "Bearer " + localStorage.getItem("token"),
            "Content-Type": "application/json"
        },
        body: JSON.stringify({ newPassword })
    })
    .then(res => res.json())
    .then(data => {
        msg.style.color = "green";
        msg.innerText = data.message;

        setTimeout(() => {
            window.location.href = "manager-dashboard.html";
        }, 1500);
    })
    .catch(() => {
        msg.style.color = "red";
        msg.innerText = "Server error";
    });
}
