const API_BASE = "http://localhost:2027/admin";
const token = localStorage.getItem("token");
const role = localStorage.getItem("role");

// 🔐 SECURITY
if (!token || role !== "ADMIN") {
    localStorage.clear();
    window.location.href = "login.html";
}

function authHeader() {
    return {
        "Authorization": "Bearer " + token,
        "Content-Type": "application/json"
    };
}

// ================= UI =================
function showSection(id) {
    document.querySelectorAll(".section").forEach(s => s.classList.remove("active"));
    document.getElementById(id).classList.add("active");
}

function logout() {
    localStorage.clear();
    window.location.href = "login.html";
}

// ================= MODALS =================
function openManagerModal() {
    document.getElementById("managerModal").style.display = "flex";
    clearManagerForm();
}

function closeManagerModal() {
    document.getElementById("managerModal").style.display = "none";
}

function openEmployeeModal() {
    document.getElementById("employeeModal").style.display = "flex";
    clearEmployeeForm();
}

function closeEmployeeModal() {
    document.getElementById("employeeModal").style.display = "none";
}

function clearManagerForm() {
    document.getElementById("mName").value = "";
    document.getElementById("mEmail").value = "";
    document.getElementById("mDepartment").value = "";
    document.getElementById("mContact").value = "";
}

function clearEmployeeForm() {
    document.getElementById("eName").value = "";
    document.getElementById("eEmail").value = "";
    document.getElementById("eDept").value = "";
    document.getElementById("eContact").value = "";
    document.getElementById("eGender").value = "Male";
    document.getElementById("eAge").value = "25";
    document.getElementById("eDesignation").value = "Employee";
    document.getElementById("eSalary").value = "30000";
}

// ================= ADD MANAGER =================
async function addManager() {
    const manager = {
        name: document.getElementById("mName").value,
        email: document.getElementById("mEmail").value,
        department: document.getElementById("mDepartment").value,
        contact: document.getElementById("mContact").value
    };

    if (!manager.name || !manager.email || !manager.department || !manager.contact) {
        alert("Please fill all required fields");
        return;
    }

    try {
        const res = await fetch(API_BASE + "/addmanager", {
            method: "POST",
            headers: authHeader(),
            body: JSON.stringify(manager)
        });

        const data = await res.json();

        if (data.success) {
            alert(data.message); // "Manager added successfully. Credentials sent to email."
            closeManagerModal();
            loadManagers();
            loadCounts();
        } else {
            alert("Failed: " + data.message);
        }
    } catch (err) {
        alert("Error: " + err.message);
    }
}

// ================= ADD EMPLOYEE =================
async function addEmployee() {
    const employee = {
        name: document.getElementById("eName").value,
        email: document.getElementById("eEmail").value,
        department: document.getElementById("eDept").value,
        contact: document.getElementById("eContact").value,
        gender: document.getElementById("eGender").value,
        age: parseInt(document.getElementById("eAge").value),
        designation: document.getElementById("eDesignation").value,
        salary: parseFloat(document.getElementById("eSalary").value)
    };

    if (!employee.name || !employee.email || !employee.department || !employee.contact) {
        alert("Please fill all required fields");
        return;
    }

    try {
        const res = await fetch(API_BASE + "/addemployee", {
            method: "POST",
            headers: authHeader(),
            body: JSON.stringify(employee)
        });

        const data = await res.json();

        if (data.success) {
            alert(data.message); // "Employee added successfully. Credentials sent to email."
            closeEmployeeModal();
            loadEmployees();
            loadCounts();
        } else {
            alert("Failed: " + data.message);
        }
    } catch (err) {
        alert("Error: " + err.message);
    }
}

// ================= ASSIGN MANAGER =================
function assignManager(empId, empName) {
    const managerId = prompt(`Enter Manager ID to assign for ${empName}:`);
    if (!managerId || isNaN(managerId)) {
        alert("Please enter a valid Manager ID");
        return;
    }

    fetch(`${API_BASE}/assignmanager?empId=${empId}&managerId=${managerId}`, {
        method: "PUT",
        headers: authHeader()
    })
        .then(res => res.json())
        .then(data => {
            alert(data.message);
            loadEmployees();
        })
        .catch(err => {
            alert("Error: " + err);
        });
}

// ================= COUNTS =================
async function loadCounts() {
    try {
        const managerRes = await fetch(API_BASE + "/managercount", { headers: authHeader() });
        const employeeRes = await fetch(API_BASE + "/employeecount", { headers: authHeader() });

        if (managerRes.ok) {
            const data = await managerRes.json();
            document.getElementById("managerCount").innerText = data.count || 0;
        }

        if (employeeRes.ok) {
            const data = await employeeRes.json();
            document.getElementById("employeeCount").innerText = data.count || 0;
        }
    } catch (err) {
        console.error("Error loading counts:", err);
        document.getElementById("managerCount").innerText = 0;
        document.getElementById("employeeCount").innerText = 0;
    }
}

