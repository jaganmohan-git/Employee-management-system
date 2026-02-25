package com.klef.fsad.sdp.services;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.klef.fsad.sdp.model.Employee;
import com.klef.fsad.sdp.model.Manager;
import com.klef.fsad.sdp.repository.EmployeeRepository;
import com.klef.fsad.sdp.repository.ManagerRepository;

@Service
public class ManagerServiceImpl implements ManagerService {
    @Autowired private ManagerRepository managerRepository;
    @Autowired private EmployeeRepository employeeRepository;

    @Override public Manager checkmanagerlogin(String username, String password) { return managerRepository.findByUsernameAndPassword(username, password); }
    @Override public Manager getManagerById(Long id) { return managerRepository.findById(id).orElse(null); }
    @Override public Manager getManagerByUsername(String username) { return managerRepository.findByUsername(username); }
    
    @Override
    public List<Employee> getEmployeesByManagerId(Long managerId) {
        try {
            List<Employee> employees = employeeRepository.findByManagerId(managerId);
            if (employees == null) return new ArrayList<>();
            for (Employee emp : employees) emp.setManager(null); // Break circular ref
            return employees;
        } catch (Exception e) { return new ArrayList<>(); }
    }
    
    @Override
    public List<Employee> getEmployeesByManagerUsername(String username) {
        try {
            Manager manager = managerRepository.findByUsername(username);
            if (manager == null) return new ArrayList<>();
            List<Employee> employees = employeeRepository.findByManager(manager);
            for (Employee emp : employees) emp.setManager(null);
            return employees;
        } catch (Exception e) { return new ArrayList<>(); }
    }
    
    @Override @Transactional
    public String approveEmployee(Long empId, String status) {
        Employee emp = employeeRepository.findById(empId).orElse(null);
        if (emp == null) return "Employee not found";
        if (!status.equalsIgnoreCase("ACTIVE") && !status.equalsIgnoreCase("REJECTED")) return "Invalid status";
        emp.setAccountstatus(status.toUpperCase());
        employeeRepository.save(emp);
        return "Employee " + status + " successfully";
    }
    
    @Override @Transactional
    public String changePassword(Long managerId, String oldPassword, String newPassword) {
        Manager manager = managerRepository.findById(managerId).orElse(null);
        if (manager == null) return "Manager not found";
        if (!manager.getPassword().equals(oldPassword)) return "Old password is incorrect";
        manager.setPassword(newPassword);
        manager.setFirstLogin(false);
        managerRepository.save(manager);
        return "Password changed successfully";
    }
    
    @Override @Transactional public Manager updateManager(Manager manager) { return managerRepository.save(manager); }
}