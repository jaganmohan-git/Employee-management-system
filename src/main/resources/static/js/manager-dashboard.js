const API_BASE = "http://localhost:2027/manager";
const token = localStorage.getItem("token");
const role = localStorage.getItem("role");

if (!token || role !== "MANAGER") { localStorage.clear(); window.location.href = "login.html"; }

function authHeader() { return { "Authorization": "Bearer " + token, "Content-Type": "application/json" }; }
function showSection(id) {
    document.querySelectorAll(".sidebar li").forEach(item => item.classList.remove("active"));
    const activeItem = document.querySelector(`.sidebar li[onclick*="${id}"]`);
    if (activeItem) activeItem.classList.add("active");
    document.querySelectorAll(".section").forEach(s => s.classList.remove("active"));
    document.getElementById(id).classList.add("active");
    if (id === 'dashboard') loadDashboard(); else if (id === 'employees') loadEmployees(); else if (id === 'duty') loadDuties(); else if (id === 'leaves') loadLeaves();
}
function logout() { localStorage.clear(); window.location.href = "login.html"; }

async function loadDashboard() {
    try {
        const res = await fetch(API_BASE + "/dashboard", { headers: authHeader() });
        if (!res.ok) { if (res.status === 403) { localStorage.clear(); window.location.href = "login.html"; return; } throw new Error(`HTTP error! status: ${res.status}`); }
        const data = await res.json();
        if (data.success) {
            document.getElementById('welcomeMessage').textContent = `Welcome, ${data.manager.name || 'Manager'}`;
            document.getElementById('employeeCount').textContent = data.employeeCount || 0;
            document.getElementById('activeEmployeeCount').textContent = data.activeEmployeeCount || 0;
            document.getElementById('pendingLeavesCount').textContent = data.pendingLeavesCount || 0;
            document.getElementById('tasksCount').textContent = data.tasksCount || 0;
            updateActivities(data.employees || []);
        } else showError(data.message || "Failed to load dashboard");
    } catch (err) { console.error("Error loading dashboard:", err); showError("Failed to load dashboard data"); }
}

function updateActivities(employees) {
    const table = document.getElementById('activitiesTable');
    if (!table) return;
    if (!employees || employees.length === 0) {
        table.innerHTML = `<tr><td colspan="4" class="no-data"><i>📊</i>No recent activities</td></tr>`; return;
    }
    let html = '';
    const recentEmployees = employees.slice(0, 5);
    recentEmployees.forEach(emp => {
        const statusClass = emp.accountstatus?.toLowerCase() || 'pending';
        const timeAgo = getRandomTimeAgo();
        html += `<tr><td><strong>${emp.name}</strong><br><small>${emp.department || 'No Department'}</small></td><td>${emp.accountstatus === 'ACTIVE' ? 'Logged in today' : 'Account pending'}</td><td>${timeAgo}</td><td><span class="status ${statusClass}">${emp.accountstatus || 'PENDING'}</span></td></tr>`;
    });
    table.innerHTML = html;
}
function getRandomTimeAgo() { const times = ['2 mins ago', '15 mins ago', '1 hour ago', '2 hours ago', 'Today']; return times[Math.floor(Math.random() * times.length)]; }

