package com.klef.fsad.sdp.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.klef.fsad.sdp.model.Employee;
import com.klef.fsad.sdp.model.Manager;
import com.klef.fsad.sdp.security.JWTUtilizer;
import com.klef.fsad.sdp.services.AdminService;
import com.klef.fsad.sdp.services.EmailService;

@RestController
@RequestMapping("/admin")
@CrossOrigin("*")
public class AdminController {
    
    @Autowired private AdminService adminService;
    @Autowired private JWTUtilizer jwtService;
    @Autowired private EmailService emailService;
    
    private boolean isAdmin(String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) return false;
            String token = authHeader.substring(7);
            Map<String, String> data = jwtService.validateToken(token);
            return "200".equals(data.get("code")) && "ADMIN".equalsIgnoreCase(data.get("role"));
        } catch (Exception e) { return false; }
    }
    
    // ================= ADD MANAGER =================
    @PostMapping("/addmanager")
    public ResponseEntity<Map<String, Object>> addManager(@RequestBody Manager manager, @RequestHeader("Authorization") String authHeader) {
        Map<String, Object> response = new HashMap<>();
        if (!isAdmin(authHeader)) return ResponseEntity.status(403).body(Map.of("message", "Access Denied"));
        
        try {
            Manager savedManager = adminService.addManager(manager);
            emailService.sendEmail(savedManager.getEmail(), "Manager Account Credentials", 
                "Username: " + savedManager.getUsername() + "\nPassword: " + savedManager.getPassword());
            return ResponseEntity.ok(Map.of("success", true, "message", "Manager added successfully", "id", savedManager.getId()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("success", false, "message", "Error: " + e.getMessage()));
        }
    }
    
    // ================= ADD EMPLOYEE =================
    @PostMapping("/addemployee")
    public ResponseEntity<Map<String, Object>> addEmployee(@RequestBody Employee employee, @RequestHeader("Authorization") String authHeader) {
        if (!isAdmin(authHeader)) return ResponseEntity.status(403).body(Map.of("message", "Access Denied"));
        try {
            Employee savedEmployee = adminService.addEmployee(employee);
            emailService.sendEmail(savedEmployee.getEmail(), "Employee Account Credentials", 
                "Username: " + savedEmployee.getUsername() + "\nPassword: " + savedEmployee.getPassword());
            return ResponseEntity.ok(Map.of("success", true, "message", "Employee added", "id", savedEmployee.getId()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("success", false, "message", "Error: " + e.getMessage()));
        }
    }
    
    // ================= VIEW ALL MANAGERS =================
    @GetMapping("/viewallmanagers")
    public ResponseEntity<?> viewAllManagers(@RequestHeader("Authorization") String authHeader) {
        if (!isAdmin(authHeader)) return ResponseEntity.status(403).body(Map.of("error", "Access Denied"));
        try {
            List<Manager> managers = adminService.viewAllManagers();
            List<Map<String, Object>> simpleManagers = new ArrayList<>();
            for (Manager m : managers) {
                Map<String, Object> simple = new HashMap<>();
                simple.put("id", m.getId()); simple.put("name", m.getName()); simple.put("email", m.getEmail());
                simple.put("department", m.getDepartment()); simple.put("contact", m.getContact());
                simpleManagers.add(simple);
            }
            return ResponseEntity.ok(simpleManagers);
        } catch (Exception e) { return ResponseEntity.status(500).body(Map.of("error", e.getMessage())); }
    }
    
    // ================= VIEW ALL EMPLOYEES =================
    @GetMapping("/viewallemployees")
    public ResponseEntity<?> viewAllEmployees(@RequestHeader("Authorization") String authHeader) {
        if (!isAdmin(authHeader)) return ResponseEntity.status(403).body(Map.of("error", "Access Denied"));
        try {
            List<Employee> employees = adminService.viewAllEmployees();
            List<Map<String, Object>> simpleEmployees = new ArrayList<>();
            for (Employee e : employees) {
                Map<String, Object> simple = new HashMap<>();
                simple.put("id", e.getId()); simple.put("name", e.getName()); simple.put("email", e.getEmail());
                simple.put("department", e.getDepartment()); simple.put("accountstatus", e.getAccountstatus());
                if (e.getManager() != null) simple.put("manager", Map.of("name", e.getManager().getName()));
                simpleEmployees.add(simple);
            }
            return ResponseEntity.ok(simpleEmployees);
        } catch (Exception e) { return ResponseEntity.status(500).body(Map.of("error", e.getMessage())); }
    }
    
    // ================= DELETE MANAGER =================
    @DeleteMapping("/deletemanager")
    public ResponseEntity<Map<String, String>> deleteManager(@RequestParam("mid") Long mid, @RequestHeader("Authorization") String authHeader) {
        if (!isAdmin(authHeader)) return ResponseEntity.status(403).body(Map.of("message", "Access Denied"));
        try { return ResponseEntity.ok(Map.of("message", adminService.deleteManager(mid))); } 
        catch (Exception e) { return ResponseEntity.status(500).body(Map.of("message", e.getMessage())); }
    }
    
    // ================= DELETE EMPLOYEE =================
    @DeleteMapping("/deleteemployee")
    public ResponseEntity<Map<String, String>> deleteEmployee(@RequestParam("eid") Long eid, @RequestHeader("Authorization") String authHeader) {
        if (!isAdmin(authHeader)) return ResponseEntity.status(403).body(Map.of("message", "Access Denied"));
        try { return ResponseEntity.ok(Map.of("message", adminService.deleteEmployee(eid))); } 
        catch (Exception e) { return ResponseEntity.status(500).body(Map.of("message", e.getMessage())); }
    }
    
    // ================= ASSIGN MANAGER =================
    @PutMapping("/assignmanager")
    public ResponseEntity<Map<String, String>> assignManager(@RequestParam("empId") Long empId, @RequestParam("managerId") Long managerId, @RequestHeader("Authorization") String authHeader) {
        if (!isAdmin(authHeader)) return ResponseEntity.status(403).body(Map.of("message", "Access Denied"));
        try {
            String result = adminService.assignManagerToEmployee(empId, managerId);
            if (result.contains("successfully")) {
                Employee emp = adminService.getEmployeeById(empId);
                Manager mgr = adminService.getManagerById(managerId);
                emailService.sendEmail(emp.getEmail(), "Manager Assigned", "You are assigned to " + mgr.getName());
                emailService.sendEmail(mgr.getEmail(), "New Employee", "You have a new employee: " + emp.getName());
            }
            return ResponseEntity.ok(Map.of("message", result));
        } catch (Exception e) { return ResponseEntity.status(500).body(Map.of("message", e.getMessage())); }
    }
    
    // ================= COUNTS =================
    @GetMapping("/managercount")
    public ResponseEntity<Map<String, Long>> managerCount(@RequestHeader("Authorization") String authHeader) {
        if (!isAdmin(authHeader)) return ResponseEntity.status(403).body(Map.of("error", 403L));
        try { return ResponseEntity.ok(Map.of("count", adminService.managercount())); } catch(Exception e) { return ResponseEntity.ok(Map.of("count", 0L)); }
    }
    
    @GetMapping("/employeecount")
    public ResponseEntity<Map<String, Long>> employeeCount(@RequestHeader("Authorization") String authHeader) {
        if (!isAdmin(authHeader)) return ResponseEntity.status(403).body(Map.of("error", 403L));
        try { return ResponseEntity.ok(Map.of("count", adminService.employeecount())); } catch(Exception e) { return ResponseEntity.ok(Map.of("count", 0L)); }
    }

    @GetMapping("/employee/{id}")
    public ResponseEntity<Map<String, Object>> getEmployeeById(@PathVariable Long id, @RequestHeader("Authorization") String authHeader) {
        if (!isAdmin(authHeader)) return ResponseEntity.status(403).body(Map.of("message", "Access Denied"));
        try { return ResponseEntity.ok(Map.of("success", true, "employee", adminService.getEmployeeById(id))); } catch(Exception e) { return ResponseEntity.status(500).body(Map.of("message", e.getMessage())); }
    }

    @GetMapping("/manager/{id}")
    public ResponseEntity<Map<String, Object>> getManagerById(@PathVariable Long id, @RequestHeader("Authorization") String authHeader) {
        if (!isAdmin(authHeader)) return ResponseEntity.status(403).body(Map.of("message", "Access Denied"));
        try { return ResponseEntity.ok(Map.of("success", true, "manager", adminService.getManagerById(id))); } catch(Exception e) { return ResponseEntity.status(500).body(Map.of("message", e.getMessage())); }
    }
    @Autowired private com.klef.fsad.sdp.services.PayslipService payslipService;

    @PostMapping("/generatepayslip")
    public ResponseEntity<Map<String, String>> generatePayslip(@RequestParam("empId") Long empId, @RequestParam("month") String month, @RequestParam("year") int year, @RequestHeader("Authorization") String authHeader) {
        if (!isAdmin(authHeader)) return ResponseEntity.status(403).body(Map.of("message", "Access Denied"));
        try {
            return ResponseEntity.ok(Map.of("message", payslipService.generatePayslip(empId, month, year)));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", e.getMessage()));
        }
    }
}