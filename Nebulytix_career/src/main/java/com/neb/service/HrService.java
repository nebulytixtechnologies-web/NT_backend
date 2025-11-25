
package com.neb.service;

import java.time.LocalDate;
import java.util.List;

import com.neb.dto.AddEmployeeRequestDto;
import com.neb.dto.AddEmployeeResponseDto;
import com.neb.dto.AddJobRequestDto;
import com.neb.dto.EmployeeDetailsResponseDto;
import com.neb.dto.EmployeeResponseDto;
import com.neb.dto.JobDetailsDto;
import com.neb.dto.LoginRequestDto;
import com.neb.dto.PayslipDto;
import com.neb.dto.UpdateEmployeeRequestDto;
import com.neb.dto.UpdatePasswordRequestDto;

public interface HrService {

    // Add a new employee
    public AddEmployeeResponseDto addEmployee(AddEmployeeRequestDto addEmpReq);

    // HR login
    public EmployeeResponseDto login(LoginRequestDto loginReq);

    // Get all employee details
    public List<EmployeeDetailsResponseDto> getEmployeeList();

    // Get employee details by ID
    public EmployeeDetailsResponseDto getEmployee(Long id);

    // Delete employee by ID
    public String deleteById(Long id);

    // Download an employee's payslip (PDF)
    public byte[] downloadPayslip(Long payslipId) throws Exception;

    // List all payslips for a specific employee
    public List<PayslipDto> listPayslipsForEmployee(Long employeeId);

    // Add or update employee attendance
    public EmployeeDetailsResponseDto addAttendence(Long id, int days);

    // Update existing employee information
    EmployeeDetailsResponseDto updateEmployee(Long id, UpdateEmployeeRequestDto updateReq);
    
    EmployeeDetailsResponseDto updatePassword(Long id, UpdatePasswordRequestDto updatePasswordRequestDto);

     public JobDetailsDto addJob(AddJobRequestDto jobRequestDto);
     
     public List<JobDetailsDto> getAllJobs();
     
     public String generateDailyReport(LocalDate reportDate);
     
     public String getDailyReportUrl(LocalDate reportDate);
    
 	void updateJobApplicationStatus(Long applicationId, Boolean status);
 	String deleteJob(Long jobId);

}