async function loadEmployees() {
    const table = document.getElementById('employeeTable');
    if (!table) return;
    table.innerHTML = `<tr><td colspan="7" class="loading"><div class="spinner"></div>Loading employees...</td></tr>`;
    try {
        const res = await fetch(API_BASE + "/employees", { headers: authHeader() });
        if (!res.ok) throw new Error(`HTTP error! status: ${res.status}`);
        const data = await res.json();
        if (data.success && data.employees) displayEmployees(data.employees); else table.innerHTML = `<tr><td colspan="7" class="no-data"><i>👥</i>${data.message || "No employees found"}</td></tr>`;
    } catch (err) {
        console.error("Error loading employees:", err);
        table.innerHTML = `<tr><td colspan="7" class="no-data" style="color: #dc3545;"><i>⚠️</i>Failed to load employees: ${err.message}</td></tr>`;
    }
}
function displayEmployees(employees) {
    const table = document.getElementById('employeeTable');
    if (!table || !employees || employees.length === 0) { table.innerHTML = `<tr><td colspan="7" class="no-data"><i>👥</i>No employees assigned to you</td></tr>`; return; }
    let html = '';
    employees.forEach(emp => {
        const statusClass = emp.accountstatus?.toLowerCase() || 'pending';
        const statusText = emp.accountstatus || 'PENDING';
        html += `<tr>
            <td>${emp.id || '-'}</td>
            <td><strong>${emp.name || '-'}</strong><br><small>${emp.designation || 'Employee'}</small></td>
            <td>${emp.email || '-'}</td>
            <td>${emp.department || '-'}</td>
            <td><span class="status ${statusClass}">${statusText}</span></td>
            <td>${emp.contact || '-'}</td>
            <td>
                <button onclick="viewEmployee(${emp.id})" class="btn-view">View</button>
                <button onclick="openAppraisalModal(${emp.id}, '${emp.name}')" class="btn-assign" style="background:#f39c12; margin-left:5px;">Rate</button>
                ${statusText === 'PENDING' ? `<button onclick="approveEmployee(${emp.id})" class="btn-assign">Approve</button>` : ''}
            </td>
        </tr>`;
    });
    table.innerHTML = html;
}
async function approveEmployee(employeeId) {
    if (confirm("Are you sure you want to approve this employee?")) {
        try {
            const res = await fetch(`${API_BASE}/approveemployee?empId=${employeeId}&status=ACTIVE`, { method: "PUT", headers: authHeader() });
            const data = await res.json();
            alert(data.message); loadEmployees(); loadDashboard();
        } catch (err) { alert("Error approving employee: " + err.message); }
    }
}
function viewEmployee(employeeId) { alert(`View employee details for ID: ${employeeId}`); }
function searchEmployees() {
    const searchTerm = document.getElementById('employeeSearch').value.toLowerCase();
    const rows = document.querySelectorAll('#employeeTable tr');
    rows.forEach(row => { if (row.cells.length > 1) { const name = row.cells[1]?.textContent?.toLowerCase() || ''; if (name.includes(searchTerm)) row.style.display = ''; else row.style.display = 'none'; } });
}

// ================= DUTY ASSIGNMENT =================
async function loadDuties() {
    const table = document.getElementById('dutyTable');
    if (!table) return;
    table.innerHTML = `<tr><td colspan="6" class="loading"><div class="spinner"></div>Loading duties...</td></tr>`;
    try {
        const res = await fetch(API_BASE + "/duties", { headers: authHeader() });
        if (res.ok) {
            const data = await res.json();
            if (data.success && data.duties && data.duties.length > 0) {
                let html = '';
                data.duties.forEach(duty => {
                    const statusClass = duty.status ? duty.status.toLowerCase() : 'pending';
                    html += `<tr><td>${duty.id}</td><td>${duty.employee ? duty.employee.name : 'Unknown'}</td><td>${duty.title || '-'}</td><td>${duty.description || '-'}</td><td>${duty.targetDate || '-'}</td><td><span class="status ${statusClass}">${duty.status || 'PENDING'}</span></td></tr>`;
                });
                table.innerHTML = html;
            } else { table.innerHTML = `<tr><td colspan="6" class="no-data"><i>📋</i>No duties assigned yet</td></tr>`; }
        } else { throw new Error("Failed to load duties"); }
    } catch (err) { console.error("Error loading duties:", err); table.innerHTML = `<tr><td colspan="6" class="no-data" style="color:red;">Failed to load duties</td></tr>`; }
}
async function openDutyModal() {
    document.getElementById("dutyModal").style.display = "flex";
    const select = document.getElementById("dutyEmployeeSelect");
    select.innerHTML = '<option value="">Loading employees...</option>';
    try {
        const res = await fetch(API_BASE + "/employees", { headers: authHeader() });
        const data = await res.json();
        if (data.success && data.employees) {
            let options = '<option value="">-- Select Employee --</option>';
            data.employees.forEach(emp => { if (emp.accountstatus === 'ACTIVE') { options += `<option value="${emp.id}">${emp.name} (${emp.designation})</option>`; } });
            select.innerHTML = options;
        } else { select.innerHTML = '<option value="">No employees found</option>'; }
    } catch (err) { select.innerHTML = '<option value="">Error loading employees</option>'; }
}
function closeDutyModal() { document.getElementById("dutyModal").style.display = "none"; }
async function assignDuty() {
    const empId = document.getElementById("dutyEmployeeSelect").value;
    const title = document.getElementById("dutyTitle").value;
    const desc = document.getElementById("dutyDesc").value;
    const date = document.getElementById("dutyDate").value;
    if (!empId || !title || !desc || !date) { alert("Please fill all fields"); return; }
    const duty = { title: title, description: desc, targetDate: date, status: "PENDING" };
    try {
        const res = await fetch(`${API_BASE}/assignduty?empId=${empId}`, { method: "POST", headers: authHeader(), body: JSON.stringify(duty) });
        const data = await res.json();
        if (data.success) { alert("Duty assigned successfully!"); closeDutyModal(); loadDuties(); } else { alert("Failed: " + data.message); }
    } catch (err) { alert("Error: " + err.message); }
}

