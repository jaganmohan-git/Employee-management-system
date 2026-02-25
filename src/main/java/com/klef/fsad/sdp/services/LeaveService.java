package com.klef.fsad.sdp.services;

import java.util.List;
import com.klef.fsad.sdp.model.Leave;

public interface LeaveService {
    Leave applyLeaveByEmployee(Leave leave, Long empid);
    List<Leave> viewLeavesByEmployee(Long empid);
    List<Leave> viewLeavesByManager(Long managerId);
    String updateLeaveStatus(Long leaveid, String status);
    List<Leave> getAllLeaves();
}