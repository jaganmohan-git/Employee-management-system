package com.klef.fsad.sdp.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.klef.fsad.sdp.model.Duty;
import com.klef.fsad.sdp.model.Employee;
import com.klef.fsad.sdp.model.Leave;
import com.klef.fsad.sdp.security.JWTUtilizer;
import com.klef.fsad.sdp.services.EmployeeService;
import com.klef.fsad.sdp.services.LeaveService;
import com.klef.fsad.sdp.services.AppraisalService;
import com.klef.fsad.sdp.model.Appraisal;

@RestController
@RequestMapping("/employee")
@CrossOrigin("*")
public class EmployeeController {
    
    @Autowired private JWTUtilizer jwtService;
    @Autowired private EmployeeService employeeService;
    @Autowired private LeaveService leaveService;
    @Autowired private AppraisalService appraisalService;
    
    private boolean isAuthorized(String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) return false;
            String token = authHeader.substring(7);
            Map<String, String> data = jwtService.validateToken(token);
            return "200".equals(data.get("code")) && "EMPLOYEE".equalsIgnoreCase(data.get("role"));
        } catch (Exception e) { return false; }
    }
    
    private Long getEmployeeId(String authHeader) {
        try {
            String token = authHeader.substring(7);
            Map<String, String> data = jwtService.validateToken(token);
            return Long.parseLong(data.get("id"));
        } catch (Exception e) { return null; }
    }
    
    @GetMapping("/profile")
    public ResponseEntity<Map<String, Object>> viewProfile(@RequestHeader("Authorization") String authHeader) {
        if (!isAuthorized(authHeader)) return ResponseEntity.status(403).body(Map.of("message", "Access Denied"));
        Long id = getEmployeeId(authHeader);
        try {
            Employee emp = employeeService.findEmployeeById(id);
            Map<String, Object> safeEmp = new HashMap<>(); // Remove password
            safeEmp.put("id", emp.getId()); safeEmp.put("name", emp.getName()); safeEmp.put("email", emp.getEmail());
            safeEmp.put("department", emp.getDepartment()); safeEmp.put("salary", emp.getSalary());
            if (emp.getManager() != null) safeEmp.put("manager", emp.getManager().getName());
            return ResponseEntity.ok(Map.of("success", true, "employee", safeEmp));
        } catch (Exception e) { return ResponseEntity.status(500).body(Map.of("message", e.getMessage())); }
    }
    
    @GetMapping("/duties")
    public ResponseEntity<Map<String, Object>> viewDuties(@RequestHeader("Authorization") String authHeader) {
        if (!isAuthorized(authHeader)) return ResponseEntity.status(403).body(Map.of("message", "Access Denied"));
        try { return ResponseEntity.ok(Map.of("success", true, "duties", employeeService.viewAssignedDuties(getEmployeeId(authHeader)))); } 
        catch (Exception e) { return ResponseEntity.status(500).body(Map.of("message", e.getMessage())); }
    }
    
    @PostMapping("/applyleave")
    public ResponseEntity<Map<String, Object>> applyLeave(@RequestBody Leave leave, @RequestHeader("Authorization") String authHeader) {
        if (!isAuthorized(authHeader)) return ResponseEntity.status(403).body(Map.of("message", "Access Denied"));
        try { return ResponseEntity.ok(Map.of("success", true, "message", "Leave Applied", "leave", leaveService.applyLeaveByEmployee(leave, getEmployeeId(authHeader)))); }
        catch (Exception e) { return ResponseEntity.status(500).body(Map.of("message", e.getMessage())); }
    }

    @GetMapping("/leaves")
    public ResponseEntity<Map<String, Object>> viewLeaves(@RequestHeader("Authorization") String authHeader) {
        if (!isAuthorized(authHeader)) return ResponseEntity.status(403).body(Map.of("message", "Access Denied"));
        try { return ResponseEntity.ok(Map.of("success", true, "leaves", leaveService.viewLeavesByEmployee(getEmployeeId(authHeader)))); }
        catch (Exception e) { return ResponseEntity.status(500).body(Map.of("message", e.getMessage())); }
    }

    @PutMapping("/updateprofile")
    public ResponseEntity<Map<String, Object>> updateProfile(@RequestBody Employee employee, @RequestHeader("Authorization") String authHeader) {
        if (!isAuthorized(authHeader)) return ResponseEntity.status(403).body(Map.of("message", "Access Denied"));
        try {
            Employee existing = employeeService.findEmployeeById(getEmployeeId(authHeader));
            if (employee.getName() != null) existing.setName(employee.getName());
            if (employee.getEmail() != null) existing.setEmail(employee.getEmail());
            if (employee.getContact() != null) existing.setContact(employee.getContact());
            return ResponseEntity.ok(Map.of("success", true, "message", employeeService.updateEmployeeProfile(existing)));
        } catch (Exception e) { return ResponseEntity.status(500).body(Map.of("message", e.getMessage())); }
    }

    @PostMapping("/changepassword")
    public ResponseEntity<Map<String, Object>> changePassword(@RequestBody Map<String, String> pwd, @RequestHeader("Authorization") String authHeader) {
        if (!isAuthorized(authHeader)) return ResponseEntity.status(403).body(Map.of("message", "Access Denied"));
        try {
            boolean changed = employeeService.changePassword(employeeService.findEmployeeById(getEmployeeId(authHeader)), pwd.get("oldPassword"), pwd.get("newPassword"));
            return ResponseEntity.ok(Map.of("success", changed, "message", changed ? "Password Changed" : "Wrong Old Password"));
        } catch (Exception e) { return ResponseEntity.status(500).body(Map.of("message", e.getMessage())); }
    }
    @GetMapping("/viewappraisals")
    public ResponseEntity<Map<String, Object>> viewAppraisals(@RequestHeader("Authorization") String authHeader) {
        if (!isAuthorized(authHeader)) return ResponseEntity.status(403).body(Map.of("message", "Access Denied"));
        try {
            Long empId = getEmployeeId(authHeader);
            List<Appraisal> appraisals = appraisalService.viewAppraisalsByEmployee(empId);
            return ResponseEntity.ok(Map.of("success", true, "appraisals", appraisals));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", e.getMessage()));
        }
    }
    @Autowired private com.klef.fsad.sdp.services.DutyService dutyService;

    @PutMapping("/updateduty")
    public ResponseEntity<Map<String, String>> updateDutyStatus(@RequestParam("id") Long id, @RequestParam("status") String status, @RequestHeader("Authorization") String authHeader) {
        if (!isAuthorized(authHeader)) return ResponseEntity.status(403).body(Map.of("message", "Access Denied"));
        try {
            String result = dutyService.updateDutyStatus(id, status);
            return ResponseEntity.ok(Map.of("message", result));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", e.getMessage()));
        }
    }
    @Autowired private com.klef.fsad.sdp.services.AttendanceService attendanceService;

    @PostMapping("/attendance/clockin")
    public ResponseEntity<Map<String, String>> clockIn(@RequestHeader("Authorization") String authHeader) {
        if (!isAuthorized(authHeader)) return ResponseEntity.status(403).body(Map.of("message", "Access Denied"));
        return ResponseEntity.ok(Map.of("message", attendanceService.clockIn(getEmployeeId(authHeader))));
    }

    @PostMapping("/attendance/clockout")
    public ResponseEntity<Map<String, String>> clockOut(@RequestHeader("Authorization") String authHeader) {
        if (!isAuthorized(authHeader)) return ResponseEntity.status(403).body(Map.of("message", "Access Denied"));
        return ResponseEntity.ok(Map.of("message", attendanceService.clockOut(getEmployeeId(authHeader))));
    }

    @GetMapping("/attendance/status")
    public ResponseEntity<Map<String, Object>> getAttendanceStatus(@RequestHeader("Authorization") String authHeader) {
        if (!isAuthorized(authHeader)) return ResponseEntity.status(403).body(Map.of("message", "Access Denied"));
        com.klef.fsad.sdp.model.Attendance att = attendanceService.getAttendanceByDate(getEmployeeId(authHeader), java.time.LocalDate.now());
        boolean clockedIn = att != null;
        boolean clockedOut = att != null && att.getOutTime() != null;
        return ResponseEntity.ok(Map.of("clockedIn", clockedIn, "clockedOut", clockedOut, "inTime", clockedIn ? att.getInTime() : null));
    }
    @Autowired private com.klef.fsad.sdp.services.PayslipService payslipService;

    @GetMapping("/payslips")
    public ResponseEntity<Map<String, Object>> viewPayslips(@RequestHeader("Authorization") String authHeader) {
        if (!isAuthorized(authHeader)) return ResponseEntity.status(403).body(Map.of("message", "Access Denied"));
        try {
            return ResponseEntity.ok(Map.of("success", true, "payslips", payslipService.getPayslipsByEmployee(getEmployeeId(authHeader))));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", e.getMessage()));
        }
    }
}
