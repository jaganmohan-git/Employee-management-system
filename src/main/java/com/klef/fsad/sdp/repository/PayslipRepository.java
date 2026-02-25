package com.klef.fsad.sdp.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.klef.fsad.sdp.model.Payslip;
import com.klef.fsad.sdp.model.Employee;

@Repository
public interface PayslipRepository extends JpaRepository<Payslip, Long> {
    List<Payslip> findByEmployee(Employee employee);
    boolean existsByEmployeeAndMonthAndYear(Employee employee, String month, int year);
}
