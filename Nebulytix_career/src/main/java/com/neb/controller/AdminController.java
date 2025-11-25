//admin controller
package com.neb.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.neb.dto.AddEmployeeRequestDto;
import com.neb.dto.AddEmployeeResponseDto;
import com.neb.dto.AddWorkRequestDto;
import com.neb.dto.EmployeeDetailsResponseDto;
import com.neb.dto.EmployeeResponseDto;
import com.neb.dto.GeneratePayslipRequest;
import com.neb.dto.LoginRequestDto;
import com.neb.dto.PayslipDto;
import com.neb.dto.ResponseMessage;
import com.neb.dto.UpdateEmployeeRequestDto;
import com.neb.dto.UpdateEmployeeResponseDto;
import com.neb.dto.WorkResponseDto;
import com.neb.entity.Payslip;
import com.neb.service.AdminService;
import com.neb.service.EmployeeService;
import com.neb.service.HrService;


@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:5173")
public class AdminController {

	@Autowired
	private AdminService adminService;
	@Autowired
	private HrService hrService;
	
	@Autowired
	private EmployeeService employeeService;
	
	@PostMapping("/login")
	public ResponseEntity<ResponseMessage<EmployeeResponseDto>> login(@RequestBody LoginRequestDto loginReq){
		
		EmployeeResponseDto loginRes = adminService.login(loginReq);
		
		return ResponseEntity.ok(new ResponseMessage<EmployeeResponseDto>(HttpStatus.OK.value(), HttpStatus.OK.name(), "admin login successfully", loginRes));
	}
	
	 //for adding admin
	@PostMapping("/addadmin")
	public ResponseEntity<ResponseMessage<AddEmployeeResponseDto>> addAdmin(@RequestBody AddEmployeeRequestDto addEmpReq){
		
		AddEmployeeResponseDto addEmpRes = adminService.addAdmin(addEmpReq);
		
		return ResponseEntity.ok(new ResponseMessage<AddEmployeeResponseDto>(HttpStatus.OK.value(), HttpStatus.OK.name(), "Admin added successfully", addEmpRes));
	}
	
	//for adding Hr
	@PostMapping("/addhr")
	public ResponseEntity<ResponseMessage<AddEmployeeResponseDto>> addEmployee(@RequestBody AddEmployeeRequestDto addEmpReq){
		
		AddEmployeeResponseDto addEmpRes = adminService.addEmployee(addEmpReq);
		
		return ResponseEntity.ok(new ResponseMessage<AddEmployeeResponseDto>(HttpStatus.OK.value(), HttpStatus.OK.name(), "Hr added successfully", addEmpRes));
	}
	
