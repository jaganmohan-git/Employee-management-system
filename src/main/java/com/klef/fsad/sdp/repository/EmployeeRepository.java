package com.klef.fsad.sdp.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.klef.fsad.sdp.model.Employee;
import com.klef.fsad.sdp.model.Manager;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Employee findByUsernameAndPassword(String username, String password);
    Employee findByEmail(String email);
    Employee findByUsername(String username);
    List<Employee> findByManager(Manager manager);
    List<Employee> findByManagerIsNull();
    long countByManager(Manager manager);
    
    @Query("SELECT e FROM Employee e WHERE e.manager.id = :managerId")
    List<Employee> findByManagerId(@Param("managerId") Long managerId);
}