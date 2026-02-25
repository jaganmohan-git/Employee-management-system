package com.klef.fsad.sdp.services;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.klef.fsad.sdp.model.Admin;
import com.klef.fsad.sdp.model.Employee;
import com.klef.fsad.sdp.model.Leave;
import com.klef.fsad.sdp.model.Manager;
import com.klef.fsad.sdp.repository.AdminRepository;
import com.klef.fsad.sdp.repository.EmployeeRepository;
import com.klef.fsad.sdp.repository.LeaveRepository;
import com.klef.fsad.sdp.repository.ManagerRepository;

@Service
public class AdminServiceImpl implements AdminService {
    
    @Autowired private AdminRepository adminRepository;
    @Autowired private ManagerRepository managerRepository;
    @Autowired private EmployeeRepository employeeRepository;
    @Autowired private LeaveRepository leaveRepository;
    
    @Override
    public Admin checkadminlogin(String username, String password) {
        return adminRepository.findByUsernameAndPassword(username, password);
    }
    
    @Override
    @Transactional
    public Manager addManager(Manager manager) {
        try {
            if (manager.getRole() == null || manager.getRole().trim().isEmpty()) manager.setRole("MANAGER");
            if (manager.getUsername() == null || manager.getUsername().trim().isEmpty()) manager.setUsername(manager.getEmail());
            manager.setPassword(generateRandomPassword(10));
            manager.setFirstLogin(true);
            return managerRepository.save(manager);
        } catch (Exception e) { throw new RuntimeException("Error adding manager: " + e.getMessage()); }
    }
    
    @Override
    @Transactional
    public Employee addEmployee(Employee employee) {
        try {
            if (employee.getUsername() == null) employee.setUsername(employee.getEmail());
            employee.setPassword("emp@" + generateRandomPassword(8));
            if (employee.getGender() == null) employee.setGender("Male");
            if (employee.getAge() <= 0) employee.setAge(25);
            if (employee.getDesignation() == null) employee.setDesignation("Employee");
            if (employee.getDepartment() == null) employee.setDepartment("General");
            if (employee.getSalary() <= 0) employee.setSalary(30000);
            if (employee.getContact() == null) employee.setContact("0000000000");
            
            // Set default photo if missing (fixes "Field 'emp_photo' doesn't have a default value")
            if (employee.getPhoto() == null || employee.getPhoto().isEmpty()) {
                employee.setPhoto("default.jpg"); 
            }
            
            employee.setAccountstatus("PENDING");
            employee.setRole("EMPLOYEE");
            return employeeRepository.save(employee);
        } catch (Exception e) { throw new RuntimeException("Error adding employee: " + e.getMessage()); }
    }
    
    @Override
    public List<Manager> viewAllManagers() { try { return managerRepository.findAll(); } catch (Exception e) { throw new RuntimeException(e.getMessage()); } }
    
    @Override
    public List<Employee> viewAllEmployees() { try { return employeeRepository.findAll(); } catch (Exception e) { throw new RuntimeException(e.getMessage()); } }
    
    @Override
    @Transactional
    public String deleteManager(Long mid) {
        try {
            Optional<Manager> m = managerRepository.findById(mid);
            if (m.isPresent()) {
                if (employeeRepository.countByManager(m.get()) > 0) return "Cannot delete: employees assigned.";
                managerRepository.deleteById(mid);
                return "Manager deleted successfully";
            }
            return "Manager not found";
        } catch (Exception e) { throw new RuntimeException(e.getMessage()); }
    }
    
    @Override
    @Transactional
    public String deleteEmployee(Long eid) {
        try {
            Optional<Employee> emp = employeeRepository.findById(eid);
            if (emp.isPresent()) {
                if (emp.get().getManager() != null) {
                    Manager m = emp.get().getManager();
                    m.getEmployees().remove(emp.get());
                    managerRepository.save(m);
                }
                employeeRepository.deleteById(eid);
                return "Employee deleted successfully";
            }
            return "Employee not found";
        } catch (Exception e) { throw new RuntimeException(e.getMessage()); }
    }
    
    @Override
    @Transactional
    public String assignManagerToEmployee(Long empId, Long managerId) {
        try {
            Employee emp = employeeRepository.findById(empId).orElse(null);
            Manager mgr = managerRepository.findById(managerId).orElse(null);
            if (emp == null || mgr == null) return "Not found";
            if (emp.getManager() != null && emp.getManager().getId().equals(managerId)) return "Already assigned";
            
            if (emp.getManager() != null) {
                Manager old = emp.getManager();
                old.getEmployees().remove(emp);
                managerRepository.save(old);
            }
            emp.setManager(mgr);
            emp.setAccountstatus("ACTIVE");
            employeeRepository.save(emp);
            mgr.getEmployees().add(emp);
            managerRepository.save(mgr);
            return "Manager assigned successfully";
        } catch (Exception e) { throw new RuntimeException(e.getMessage()); }
    }
    
    @Override public long managercount() { return managerRepository.count(); }
    @Override public long employeecount() { return employeeRepository.count(); }
    @Override public Employee getEmployeeById(Long id) { return employeeRepository.findById(id).orElse(null); }
    @Override public Manager getManagerById(Long id) { return managerRepository.findById(id).orElse(null); }
    @Override public List<Leave> viewAllLeaveApplications() { return leaveRepository.findAll(); }
    
    private String generateRandomPassword(int len) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
        StringBuilder sb = new StringBuilder();
        Random r = new Random();
        for (int i = 0; i < len; i++) sb.append(chars.charAt(r.nextInt(chars.length())));
        return sb.toString();
    }
}