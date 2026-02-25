package com.klef.fsad.sdp.services;

import java.util.List;
import com.klef.fsad.sdp.model.Duty;

public interface DutyService {
    Duty assignDutyByAdminToEmployee(Duty duty, Long empid, Long adminid);
    Duty assignDutyByAdminToManager(Duty duty, Long managerid, Long adminid);
    Duty assignDutyByManagerToEmployee(Duty duty, Long empid, Long managerid);
    List<Duty> viewAllDutiesofEmployee(Long eid);
    List<Duty> viewDutiesAssignedByManager(Long managerid);
    List<Duty> viewDutiesAssignedByAdmin(Long adminid);
    String updateDutyStatus(Long dutyId, String status);
}