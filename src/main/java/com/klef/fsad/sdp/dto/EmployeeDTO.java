package com.klef.fsad.sdp.dto;

public class EmployeeDTO {
    private Long id;
    private String name;
    private String gender;
    private int age;
    private String designation;
    private String department;
    private double salary;
    private String username;
    private String email;
    private String contact;
    private String accountstatus;
    private String role;
    private Long managerId;
    
    public EmployeeDTO() {}
    
    public EmployeeDTO(Long id, String name, String gender, int age, String designation, 
                      String department, double salary, String username, String email, 
                      String contact, String accountstatus, String role, Long managerId) {
        this.id = id; this.name = name; this.gender = gender; this.age = age;
        this.designation = designation; this.department = department; this.salary = salary;
        this.username = username; this.email = email; this.contact = contact;
        this.accountstatus = accountstatus; this.role = role; this.managerId = managerId;
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    
    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }
    
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    
    public double getSalary() { return salary; }
    public void setSalary(double salary) { this.salary = salary; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }
    
    public String getAccountstatus() { return accountstatus; }
    public void setAccountstatus(String accountstatus) { this.accountstatus = accountstatus; }
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    
    public Long getManagerId() { return managerId; }
    public void setManagerId(Long managerId) { this.managerId = managerId; }
}