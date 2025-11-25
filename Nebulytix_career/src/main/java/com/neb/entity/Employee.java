/**
 * --------------------------------------------------------------
 * Purpose :
 *   Represents an employee in the organization.
 *   Stores personal, job, and bank details of each employee.
 *
 * Description :
 *   - This class is a JPA entity mapped to the "employees" table.
 *   - Contains employee details such as name, email, job role,
 *     salary, and banking info.
 *   - Maintains relationships with Work and Payslip entities.
 *
 * Relationships :
 *   ✅ One employee can have many works (OneToMany with Work)
 *   ✅ One employee can have many payslips (OneToMany with Payslip)
 * --------------------------------------------------------------
 */

package com.neb.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "employees")
@Data
@SQLDelete(sql = "update employees set emp_status='inactive' where id=?")
@SQLRestriction("emp_status<> 'inactive'")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;
    private String email;
    private String mobile;
    private String cardNumber;

    private String loginRole;  // Role type (admin/hr/employee)
    private String jobRole;    // Position (intern/developer/hr)
    private String domain;     // Department (Java/.Net/Python)
    private String gender;
    private LocalDate joiningDate;
    private Double salary;
    private int daysPresent;
    private int paidLeaves;
    private String password;
    
    // Bank and tax-related details
    private String bankAccountNumber;
    private String ifscCode;
    private String bankName;
    private String pfNumber;
    private String panNumber;
    private String uanNumber;
    private String epsNumber;
    private String esiNumber;
    
    private String empStatus = "active";

    // One employee can have multiple work records
    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    private List<Work> works = new ArrayList<>();

    // One employee can have multiple payslips
    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Payslip> payslips = new ArrayList<>();
}
