package com.neb.dto;

import lombok.Data;

@Data
public class EmployeeReportDto {
    private Long employeeId;
    private String cardNo;
    private String employeeName;
    private String role;
    private String summary; // Add this field

}
