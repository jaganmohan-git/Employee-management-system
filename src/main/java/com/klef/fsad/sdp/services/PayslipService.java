package com.klef.fsad.sdp.services;

import java.util.List;
import com.klef.fsad.sdp.model.Payslip;

public interface PayslipService {
    String generatePayslip(Long empId, String month, int year);
    List<Payslip> getPayslipsByEmployee(Long empId);
}
