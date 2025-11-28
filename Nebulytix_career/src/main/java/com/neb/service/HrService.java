package com.neb.service;

import java.time.LocalDate;
import java.util.List;

import com.neb.dto.AddEmployeeRequestDto;
import com.neb.dto.AddEmployeeResponseDto;
import com.neb.dto.AddJobRequestDto;
import com.neb.dto.EmployeeDetailsResponseDto;
import com.neb.dto.JobDetailsDto;
import com.neb.dto.LoginRequestDto;
import com.neb.dto.PayslipDto;
import com.neb.dto.UpdateEmployeeRequestDto;
import com.neb.dto.UpdatePasswordRequestDto;
import com.neb.entity.JobApplication;

public interface HrService {

    // Employee management
    AddEmployeeResponseDto addEmployee(AddEmployeeRequestDto addEmpReq);
    EmployeeDetailsResponseDto login(LoginRequestDto loginReq);
    List<EmployeeDetailsResponseDto> getEmployeeList();
    EmployeeDetailsResponseDto getEmployee(Long id);
    String deleteById(Long id);
    EmployeeDetailsResponseDto addAttendence(Long id, int days);
    EmployeeDetailsResponseDto updateEmployee(Long id, UpdateEmployeeRequestDto updateReq);
    EmployeeDetailsResponseDto updatePassword(Long id, UpdatePasswordRequestDto updatePasswordRequestDto);

    // Payslip
    byte[] downloadPayslip(Long payslipId) throws Exception;
    List<PayslipDto> listPayslipsForEmployee(Long employeeId);

    // Job management
    JobDetailsDto addJob(AddJobRequestDto jobRequestDto);
    List<JobDetailsDto> getAllJobs();
    String deleteJob(Long jobId);

    // Daily report
    String generateDailyReport(LocalDate reportDate);
    String getDailyReportUrl(LocalDate reportDate);

    // Job application
    void updateJobApplicationStatus(Long applicationId, Boolean status);
    void sendInvitedEmailAndUpdateStatus(Long applicantId, String subject, String message);
    void sendRejectedEmailAndUpdateStatus(Long applicantId, String subject, String message);

//    // Send email to all applicants by status
//    void sendEmailsToShortlisted(String subject, String message);
//    void sendEmailsToRejected(String subject, String message);
    List<JobApplication> sendEmailsToShortlisted(String subject, String message);
    List<JobApplication> sendEmailsToRejected(String subject, String message);

    // Single applicant email
    void sendEmailToSingleApplicant(Long applicantId, String subject, String message);
}