	/**
     * Fetches a list of all employees in the organization.
     * 
     * @return Response containing a list of employee details.
     */
	//get employee list
	@GetMapping("/getEmpList")
	public ResponseEntity<ResponseMessage<List<EmployeeDetailsResponseDto>>> getEmployeeList(){
		
		List<EmployeeDetailsResponseDto> employeeList = adminService.getEmployeeList();
		
		return ResponseEntity.ok(new ResponseMessage<List<EmployeeDetailsResponseDto>>(HttpStatus.OK.value(), HttpStatus.OK.name(), "All Employee fetched successfully", employeeList));
	}
	 @PostMapping(value = "/work/add", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
	 
	    public ResponseEntity<ResponseMessage<String>> addWork(
	        @RequestPart("dto") AddWorkRequestDto dto,
	        @RequestPart(value = "file", required = false) MultipartFile file
	    ) throws IOException {

	        String workRes = adminService.assignWork(dto, file);

	        return ResponseEntity.ok(
	            new ResponseMessage<>(HttpStatus.OK.value(), HttpStatus.OK.name(), "Work added successfully", workRes)
	        );
	    }
        
	    
	    // ✅ Get all Work of employee
	    @GetMapping("/getAllWork/{empId}")
	    public ResponseEntity<ResponseMessage<List<WorkResponseDto>>> getAllWork(@PathVariable Long empId) {
	        List<WorkResponseDto> works = adminService.getAllWorks(empId);
	        return ResponseEntity.ok(new ResponseMessage<>(200, "OK", "All work fetched successfully", works));
	    }
	    
	    //  Get Employee Work
	    @GetMapping("/getWork/{empId}")
	    public ResponseEntity<ResponseMessage<List<WorkResponseDto>>> getWorkByEmployee(@PathVariable Long empId) {
	        List<WorkResponseDto> works = adminService.getWorkByEmployee(empId);
	        return ResponseEntity.ok(new ResponseMessage<>(200, "OK", "Work fetched for employee", works));
	    }
	    // delete  admin by id 
	    @DeleteMapping("/delete/admin/{id}")//http://localhost:5054/api/admin/delete/admin/3
	    public ResponseEntity<ResponseMessage<?>> deleteAdmin(@PathVariable Long id){
	    	String deleteRes = adminService.deleteAdmin(id);
	    	return ResponseEntity.ok(new ResponseMessage<>(200, "OK", "Admin deleted successfully", deleteRes));
	    }
	    // delete hr by id
	    @DeleteMapping("/delete/hr/{id}")//http://localhost:5054/api/admin/delete/hr/3
	    public ResponseEntity<ResponseMessage<?>> deleteHr(@PathVariable Long id){
	    	String deleteRes = adminService.deleteHr(id);
	    	return ResponseEntity.ok(new ResponseMessage<>(200, "OK", "hr deleted successfully", deleteRes));
	    }
	    
//	    @PutMapping("/update/hr/{id}")//http://localhost:5054/api/admin/update/hr/4
//		public ResponseEntity<ResponseMessage<UpdateEmployeeResponseDto>> updateHr(@PathVariable Long id,@RequestBody UpdateEmployeeRequestDto updateReq){
//			
//			UpdateEmployeeResponseDto updatedhrRes = adminService.updateHrDetails(id, updateReq);
//			
//			return ResponseEntity.ok(new ResponseMessage<UpdateEmployeeResponseDto>(HttpStatus.OK.value(), HttpStatus.OK.name(), "hr details updated successfully", updatedhrRes));
//		}
	    
	    @GetMapping("/getEmp/{id}")
		public ResponseEntity<ResponseMessage<EmployeeDetailsResponseDto>> getEmployee(@PathVariable Long id){
			
			EmployeeDetailsResponseDto employee = adminService.getEmployee(id);
		
			
			return ResponseEntity.ok(new ResponseMessage<EmployeeDetailsResponseDto>(HttpStatus.OK.value(), HttpStatus.OK.name(), " Employee fetched successfully", employee));
		}
		
		@GetMapping("/payslip/{id}/download")
	    public ResponseEntity<byte[]> download(@PathVariable Long id) throws Exception {
	        byte[] pdf = hrService.downloadPayslip(id);

	        HttpHeaders headers = new HttpHeaders();
	        headers.setContentType(MediaType.APPLICATION_PDF);
	        headers.setContentDisposition(ContentDisposition
	            .attachment()
	            .filename("payslip_" + id + ".pdf")
	            .build());

	        return ResponseEntity.ok()
	                             .headers(headers)
	                             .body(pdf);
	    }

	    
	    @GetMapping("/payslip/{employeeId}")
	    public ResponseEntity<List<PayslipDto>> listPayslips(@PathVariable Long employeeId) {
	        List<PayslipDto> payslips = hrService.listPayslipsForEmployee(employeeId);
	        return ResponseEntity.ok(payslips);
	    }
	    
	    
	    @PostMapping("/payslip/generate")
	    public ResponseEntity<PayslipDto> generate(@RequestBody GeneratePayslipRequest request) throws Exception {
	        Payslip p = employeeService.generatePayslip(request.getEmployeeId(), request.getMonthYear());
	        PayslipDto dto = PayslipDto.fromEntity(p);
	        return ResponseEntity.ok(dto);
	    }
		
	    @PutMapping("/editEmp/{empId}/{days}")
	    public ResponseEntity<ResponseMessage<EmployeeDetailsResponseDto>> addAttendence(@PathVariable Long empId, @PathVariable int days){
	    	
	    	EmployeeDetailsResponseDto updatedEmp = hrService.addAttendence(empId, days);
	    	
	    	return ResponseEntity.ok(new ResponseMessage<EmployeeDetailsResponseDto>(HttpStatus.OK.value(), HttpStatus.OK.name(), "employee details updated", updatedEmp));
	    }
	    
	    @GetMapping("/reports/daily")
	    public ResponseEntity<byte[]> generateReport() throws Exception {
	    		    	
	    	LocalDate date = LocalDate.of(2025, 11, 05);
	        byte[] pdfBytes = adminService.generateDailyReport(date);
	        HttpHeaders headers = new HttpHeaders();
	        headers.setContentType(MediaType.APPLICATION_PDF);
	        headers.setContentDisposition(ContentDisposition
	            .attachment()
	            .filename("DailyReport_" + date + ".pdf")
	            .build());
	        System.out.println("pdf generated");
	        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
	    }
	    @GetMapping("/hr-list")
	    public ResponseEntity<Page<EmployeeDetailsResponseDto>> getAllHrs(
	            @RequestParam(defaultValue = "0") int page,
	            @RequestParam(defaultValue = "10") int size,
	            @RequestParam(defaultValue = "id") String sort) {

	        Page<EmployeeDetailsResponseDto> hrPage = adminService.getHrList(page, size, sort);

	        return ResponseEntity.ok(hrPage);
	    }
	    @PostMapping("/logout")
	    public ResponseEntity<ResponseMessage<String>> logout() {

	        return ResponseEntity.ok(
	                new ResponseMessage<>(
	                        HttpStatus.OK.value(),
	                        HttpStatus.OK.name(),
	                        "Logout successful",
	                        "Admin logged out successfully"
	                )
	        );
	    }
//	    @GetMapping("/hr/{id}")
//	    public ResponseEntity<ResponseMessage<EmployeeDetailsResponseDto>> getHrById(@PathVariable Long id) {
//	        EmployeeDetailsResponseDto hr = adminService.getHrById(id);
//	        return ResponseEntity.ok(new ResponseMessage<>(HttpStatus.OK.value(), HttpStatus.OK.name(), "HR details fetched", hr));
//	    }

	 // file: com/neb/controller/AdminController.java
	 // Replace your previous update-hr endpoint with:

	 @PutMapping("/update/hr/{id}")
	 public ResponseEntity<ResponseMessage<EmployeeDetailsResponseDto>> updateHrDetails(
	         @PathVariable Long id,
	         @RequestBody UpdateEmployeeRequestDto updateReq) {

	     EmployeeDetailsResponseDto updatedHr = adminService.updateHrDetails(id, updateReq);

	     return ResponseEntity.ok(
	         new ResponseMessage<>(
	             HttpStatus.OK.value(),
	             HttpStatus.OK.name(),
	             "HR details updated successfully",
	             updatedHr
	         )
	     );
	 }

 
	    
}
