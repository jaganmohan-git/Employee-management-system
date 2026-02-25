package com.klef.fsad.sdp.controller;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.klef.fsad.sdp.dto.LoginRequest;
import com.klef.fsad.sdp.model.Admin;
import com.klef.fsad.sdp.model.Employee;
import com.klef.fsad.sdp.model.Manager;
import com.klef.fsad.sdp.security.JWTUtilizer;
import com.klef.fsad.sdp.services.AdminService;
import com.klef.fsad.sdp.services.EmployeeService;
import com.klef.fsad.sdp.services.ManagerService;

@RestController
@RequestMapping("/auth")
@CrossOrigin("*")
public class AuthController {

    @Autowired private AdminService adminService;
    @Autowired private ManagerService managerService;
    @Autowired private EmployeeService employeeService;
    @Autowired private JWTUtilizer jwtService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        String id = req.getIdentifier(); // Assuming identifier is username
        String pwd = req.getPassword();

        // Check Admin
        Admin admin = adminService.checkadminlogin(id, pwd);
        if (admin != null) {
            String token = jwtService.generateToken(admin.getUsername(), "ADMIN", Long.valueOf(admin.getId())); // Ensure ID compatibility
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("role", "ADMIN");
            response.put("token", token);
            response.put("id", admin.getId());
            response.put("username", admin.getUsername());
            return ResponseEntity.ok(response);
        }

        // Check Manager
        Manager manager = managerService.checkmanagerlogin(id, pwd);
        if (manager != null) {
            String token = jwtService.generateToken(manager.getUsername(), "MANAGER", manager.getId());
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("role", "MANAGER");
            response.put("token", token);
            response.put("id", manager.getId());
            response.put("username", manager.getUsername());
            return ResponseEntity.ok(response);
        }

        // Check Employee
        Employee emp = employeeService.checkemplogin(id, pwd);
        if (emp != null && "ACTIVE".equals(emp.getAccountstatus())) {
            String token = jwtService.generateToken(emp.getUsername(), "EMPLOYEE", emp.getId());
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("role", "EMPLOYEE");
            response.put("token", token);
            response.put("id", emp.getId());
            response.put("username", emp.getUsername());
            return ResponseEntity.ok(response);
        }

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("message", "Invalid credentials or account not active");
        return ResponseEntity.status(401).body(errorResponse);
    }
}