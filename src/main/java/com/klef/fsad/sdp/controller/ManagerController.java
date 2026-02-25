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
import com.klef.fsad.sdp.model.Manager;
import com.klef.fsad.sdp.security.JWTUtilizer;
import com.klef.fsad.sdp.services.DutyService;
import com.klef.fsad.sdp.services.LeaveService;
import com.klef.fsad.sdp.services.ManagerService;
import com.klef.fsad.sdp.services.AppraisalService;
import com.klef.fsad.sdp.services.EmployeeService;
import com.klef.fsad.sdp.model.Appraisal;

@RestController
@RequestMapping("/manager")
@CrossOrigin("*")
public class ManagerController {

    @Autowired private JWTUtilizer jwtUtilizer;
    @Autowired private ManagerService managerService;
    @Autowired private LeaveService leaveService;
    @Autowired private DutyService dutyService;
    @Autowired private AppraisalService appraisalService;
    @Autowired private EmployeeService employeeService;

    // ================= HELPER METHODS =================
    private boolean isManager(String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) return false;
            String token = authHeader.substring(7);
            Map<String, String> data = jwtUtilizer.validateToken(token);
            return "200".equals(data.get("code")) && "MANAGER".equalsIgnoreCase(data.get("role"));
        } catch (Exception e) { return false; }
    }

    private Long getManagerIdFromToken(String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) return null;
            String token = authHeader.substring(7);
            Map<String, String> data = jwtUtilizer.validateToken(token);
            if ("200".equals(data.get("code"))) return Long.parseLong(data.get("id"));
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    // ================= DASHBOARD ENDPOINT =================
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard(@RequestHeader("Authorization") String authHeader) {
        Map<String, Object> response = new HashMap<>();
        if (!isManager(authHeader)) return ResponseEntity.status(403).body(Map.of("success", false, "message", "Access Denied"));

        Long managerId = getManagerIdFromToken(authHeader);
        if (managerId == null) return ResponseEntity.status(401).body(Map.of("success", false, "message", "Invalid token"));

        try {
            Manager manager = managerService.getManagerById(managerId);
            if (manager == null) return ResponseEntity.status(404).body(Map.of("success", false, "message", "Manager not found"));

            List<Employee> employees = managerService.getEmployeesByManagerId(managerId);
            List<Leave> leaves = leaveService.viewLeavesByManager(managerId);

            Map<String, Object> managerData = new HashMap<>(); // Safe data (no password)
            managerData.put("id", manager.getId());
            managerData.put("name", manager.getName());
            managerData.put("email", manager.getEmail());
            managerData.put("department", manager.getDepartment());

            response.put("success", true);
            response.put("manager", managerData);
            response.put("employees", employees);
            response.put("employeeCount", employees.size());
            response.put("activeEmployeeCount", employees.stream().filter(e -> "ACTIVE".equals(e.getAccountstatus())).count());
            response.put("pendingLeavesCount", leaves.stream().filter(l -> "PENDING".equals(l.getStatus())).count());
            response.put("tasksCount", dutyService.viewDutiesAssignedByManager(managerId).size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("success", false, "message", "Error: " + e.getMessage()));
        }
    }

    // ================= VIEW EMPLOYEES =================
    @GetMapping("/employees")
    public ResponseEntity<Map<String, Object>> viewEmployees(@RequestHeader("Authorization") String authHeader) {
        if (!isManager(authHeader)) return ResponseEntity.status(403).body(Map.of("success", false, "message", "Access Denied"));
        Long managerId = getManagerIdFromToken(authHeader);
        
        try {
            List<Employee> employees = managerService.getEmployeesByManagerId(managerId);
            return ResponseEntity.ok(Map.of("success", true, "employees", employees));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("success", false, "message", "Error: " + e.getMessage()));
        }
    }
    
    // ================= APPROVE EMPLOYEE =================
    @PutMapping("/approveemployee")
    public ResponseEntity<Map<String, String>> approveEmployee(@RequestParam("empId") Long empId, @RequestParam("status") String status, @RequestHeader("Authorization") String authHeader) {
        if (!isManager(authHeader)) return ResponseEntity.status(403).body(Map.of("message", "Access Denied"));
        try {
            String result = managerService.approveEmployee(empId, status);
            return ResponseEntity.ok(Map.of("message", result));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Error: " + e.getMessage()));
        }
    }

    // ================= LEAVES =================
    @GetMapping("/leaves")
    public ResponseEntity<Map<String, Object>> viewLeaves(@RequestHeader("Authorization") String authHeader) {
        if (!isManager(authHeader)) return ResponseEntity.status(403).body(Map.of("success", false, "message", "Access Denied"));
        Long managerId = getManagerIdFromToken(authHeader);
        try {
            List<Leave> leaves = leaveService.viewLeavesByManager(managerId);
            return ResponseEntity.ok(Map.of("success", true, "leaves", leaves));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("success", false, "message", "Error: " + e.getMessage()));
        }
    }

    @PutMapping("/updateleavestatus")
    public ResponseEntity<Map<String, String>> updateLeave(@RequestParam("leaveid") Long leaveid, @RequestParam("status") String status, @RequestHeader("Authorization") String authHeader) {
        if (!isManager(authHeader)) return ResponseEntity.status(403).body(Map.of("message", "Access Denied"));
        try {
            String result = leaveService.updateLeaveStatus(leaveid, status);
            return ResponseEntity.ok(Map.of("message", result));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Error: " + e.getMessage()));
        }
    }

    // ================= DUTIES =================
    @PostMapping("/assignduty")
    public ResponseEntity<Map<String, Object>> assignDuty(@RequestBody Duty duty, @RequestParam("empId") Long empId, @RequestHeader("Authorization") String authHeader) {
        if (!isManager(authHeader)) return ResponseEntity.status(403).body(Map.of("success", false, "message", "Access Denied"));
        
        Long managerId = getManagerIdFromToken(authHeader);
        try {
           Duty assignedDuty = dutyService.assignDutyByManagerToEmployee(duty, empId, managerId);
           if (assignedDuty != null) {
               return ResponseEntity.ok(Map.of("success", true, "message", "Duty assigned successfully"));
           } else {
               return ResponseEntity.status(400).body(Map.of("success", false, "message", "Failed to assign duty"));
           }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("success", false, "message", "Error: " + e.getMessage()));
        }
    }

    @GetMapping("/duties")
    public ResponseEntity<Map<String, Object>> viewDuties(@RequestHeader("Authorization") String authHeader) {
        if (!isManager(authHeader)) return ResponseEntity.status(403).body(Map.of("success", false, "message", "Access Denied"));
        
        Long managerId = getManagerIdFromToken(authHeader);
        try {
            List<Duty> duties = dutyService.viewDutiesAssignedByManager(managerId);
            return ResponseEntity.ok(Map.of("success", true, "duties", duties));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("success", false, "message", "Error: " + e.getMessage()));
        }
    }

    @PostMapping("/addappraisal")
    public ResponseEntity<Map<String, String>> addAppraisal(@RequestBody Appraisal appraisal, @RequestParam("empId") Long empId, @RequestHeader("Authorization") String authHeader) {
        if (!isManager(authHeader)) return ResponseEntity.status(403).body(Map.of("message", "Access Denied"));
        
        Long managerId = getManagerIdFromToken(authHeader);
        try {
            Manager manager = managerService.getManagerById(managerId);
            Employee employee = employeeService.findEmployeeById(empId);
            
            appraisal.setManager(manager);
            appraisal.setEmployee(employee);
            
            String result = appraisalService.addAppraisal(appraisal);
            return ResponseEntity.ok(Map.of("message", result));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Error: " + e.getMessage()));
        }
    }
    
    @GetMapping("/viewappraisals")
    public ResponseEntity<Map<String, Object>> viewAppraisals(@RequestHeader("Authorization") String authHeader) {
        if (!isManager(authHeader)) return ResponseEntity.status(403).body(Map.of("success", false, "message", "Access Denied"));
        Long managerId = getManagerIdFromToken(authHeader);
        try {
            List<Appraisal> appraisals = appraisalService.viewAppraisalsByManager(managerId);
            return ResponseEntity.ok(Map.of("success", true, "appraisals", appraisals));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("success", false, "message", "Error: " + e.getMessage()));
        }
    }
}