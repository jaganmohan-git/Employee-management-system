package com.klef.fsad.sdp.services;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.klef.fsad.sdp.model.Employee;
import com.klef.fsad.sdp.model.Leave;
import com.klef.fsad.sdp.model.Manager;
import com.klef.fsad.sdp.repository.EmployeeRepository;
import com.klef.fsad.sdp.repository.LeaveRepository;
import com.klef.fsad.sdp.repository.ManagerRepository;

@Service
public class LeaveServiceImpl implements LeaveService {
    @Autowired private LeaveRepository leaveRepository;
    @Autowired private EmployeeRepository employeeRepository;
    @Autowired private ManagerRepository managerRepository;

    @Override @Transactional
    public Leave applyLeaveByEmployee(Leave leave, Long empid) {
        Employee employee = employeeRepository.findById(empid).orElseThrow(() -> new RuntimeException("Employee not found"));
        leave.setStatus("PENDING");
        leave.setEmployee(employee);
        if (employee.getManager() != null) leave.setManager(employee.getManager());
        return leaveRepository.save(leave);
    }

    @Override public List<Leave> viewLeavesByEmployee(Long empid) { Employee e = employeeRepository.findById(empid).orElse(null); return e != null ? leaveRepository.findByEmployee(e) : List.of(); }
    @Override public List<Leave> viewLeavesByManager(Long managerId) { Manager m = managerRepository.findById(managerId).orElse(null); return m != null ? leaveRepository.findByManager(m) : List.of(); }
    
    @Override @Transactional
    public String updateLeaveStatus(Long leaveid, String status) {
        Leave leave = leaveRepository.findById(leaveid).orElse(null);
        if (leave == null) return "Leave not found";
        leave.setStatus(status.toUpperCase());
        leaveRepository.save(leave);
        return "Leave status updated to " + status;
    }
    @Override public List<Leave> getAllLeaves() { return leaveRepository.findAll(); }
}