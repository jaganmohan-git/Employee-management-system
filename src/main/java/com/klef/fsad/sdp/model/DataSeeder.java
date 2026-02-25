package com.klef.fsad.sdp.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.klef.fsad.sdp.model.Admin;
import com.klef.fsad.sdp.model.Manager;
import com.klef.fsad.sdp.model.Employee;
import com.klef.fsad.sdp.repository.AdminRepository;
import com.klef.fsad.sdp.repository.ManagerRepository;
import com.klef.fsad.sdp.repository.EmployeeRepository;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired private AdminRepository adminRepo;
    @Autowired private ManagerRepository managerRepo;
    @Autowired private EmployeeRepository employeeRepo;

    @Override
    public void run(String... args) throws Exception {
        if (adminRepo.count() == 0) {
            Admin admin = new Admin();
            admin.setUsername("admin");
            admin.setPassword("admin");
            admin.setEmail("admin@ems.com");
            admin.setRole("ADMIN");
            adminRepo.save(admin);
            System.out.println("Seeded Admin: admin/admin");
        }

        if (managerRepo.count() == 0) {
            Manager manager = new Manager();
            manager.setName("Test Manager");
            manager.setUsername("manager");
            manager.setPassword("manager");
            manager.setEmail("manager@ems.com");
            manager.setDepartment("Engineering");
            manager.setContact("9999999999");
            manager.setRole("MANAGER");
            managerRepo.save(manager);
            System.out.println("Seeded Manager: manager/manager");
        }

        if (employeeRepo.count() == 0) {
            Employee emp = new Employee();
            emp.setName("Test Employee");
            emp.setUsername("employee");
            emp.setPassword("employee");
            emp.setEmail("employee@ems.com");
            emp.setDepartment("Engineering");
            emp.setDesignation("Software Engineer");
            emp.setSalary(50000);
            emp.setContact("8888888888");
            emp.setRole("EMPLOYEE");
            emp.setAccountstatus("ACTIVE");
            emp.setGender("Male");
            emp.setAge(25);
            
            // Assign to manager
            Manager mgr = managerRepo.findAll().get(0);
            emp.setManager(mgr);
            
            employeeRepo.save(emp);
            System.out.println("Seeded Employee: employee/employee");
        }
    }
}
