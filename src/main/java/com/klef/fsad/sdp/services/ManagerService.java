package com.klef.fsad.sdp.services;

import java.util.List;
import com.klef.fsad.sdp.model.Employee;
import com.klef.fsad.sdp.model.Manager;

public interface ManagerService {
    Manager checkmanagerlogin(String username, String password);
    Manager getManagerById(Long id);
    Manager getManagerByUsername(String username);
    List<Employee> getEmployeesByManagerId(Long managerId);
    List<Employee> getEmployeesByManagerUsername(String username);
    String approveEmployee(Long empId, String status);
    String changePassword(Long managerId, String oldPassword, String newPassword);
    Manager updateManager(Manager manager);
}