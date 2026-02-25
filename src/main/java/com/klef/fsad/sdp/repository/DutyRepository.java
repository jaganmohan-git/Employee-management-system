package com.klef.fsad.sdp.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.klef.fsad.sdp.model.Admin;
import com.klef.fsad.sdp.model.Duty;
import com.klef.fsad.sdp.model.Employee;
import com.klef.fsad.sdp.model.Manager;

@Repository
public interface DutyRepository extends JpaRepository<Duty, Long> {
    List<Duty> findByEmployee(Employee employee);
    List<Duty> findByManager(Manager manager);
    List<Duty> findByAssignedByManager(Manager manager);
    List<Duty> findByAssignedByAdmin(Admin admin);
}