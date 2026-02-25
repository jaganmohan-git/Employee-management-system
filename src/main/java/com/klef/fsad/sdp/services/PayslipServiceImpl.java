package com.klef.fsad.sdp.services;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.klef.fsad.sdp.model.Employee;
import com.klef.fsad.sdp.model.Payslip;
import com.klef.fsad.sdp.repository.EmployeeRepository;
import com.klef.fsad.sdp.repository.PayslipRepository;

@Service
public class PayslipServiceImpl implements PayslipService {

    @Autowired private PayslipRepository payslipRepository;
    @Autowired private EmployeeRepository employeeRepository;

    @Override
    public String generatePayslip(Long empId, String month, int year) {
        Employee emp = employeeRepository.findById(empId).orElse(null);
        if (emp == null) return "Employee not found";

        if (payslipRepository.existsByEmployeeAndMonthAndYear(emp, month, year)) {
            return "Payslip already generated for this month";
        }

        double basic = emp.getSalary();
        double hra = basic * 0.20; // 20% HRA
        double deductions = basic * 0.05; // 5% Deductions
        double net = basic + hra - deductions;

        Payslip payslip = new Payslip();
        payslip.setEmployee(emp);
        payslip.setMonth(month);
        payslip.setYear(year);
        payslip.setBasicSalary(basic);
        payslip.setHra(hra);
        payslip.setDeductions(deductions);
        payslip.setNetSalary(net);

        payslipRepository.save(payslip);
        return "Payslip generated successfully";
    }

    @Override
    public List<Payslip> getPayslipsByEmployee(Long empId) {
        Employee emp = employeeRepository.findById(empId).orElse(null);
        if (emp == null) return List.of();
        return payslipRepository.findByEmployee(emp);
    }
}
