package com.klef.fsad.sdp.services;

import java.util.List;
import com.klef.fsad.sdp.model.Admin;
import com.klef.fsad.sdp.model.Employee;
import com.klef.fsad.sdp.model.Leave;
import com.klef.fsad.sdp.model.Manager;

public interface AdminService {
    Admin checkadminlogin(String username, String password);
    Manager addManager(Manager manager);
    List<Manager> viewAllManagers();
    String deleteManager(Long mid);
    List<Employee> viewAllEmployees();
    String deleteEmployee(Long eid);
    Employee addEmployee(Employee employee);
    long managercount();
    String assignManagerToEmployee(Long empId, Long managerId);
    long employeecount();
    Employee getEmployeeById(Long id);
    Manager getManagerById(Long id);
    List<Leave> viewAllLeaveApplications();
}