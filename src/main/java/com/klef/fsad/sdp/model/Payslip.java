package com.klef.fsad.sdp.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "payslip_table")
public class Payslip {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String month;

    @Column(nullable = false)
    private int year;

    @Column(nullable = false)
    private double basicSalary;

    @Column(nullable = false)
    private double hra; // House Rent Allowance

    @Column(nullable = false)
    private double deductions; // Tax, PF, etc.

    @Column(nullable = false)
    private double netSalary;

    @ManyToOne
    @JoinColumn(name = "emp_id", nullable = false)
    @JsonIgnoreProperties("payslips")
    private Employee employee;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getMonth() { return month; }
    public void setMonth(String month) { this.month = month; }
    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }
    public double getBasicSalary() { return basicSalary; }
    public void setBasicSalary(double basicSalary) { this.basicSalary = basicSalary; }
    public double getHra() { return hra; }
    public void setHra(double hra) { this.hra = hra; }
    public double getDeductions() { return deductions; }
    public void setDeductions(double deductions) { this.deductions = deductions; }
    public double getNetSalary() { return netSalary; }
    public void setNetSalary(double netSalary) { this.netSalary = netSalary; }
    public Employee getEmployee() { return employee; }
    public void setEmployee(Employee employee) { this.employee = employee; }
}
