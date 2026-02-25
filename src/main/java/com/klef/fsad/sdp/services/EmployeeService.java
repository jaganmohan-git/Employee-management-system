package com.klef.fsad.sdp.services;

import java.util.List;
import com.klef.fsad.sdp.model.Duty;
import com.klef.fsad.sdp.model.Employee;

public interface EmployeeService {
    Employee checkemplogin(String username, String password);
    String registerEmployee(Employee emp);
    String updateEmployeeProfile(Employee emp);
    Employee findEmployeeById(Long id);
    Employee save(Employee employee);
    Employee findEmployeeByUsername(String username);
    Employee findEmployeeByEmail(String email);
    List<Employee> viewAllEmployees();
    String updateAccountStatus(Long empid, String status);
    List<Duty> viewAssignedDuties(Long empid);
    
    String generateResetToken(String email);
    boolean validateResetToken(String token);
    boolean changePassword(Employee employee, String oldPassword, String newPassword);
    void updatePassword(String token, String newPassword);
    void deleteResetToken(String token);
    boolean isTokenExpired(String token);
}