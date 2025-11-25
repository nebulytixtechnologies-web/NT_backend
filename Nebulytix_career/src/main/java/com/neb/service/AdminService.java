
package com.neb.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.neb.dto.*;

public interface AdminService {

    // For admin login verification
    public EmployeeResponseDto login(LoginRequestDto loginReq);

    // For adding a new employee
    public AddEmployeeResponseDto addEmployee(AddEmployeeRequestDto addEmpReq);

    // For retrieving all employee details
    public List<EmployeeDetailsResponseDto> getEmployeeList();

    // For assigning new work to an employee
    public String assignWork(AddWorkRequestDto request,MultipartFile file);

    // For fetching all assigned works to employee
    public List<WorkResponseDto> getAllWorks(Long empId);

    // For fetching work details of a specific employee
    public List<WorkResponseDto> getWorkByEmployee(Long empId);
    
    // Get employee details by ID
    public EmployeeDetailsResponseDto getEmployee(Long id);

    
    public String deleteHr(Long id);
    
   // public UpdateEmployeeResponseDto updateHrDetails(Long empId,UpdateEmployeeRequestDto updateReq);
    
    public byte[] generateDailyReport(LocalDate date)throws Exception;
}