async function loadLeaves() {
    try {
        const res = await fetch(API_BASE + "/leaves", { headers: authHeader() });
        if (res.ok) { const data = await res.json(); if (data.success && data.leaves) displayLeaves(data.leaves); }
    } catch (err) { console.error("Error loading leaves:", err); }
}
function displayLeaves(leaves) {
    const table = document.getElementById('leaveTable');
    if (!table || !leaves || leaves.length === 0) { table.innerHTML = `<tr><td colspan="6" class="no-data"><i>📝</i>No leave applications</td></tr>`; return; }
    let html = '';
    leaves.forEach(leave => {
        const statusClass = leave.status?.toLowerCase() || 'pending';
        // HTML construction omitted for brevity, verify in full file view if needed
        html += `<tr><td>${leave.employee?.name || 'Unknown'}</td><td>${leave.reason || 'Personal'}</td><td>${leave.startDate || '-'}</td><td>${leave.endDate || '-'}</td><td><span class="status ${statusClass}">${leave.status || 'PENDING'}</span></td><td>${leave.status === 'PENDING' ? `<button onclick="approveLeave(${leave.id})" class="btn-assign" style="margin-right: 5px;">Approve</button><button onclick="rejectLeave(${leave.id})" class="btn-view" style="background: #dc3545;">Reject</button>` : ''}</td></tr>`;
    });
    table.innerHTML = html;
}
async function updateLeaveStatus(leaveId, status) {
    try {
        const res = await fetch(`${API_BASE}/updateleavestatus?leaveid=${leaveId}&status=${status}`, { method: "PUT", headers: authHeader() });
        const data = await res.json(); alert(data.message); loadLeaves();
    } catch (err) { alert("Error updating leave: " + err.message); }
}
async function approveLeave(leaveId) { if (confirm("Approve this leave request?")) updateLeaveStatus(leaveId, "APPROVED"); }
async function rejectLeave(leaveId) { if (confirm("Reject this leave request?")) updateLeaveStatus(leaveId, "REJECTED"); }
function searchLeaves() { /* Implementation omitted */ }
function showError(message) { alert("Error: " + message); }
document.addEventListener("DOMContentLoaded", function () { loadDashboard(); setInterval(updateDateTime, 1000); });

// ================= APPRAISALS =================
let currentRating = 0;

function openAppraisalModal(empId, empName) {
    document.getElementById("appraisalModal").style.display = "flex";
    document.getElementById("appraisalEmpId").value = empId;
    document.querySelector("#appraisalModal h3").textContent = `Rate ${empName}`;
    setRating(0);
    document.getElementById("appraisalFeedback").value = "";
}

function closeAppraisalModal() {
    document.getElementById("appraisalModal").style.display = "none";
}

function setRating(rating) {
    currentRating = rating;
    document.getElementById("ratingValue").textContent = `${rating}/5`;
    for (let i = 1; i <= 5; i++) {
        const star = document.getElementById(`star${i}`);
        if (i <= rating) {
            star.style.color = "#f39c12"; // Gold
        } else {
            star.style.color = "#ccc"; // Grey
        }
    }
}

async function submitAppraisal() {
    const empId = document.getElementById("appraisalEmpId").value;
    const feedback = document.getElementById("appraisalFeedback").value;

    if (currentRating === 0) {
        alert("Please select a rating.");
        return;
    }

    const appraisal = {
        rating: currentRating,
        feedback: feedback || "No feedback provided"
    };

    try {
        const res = await fetch(`${API_BASE}/addappraisal?empId=${empId}`, {
            method: "POST",
            headers: authHeader(),
            body: JSON.stringify(appraisal)
        });
        const data = await res.json();
        if (res.ok) {
            alert("Appraisal submitted successfully!");
            closeAppraisalModal();
        } else {
            alert("Failed: " + (data.message || "Unknown error"));
        }
    } catch (err) {
        alert("Error: " + err.message);
    }
}