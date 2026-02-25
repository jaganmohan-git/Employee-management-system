package com.klef.fsad.sdp.model;

import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "leave_table")
public class Leave {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private int id;
    @Column(nullable=false) private LocalDate startDate;
    @Column(nullable=false) private LocalDate endDate;
    @Column(nullable=false) private String reason;
    @Column(nullable=false) private String status;
    
    @JsonIgnore @ManyToOne @JoinColumn(name="emp_id") private Employee employee;
    @JsonIgnore @ManyToOne @JoinColumn(name="manager_id") private Manager manager;
    
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Employee getEmployee() { return employee; }
    public void setEmployee(Employee employee) { this.employee = employee; }
    public Manager getManager() { return manager; }
    public void setManager(Manager manager) { this.manager = manager; }
}