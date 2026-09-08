package com.example.hr;
import jakarta.persistence.*;

@Entity
@Table(name = "employees")
public class Employee {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false) private String fullName;
    @Column(nullable = false, unique = true) private String email;
    @Column(nullable = false) private String department;
    protected Employee() {}
    public Employee(String fullName, String email, String department) {
        this.fullName = fullName; this.email = email; this.department = department;
    }
    public Long getId() { return id; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getDepartment() { return department; }
}
