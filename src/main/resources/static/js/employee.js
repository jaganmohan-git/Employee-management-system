// Check authentication
console.log("Employee Script Loaded");
const token = localStorage.getItem('token');
if (!token) {
    window.location.href = 'login.html';
}

// Global state
let currentSection = 'dashboard';

document.addEventListener('DOMContentLoaded', () => {
    console.log("DOM Content Loaded - Initializing Dashboard");
    loadProfile();
    loadDuties();
    loadLeaves();
    loadPerformance();
    loadAttendance();
    loadPayslips();
});

function showSection(sectionId) {
    // Hide all sections
    document.querySelectorAll('.section').forEach(s => s.classList.remove('active'));
    document.querySelectorAll('.sidebar li').forEach(l => l.classList.remove('active'));

    // Show target section
    document.getElementById(sectionId).classList.add('active');

    // Highlight sidebar
    const sidebarItem = Array.from(document.querySelectorAll('.sidebar li')).find(li => li.getAttribute('onclick').includes(sectionId));
    if (sidebarItem) sidebarItem.classList.add('active');

    currentSection = sectionId;
}

function logout() {
    localStorage.removeItem('token');
    window.location.href = 'login.html';
}

// API Calls
async function fetchWithAuth(url, options = {}) {
    const headers = {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json',
        ...options.headers
    };
    const response = await fetch(url, { ...options, headers });
    if (response.status === 401 || response.status === 403) {
        alert("Session expired. Please login again.");
        logout();
        return null;
    }
    return response.json();
}

async function loadProfile() {
    try {
        console.log("Loading Profile...");
        const data = await fetchWithAuth('/employee/profile');
        console.log("Profile Data:", data);
        if (data && data.success) {
            const emp = data.employee;
            document.getElementById('welcomeMessage').textContent = `Welcome, ${emp.name}`;
            document.getElementById('profileName').textContent = emp.name;
            document.getElementById('profileEmail').textContent = emp.email;
            document.getElementById('profileDept').textContent = emp.department;
            document.getElementById('profileManager').textContent = emp.manager || "Not Assigned";
        }
    } catch (error) {
        console.error("Error loading profile:", error);
        document.getElementById('welcomeMessage').textContent = "Error loading profile";
    }
}

async function loadDuties() {
    try {
        const data = await fetchWithAuth('/employee/duties');
        const tableBody = document.getElementById('dutiesTable');
        tableBody.innerHTML = '';

        if (data && data.success && data.duties.length > 0) {
            let pendingCount = 0;
            data.duties.forEach(duty => {
                if (duty.status === 'PENDING') pendingCount++;
                const row = `
                    <tr>
                        <td>${duty.id}</td>
                        <td>${duty.title}</td>
                        <td>${duty.description}</td>
                        <td>${duty.targetDate || 'N/A'}</td>
                        <td><span class="status ${duty.status === 'COMPLETED' ? 'active' : 'pending'}">${duty.status}</span></td>
                        <td>
                            ${duty.status !== 'COMPLETED' ? `<button onclick="markTaskCompleted(${duty.id})" style="background:#2ecc71; color:white; border:none; padding:5px 10px; border-radius:4px; cursor:pointer;">Mark Completed</button>` : '-'}
                        </td>
                    </tr>
                `;
                tableBody.innerHTML += row;
            });
            document.getElementById('pendingTasksCount').textContent = pendingCount;
        } else {
            tableBody.innerHTML = '<tr><td colspan="6" class="no-data"><i>📋</i>No tasks assigned yet</td></tr>';
            document.getElementById('pendingTasksCount').textContent = '0';
        }
    } catch (error) {
        console.error("Error loading duties:", error);
        document.getElementById('dutiesTable').innerHTML = '<tr><td colspan="6" style="color:red; text-align:center;">Error loading tasks</td></tr>';
    }
}

async function markTaskCompleted(dutyId) {
    if (confirm("Are you sure you want to mark this task as completed?")) {
        try {
            const res = await fetchWithAuth(`/employee/updateduty?id=${dutyId}&status=COMPLETED`, { method: 'PUT' });
            if (res && res.message) {
                alert(res.message);
                loadDuties();
            }
        } catch (err) {
            alert("Error updating task: " + err.message);
        }
    }
}

async function loadLeaves() {
    try {
        const data = await fetchWithAuth('/employee/leaves');
        const tableBody = document.getElementById('leavesTable');
        tableBody.innerHTML = '';

        // Identify most recent leave status
        let lastStatus = "No recent applications";

        if (data && data.success && data.leaves.length > 0) {
            // Sort leaves by id desc
            const sortedLeaves = data.leaves.reverse();
            lastStatus = sortedLeaves[0].status;

            sortedLeaves.forEach(leave => {
                let statusClass = 'pending';
                if (leave.status === 'ACCEPTED') statusClass = 'active';
                if (leave.status === 'REJECTED') statusClass = 'inactive';

                const row = `
                    <tr>
                        <td>${leave.type}</td>
                        <td>${leave.startDate}</td>
                        <td>${leave.endDate}</td>
                        <td>${leave.reason}</td>
                        <td><span class="status ${statusClass}">${leave.status}</span></td>
                    </tr>
                `;
                tableBody.innerHTML += row;
            });
        } else {
            tableBody.innerHTML = '<tr><td colspan="5" class="no-data"><i>📝</i>No leave history</td></tr>';
        }
        document.getElementById('leaveStatusMsg').textContent = lastStatus;
    } catch (error) {
        console.error("Error loading leaves:", error);
        document.getElementById('leavesTable').innerHTML = '<tr><td colspan="5" style="color:red; text-align:center;">Error loading leaves</td></tr>';
    }
}