// ================= LOAD MANAGERS =================
async function loadManagers() {
    try {
        const res = await fetch(API_BASE + "/viewallmanagers", { headers: authHeader() });

        if (!res.ok) {
            if (res.status === 403) {
                localStorage.clear();
                window.location.href = "login.html";
                return;
            }
            throw new Error(`Failed to load managers: ${res.status}`);
        }

        const managers = await res.json();
        const table = document.getElementById("managerTable");

        if (!table) return;

        if (!managers || !Array.isArray(managers) || managers.length === 0) {
            table.innerHTML = `<tr><td colspan="6" style="text-align:center;">No managers found</td></tr>`;
            return;
        }

        let html = '';
        managers.forEach(m => {
            html += `
                <tr>
                    <td>${m.id || '-'}</td>
                    <td>${m.name || '-'}</td>
                    <td>${m.email || '-'}</td>
                    <td>${m.department || '-'}</td>
                    <td>${m.contact || '-'}</td>
                    <td>
                        <button onclick="deleteManager(${m.id}, '${m.name}')" class="btn-delete">
                            Delete
                        </button>
                    </td>
                </tr>
            `;
        });

        table.innerHTML = html;
    } catch (err) {
        console.error("Error loading managers:", err);
        const table = document.getElementById("managerTable");
        if (table) {
            table.innerHTML = `<tr><td colspan="6" style="text-align:center;color:red;">Error loading managers</td></tr>`;
        }
    }
}

// ================= LOAD EMPLOYEES =================
async function loadEmployees() {
    try {
        const res = await fetch(API_BASE + "/viewallemployees", { headers: authHeader() });

        if (!res.ok) {
            if (res.status === 403) {
                localStorage.clear();
                window.location.href = "login.html";
                return;
            }
            throw new Error(`Failed to load employees: ${res.status}`);
        }

        const employees = await res.json();
        const table = document.getElementById("employeeTable");

        if (!table) return;

        if (!employees || !Array.isArray(employees) || employees.length === 0) {
            table.innerHTML = `<tr><td colspan="8" style="text-align:center;">No employees found</td></tr>`;
            return;
        }

        let html = '';
        employees.forEach(e => {
            const statusClass = e.accountstatus?.toLowerCase() || 'pending';
            const managerName = e.manager ? e.manager.name : 'Not Assigned';

            html += `
                <tr>
                    <td>${e.id || '-'}</td>
                    <td>${e.name || '-'}</td>
                    <td>${e.email || '-'}</td>
                    <td>${e.department || '-'}</td>
                    <td><span class="status ${statusClass}">${e.accountstatus || 'PENDING'}</span></td>
                    <td>${managerName}</td>
                    <td>${e.contact || '-'}</td>
                    <td>
                        <button onclick="assignManager(${e.id}, '${e.name}')" class="btn-assign" 
                                ${e.accountstatus !== 'PENDING' ? 'disabled' : ''}>
                            Assign Manager
                        </button>
                        <button onclick="deleteEmployee(${e.id}, '${e.name}')" class="btn-delete">
                            Delete
                        </button>
                    </td>
                </tr>
            `;
        });

        table.innerHTML = html;
    } catch (err) {
        console.error("Error loading employees:", err);
        const table = document.getElementById("employeeTable");
        if (table) {
            table.innerHTML = `<tr><td colspan="8" style="text-align:center;color:red;">Error loading employees</td></tr>`;
        }
    }
}

// ================= DELETE MANAGER =================
async function deleteManager(id, name) {
    if (!confirm(`Are you sure you want to delete manager "${name}"?`)) return;

    try {
        const res = await fetch(`${API_BASE}/deletemanager?mid=${id}`, {
            method: "DELETE",
            headers: authHeader()
        });

        const data = await res.json();
        alert(data.message);
        loadManagers();
        loadCounts();
        loadEmployees();
    } catch (err) {
        alert("Error: " + err.message);
    }
}

// ================= DELETE EMPLOYEE =================
async function deleteEmployee(id, name) {
    if (!confirm(`Are you sure you want to delete employee "${name}"?`)) return;

    try {
        const res = await fetch(`${API_BASE}/deleteemployee?eid=${id}`, {
            method: "DELETE",
            headers: authHeader()
        });

        const data = await res.json();
        alert(data.message);
        loadEmployees();
        loadCounts();
    } catch (err) {
        alert("Error: " + err.message);
    }
}

// ================= GENERATE PAYSLIP =================
async function generatePayslip() {
    const empId = document.getElementById("pEmpId").value;
    const month = document.getElementById("pMonth").value;
    const year = document.getElementById("pYear").value;

    if (!empId || !month || !year) {
        alert("Please fill all fields");
        return;
    }

    try {
        const res = await fetch(`${API_BASE}/generatepayslip?empId=${empId}&month=${month}&year=${year}`, {
            method: "POST",
            headers: authHeader()
        });

        const data = await res.json();
        alert(data.message);
    } catch (err) {
        alert("Error: " + err.message);
    }
}

// ================= INITIALIZE =================
document.addEventListener("DOMContentLoaded", function () {
    console.log("Admin dashboard loaded");

    loadCounts();
    loadManagers();
    loadEmployees();

    // Add modal close listeners
    document.querySelectorAll('.modal').forEach(modal => {
        modal.addEventListener('click', function (e) {
            if (e.target === this) {
                this.style.display = 'none';
            }
        });
    });
});