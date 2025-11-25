/**
 * ---------------------------------------------------------------
 * File Name   : EmployeeRepository.java
 * Package     : com.neb.repository
 * ---------------------------------------------------------------
 * Purpose :
 *   This interface is used to interact with the Employee table 
 *   in the database and perform CRUD operations.
 *
 * Description :
 *   - Extends JpaRepository to automatically provide methods 
 *     like save, findAll, findById, deleteById, etc.
 *   - Also defines custom query methods for login, filtering,
 *     and checking existing employees.
 *
 * Custom Methods :
 *   ✅ findByEmailAndPasswordAndLoginRole(...) 
 *        → Finds an employee by email, password, and role (used for login).
 *
 *   ✅ existsByEmail(...) 
 *        → Checks if an employee already exists using email.
 *
 *   ✅ findByLoginRoleNot(String role) 
 *        → Gets all employees except the given role (e.g., exclude admin).
 *
 *   ✅ findByLoginRoleNotIn(List<String> roles)
 *        → Gets employees excluding multiple roles (e.g., admin, hr).
 *
 *   ✅ findByEmail(String email)
 *        → Finds an employee by email ID.
 *
 * Result :
 *   Helps in managing employee data easily without writing SQL queries.
 * ---------------------------------------------------------------
 */

package com.neb.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.neb.entity.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Optional<Employee> findByEmailAndPasswordAndLoginRole(String email, String password, String loginRole);

    boolean existsByEmail(String email);
    
    List<Employee> findByLoginRoleNot(String loginRole);

    List<Employee> findByLoginRoleNotIn(List<String> roles);
    
    Optional<Employee> findByEmail(String email);
}