// Modal Functions
function openLeaveModal() {
    document.getElementById('leaveModal').style.display = 'flex';
}

function closeLeaveModal() {
    document.getElementById('leaveModal').style.display = 'none';
}

async function submitLeave() {
    const type = document.getElementById('leaveType').value;
    const fromDate = document.getElementById('leaveFrom').value;
    const toDate = document.getElementById('leaveTo').value;
    const reason = document.getElementById('leaveReason').value;

    if (!fromDate || !toDate || !reason) {
        alert("Please fill all fields");
        return;
    }

    const leaveData = {
        type: type,
        startDate: fromDate, // Corrected to match Java field name
        endDate: toDate,     // Corrected to match Java field name
        reason: reason,
        status: "PENDING"
    };

    const response = await fetchWithAuth('/employee/applyleave', {
        method: 'POST',
        body: JSON.stringify(leaveData)
    });

    if (response && response.success) {
        alert("Leave applied successfully!");
        closeLeaveModal();
        loadLeaves(); // Refresh list
    } else {
        alert("Failed to apply leave: " + (response?.message || "Unknown error"));
    }
}

async function loadPerformance() {
    try {
        const data = await fetchWithAuth('/employee/viewappraisals');
        const tableBody = document.getElementById('performanceTable');
        tableBody.innerHTML = '';

        if (data && data.success && data.appraisals && data.appraisals.length > 0) {
            let totalRating = 0;
            data.appraisals.forEach(review => {
                totalRating += review.rating;
                // Generate star string
                const stars = "★".repeat(review.rating) + "☆".repeat(5 - review.rating);
                const row = `
                    <tr>
                        <td>${review.date}</td>
                        <td style="color: #f1c40f; font-size: 18px;">${stars}</td>
                        <td>${review.feedback}</td>
                        <td>${review.manager ? review.manager.name : 'Manager'}</td>
                    </tr>
                `;
                tableBody.innerHTML += row;
            });

            const avg = (totalRating / data.appraisals.length).toFixed(1);
            document.getElementById('avgRating').textContent = `${avg} / 5.0`;
        } else {
            tableBody.innerHTML = '<tr><td colspan="4" class="no-data"><i>⭐</i>No performance reviews yet</td></tr>';
            document.getElementById('avgRating').textContent = "0.0 / 5.0";
        }
    } catch (error) {
        console.error("Error loading performance:", error);
        document.getElementById('performanceTable').innerHTML = '<tr><td colspan="4" style="color:red; text-align:center;">Error loading performance</td></tr>';
    }
}

// Attendance Functions
async function loadAttendance() {
    try {
        const data = await fetchWithAuth('/employee/attendance/status');
        if (data) {
            const statusEl = document.getElementById('attendanceStatus');
            const inBtn = document.getElementById('clockInBtn');
            const outBtn = document.getElementById('clockOutBtn');

            if (data.clockedOut) {
                statusEl.innerHTML = "Day Complete<br><span style='font-size:14px; color:#7f8c8d'>See you tomorrow!</span>";
                inBtn.style.display = 'none';
                outBtn.style.display = 'none';
            } else if (data.clockedIn) {
                statusEl.innerHTML = `Present<br><span style='font-size:14px; color:#27ae60'>In: ${data.inTime}</span>`;
                inBtn.style.display = 'none';
                outBtn.style.display = 'flex';
            } else {
                statusEl.textContent = "Not Clocked In";
                inBtn.style.display = 'flex';
                outBtn.style.display = 'none';
            }
        }
    } catch (err) {
        console.error("Error loading attendance", err);
    }
}

async function clockIn() {
    const res = await fetchWithAuth('/employee/attendance/clockin', { method: 'POST' });
    if (res && res.message) {
        alert(res.message);
        loadAttendance();
    }
}

async function clockOut() {
    const res = await fetchWithAuth('/employee/attendance/clockout', { method: 'POST' });
    if (res && res.message) {
        alert(res.message);
        loadAttendance();
    }
}

// Payslips Logic
async function loadPayslips() {
    try {
        const data = await fetchWithAuth('/employee/payslips');
        const tableBody = document.getElementById('payslipsTable');

        if (data && data.success && data.payslips && data.payslips.length > 0) {
            let html = '';
            data.payslips.forEach(p => {
                html += `
                    <tr>
                        <td>${p.month}</td>
                        <td>${p.year}</td>
                        <td style="font-weight:bold; color:#27ae60;">₹${p.netSalary.toLocaleString()}</td>
                        <td>
                            <button class="primary-btn" style="padding:5px 10px; font-size:12px;" 
                                onclick='viewPayslipDetails(${JSON.stringify(p)})'>
                                View
                            </button>
                        </td>
                    </tr>
                `;
            });
            tableBody.innerHTML = html;
        } else {
            tableBody.innerHTML = '<tr><td colspan="4" class="no-data"><i>💰</i>No payslips found</td></tr>';
        }
    } catch (err) {
        console.error("Error loading payslips", err);
    }
}

function viewPayslipDetails(p) {
    const details = `
        <p><strong>Month:</strong> ${p.month} ${p.year}</p>
        <p><strong>Basic Salary:</strong> ₹${p.basicSalary.toLocaleString()}</p>
        <p><strong>HRA (20%):</strong> ₹${p.hra.toLocaleString()}</p>
        <p><strong>Deductions (5%):</strong> ₹${p.deductions.toLocaleString()}</p>
        <hr style="margin: 10px 0; border-top: 1px dashed #ccc;">
        <p style="font-size: 18px; color: #27ae60;"><strong>Net Pay:</strong> ₹${p.netSalary.toLocaleString()}</p>
    `;
    document.getElementById('payslipDetails').innerHTML = details;
    document.getElementById('payslipModal').style.display = 'flex';
}
