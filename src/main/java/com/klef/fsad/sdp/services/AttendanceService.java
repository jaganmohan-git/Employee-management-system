package com.klef.fsad.sdp.services;

import java.time.LocalDate;
import com.klef.fsad.sdp.model.Attendance;

public interface AttendanceService {
    String clockIn(Long empId);
    String clockOut(Long empId);
    Attendance getAttendanceByDate(Long empId, LocalDate date);
    java.util.List<Attendance> viewHistory(Long empId);
}
