package com.klef.fsad.sdp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name="duty_table")
public class Duty {
    @Id @GeneratedValue(strategy =GenerationType.IDENTITY ) private Long id;
    @Column(nullable = false) private String title;
    @Column(nullable = false,length=3000) private String description;
    @Column(name="target_date") private String targetDate;
    @Column(nullable = false) private String status;
    
    @JsonIgnore @ManyToOne @JoinColumn(name="emp_id") private Employee employee;
    @JsonIgnore @ManyToOne @JoinColumn(name = "manager_id") private Manager manager;
    @JsonIgnore @ManyToOne @JoinColumn(name = "assignedByManager") private Manager assignedByManager;
    @JsonIgnore @ManyToOne @JoinColumn(name = "assignedByAdmin") private Admin assignedByAdmin;
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getTargetDate() { return targetDate; }
    public void setTargetDate(String targetDate) { this.targetDate = targetDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Employee getEmployee() { return employee; }
    public void setEmployee(Employee employee) { this.employee = employee; }
    public Manager getManager() { return manager; }
    public void setManager(Manager manager) { this.manager = manager; }
    public Manager getAssignedByManager() { return assignedByManager; }
    public void setAssignedByManager(Manager assignedByManager) { this.assignedByManager = assignedByManager; }
    public Admin getAssignedByAdmin() { return assignedByAdmin; }
    public void setAssignedByAdmin(Admin assignedByAdmin) { this.assignedByAdmin = assignedByAdmin; }
}