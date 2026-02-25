package com.klef.fsad.sdp.services;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.klef.fsad.sdp.model.Admin;
import com.klef.fsad.sdp.model.Duty;
import com.klef.fsad.sdp.model.Employee;
import com.klef.fsad.sdp.model.Manager;
import com.klef.fsad.sdp.repository.*;

@Service
public class DutyServiceImpl implements DutyService {
    @Autowired private EmployeeRepository employeeRepository;
    @Autowired private AdminRepository adminRepository;
    @Autowired private DutyRepository dutyRepository;
    @Autowired private ManagerRepository managerRepository;

    @Override @Transactional public Duty assignDutyByAdminToEmployee(Duty duty, Long empid, Long adminid) {
        Employee emp = employeeRepository.findById(empid).orElse(null);
        Admin admin = adminRepository.findById(adminid).orElse(null);
        if (emp != null && admin != null) { duty.setEmployee(emp); duty.setAssignedByAdmin(admin); return dutyRepository.save(duty); } return null;
    }
    @Override @Transactional public Duty assignDutyByAdminToManager(Duty duty, Long managerid, Long adminid) {
        Manager manager = managerRepository.findById(managerid).orElse(null);
        Admin admin = adminRepository.findById(adminid).orElse(null);
        if (manager != null && admin != null) { duty.setManager(manager); duty.setAssignedByAdmin(admin); return dutyRepository.save(duty); } return null;
    }
    @Override @Transactional public Duty assignDutyByManagerToEmployee(Duty duty, Long empid, Long managerid) {
        Employee emp = employeeRepository.findById(empid).orElse(null);
        Manager manager = managerRepository.findById(managerid).orElse(null);
        if (emp != null && manager != null) { duty.setEmployee(emp); duty.setAssignedByManager(manager); return dutyRepository.save(duty); } return null;
    }
    @Override public List<Duty> viewAllDutiesofEmployee(Long eid) { Employee emp = employeeRepository.findById(eid).orElse(null); return emp != null ? dutyRepository.findByEmployee(emp) : List.of(); }
    @Override public List<Duty> viewDutiesAssignedByManager(Long managerid) { Manager manager = managerRepository.findById(managerid).orElse(null); return manager != null ? dutyRepository.findByAssignedByManager(manager) : List.of(); }
    @Override public List<Duty> viewDutiesAssignedByAdmin(Long adminid) { Admin admin = adminRepository.findById(adminid).orElse(null); return admin != null ? dutyRepository.findByAssignedByAdmin(admin) : List.of(); }

    @Override
    @Transactional
    public String updateDutyStatus(Long dutyId, String status) {
        Duty duty = dutyRepository.findById(dutyId).orElse(null);
        if (duty != null) {
            duty.setStatus(status);
            dutyRepository.save(duty);
            return "Duty status updated";
        }
        return "Duty not found";
    }
}