package com.klef.fsad.sdp.services;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.klef.fsad.sdp.model.Attendance;
import com.klef.fsad.sdp.model.Employee;
import com.klef.fsad.sdp.repository.AttendanceRepository;
import com.klef.fsad.sdp.repository.EmployeeRepository;

@Service
public class AttendanceServiceImpl implements AttendanceService {

    @Autowired private AttendanceRepository attendanceRepo;
    @Autowired private EmployeeRepository employeeRepo;

    @Override
    public String clockIn(Long empId) {
        Employee emp = employeeRepo.findById(empId).orElse(null);
        if (emp == null) return "Employee not found";

        Optional<Attendance> existing = attendanceRepo.findByEmployeeAndDate(emp, LocalDate.now());
        if (existing.isPresent()) return "Already clocked in today";

        Attendance attendance = new Attendance();
        attendance.setEmployee(emp);
        attendance.setDate(LocalDate.now());
        attendance.setInTime(LocalTime.now());
        attendance.setStatus("PRESENT");
        
        attendanceRepo.save(attendance);
        return "Clocked In Successfully";
    }

    @Override
    public String clockOut(Long empId) {
        Employee emp = employeeRepo.findById(empId).orElse(null);
        if (emp == null) return "Employee not found";

        Optional<Attendance> existing = attendanceRepo.findByEmployeeAndDate(emp, LocalDate.now());
        if (existing.isEmpty()) return "You must clock in first";

        Attendance attendance = existing.get();
        if (attendance.getOutTime() != null) return "Already clocked out today";

        attendance.setOutTime(LocalTime.now());
        attendanceRepo.save(attendance);
        return "Clocked Out Successfully";
    }

    @Override
    public Attendance getAttendanceByDate(Long empId, LocalDate date) {
        Employee emp = employeeRepo.findById(empId).orElse(null);
        if (emp == null) return null;
        return attendanceRepo.findByEmployeeAndDate(emp, date).orElse(null);
    }

    @Override
    public List<Attendance> viewHistory(Long empId) {
        Employee emp = employeeRepo.findById(empId).orElse(null);
        if (emp == null) return List.of();
        return attendanceRepo.findByEmployee(emp);
    }
}
