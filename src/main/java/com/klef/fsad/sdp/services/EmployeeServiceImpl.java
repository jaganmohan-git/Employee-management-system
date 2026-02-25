package com.klef.fsad.sdp.services;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.klef.fsad.sdp.model.Duty;
import com.klef.fsad.sdp.model.Employee;
import com.klef.fsad.sdp.model.ResetToken;
import com.klef.fsad.sdp.repository.DutyRepository;
import com.klef.fsad.sdp.repository.EmployeeRepository;
import com.klef.fsad.sdp.repository.ResetTokenRepository;

@Service
public class EmployeeServiceImpl implements EmployeeService {
    @Autowired private EmployeeRepository employeeRepository;
    @Autowired private DutyRepository dutyRepository;
    @Autowired private ResetTokenRepository resetTokenRepository;

    @Override public Employee checkemplogin(String username, String password) { return employeeRepository.findByUsernameAndPassword(username, password); }
    @Override @Transactional public String registerEmployee(Employee emp) { emp.setRole("EMPLOYEE"); emp.setAccountstatus("PENDING"); employeeRepository.save(emp); return "Employee Registered Successfully"; }
    @Override public String updateEmployeeProfile(Employee emp) { employeeRepository.save(emp); return "Employee Updated Successfully"; }
    @Override public Employee findEmployeeById(Long id) { return employeeRepository.findById(id).orElse(null); }
    @Override public Employee save(Employee employee) { return employeeRepository.save(employee); }
    @Override public Employee findEmployeeByUsername(String username) { return employeeRepository.findByUsername(username); }
    @Override public Employee findEmployeeByEmail(String email) { return employeeRepository.findByEmail(email); }
    @Override public List<Employee> viewAllEmployees() { return employeeRepository.findAll(); }
    @Override public String updateAccountStatus(Long empid, String status) { Optional<Employee> e = employeeRepository.findById(empid); if (e.isPresent()) { e.get().setAccountstatus(status); employeeRepository.save(e.get()); return "Status Updated"; } return "Not Found"; }
    @Override public List<Duty> viewAssignedDuties(Long empid) { Employee emp = employeeRepository.findById(empid).orElse(null); return emp != null ? dutyRepository.findByEmployee(emp) : Collections.emptyList(); }
    
    @Override public String generateResetToken(String email) {
        Employee employee = employeeRepository.findByEmail(email);
        if (employee != null) {
            String token = UUID.randomUUID().toString();
            ResetToken rt = new ResetToken(); rt.setToken(token); rt.setEmail(email);
            rt.setCreatedAt(LocalDateTime.now()); rt.setExpiresAt(LocalDateTime.now().plusMinutes(5));
            resetTokenRepository.save(rt); return token;
        } return null;
    }
    @Override public boolean validateResetToken(String token) { return resetTokenRepository.findByToken(token).isPresent() && !isTokenExpired(token); }
    @Override public boolean changePassword(Employee employee, String oldPassword, String newPassword) { if (employee.getPassword().equals(oldPassword)) { employee.setPassword(newPassword); employeeRepository.save(employee); return true; } return false; }
    @Override public void updatePassword(String token, String newPassword) { Optional<ResetToken> rt = resetTokenRepository.findByToken(token); if (rt.isPresent() && !isTokenExpired(token)) { Employee e = employeeRepository.findByEmail(rt.get().getEmail()); if(e!=null){ e.setPassword(newPassword); employeeRepository.save(e); deleteResetToken(token); } } }
    @Override public void deleteResetToken(String token) { resetTokenRepository.deleteByToken(token); }
    @Override public boolean isTokenExpired(String token) { Optional<ResetToken> rt = resetTokenRepository.findByToken(token); return rt.isPresent() && rt.get().getExpiresAt().isBefore(LocalDateTime.now()); }
}