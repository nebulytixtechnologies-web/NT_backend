package com.neb.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.neb.constants.WorkStatus;
import com.neb.dto.AddEmployeeRequestDto;
import com.neb.dto.AddEmployeeResponseDto;
import com.neb.dto.AddWorkRequestDto;
import com.neb.dto.EmployeeDetailsResponseDto;
import com.neb.dto.EmployeeResponseDto;
import com.neb.dto.LoginRequestDto;
import com.neb.dto.UpdateEmployeeRequestDto;
import com.neb.dto.UpdateEmployeeResponseDto;
import com.neb.dto.WorkResponseDto;
import com.neb.entity.Employee;
import com.neb.entity.Work;
import com.neb.exception.CustomeException;
import com.neb.repo.EmployeeRepository;
import com.neb.repo.WorkRepository;
import com.neb.service.AdminService;
import com.neb.util.ReportGeneratorPdf;


@Service
public class AdminServiceImpl implements AdminService{

	@Autowired
    private EmployeeRepository empRepo;
	
	@Autowired
    private WorkRepository workRepo;

    @Autowired
    private ModelMapper mapper;
    
    @Value("${task.attachment}")
    private String uploadDir;
    
     // --------- LOGIN ----------
    @Override
    public EmployeeResponseDto login(LoginRequestDto loginReq) {

        // fetch employee from DB
        Employee emp = empRepo.findByEmailAndPasswordAndLoginRole(
                loginReq.getEmail(),
                loginReq.getPassword(),
                loginReq.getLoginRole()	
        ).orElseThrow(() -> new CustomeException("Invalid credentials. Please check your email and password and login role"));
       
        // map entity to DTO
        EmployeeResponseDto loginRes = mapper.map(emp, EmployeeResponseDto.class);

        return loginRes;
    }
    
   
    // --------- ADD EMPLOYEE ----------
    @Override
    public AddEmployeeResponseDto addEmployee(AddEmployeeRequestDto addEmpReq) {

        // check if employee with same email already exists
        if (empRepo.existsByEmail(addEmpReq.getEmail())) {
        	throw new CustomeException("Admin with email :" + addEmpReq.getEmail() + " already exists");
        }

        // map DTO to entity
        Employee emp = mapper.map(addEmpReq, Employee.class);
        emp.setLoginRole("hr");

        // save entity
        Employee savedEmp = empRepo.save(emp);

        // map saved entity to response DTO
        AddEmployeeResponseDto addEmpRes = mapper.map(savedEmp, AddEmployeeResponseDto.class);

        return addEmpRes;
    }
     
     //  ----------Get Employee List-------------
	@Override
	public List<EmployeeDetailsResponseDto> getEmployeeList() {
		
		//getting all employee list without admin
		  List<Employee> employeeList=	empRepo.findByLoginRoleNotIn(List.of("admin","hr"));	    
	    if(employeeList==null) {
	    	throw new CustomeException("Employees not found");
	    }
	    
	    List<EmployeeDetailsResponseDto> empListRes = employeeList.stream().map(emp->{
	    	
	    	EmployeeDetailsResponseDto empResDto = mapper.map(emp, EmployeeDetailsResponseDto.class);
	    	return empResDto;
	    }).collect(Collectors.toList());
	    
	    return empListRes;
	}
	
           //............. adding work ..............
    public String assignWork(AddWorkRequestDto request,MultipartFile file) {
        Employee emp = empRepo.findById(request.getEmployeeId())
                .orElseThrow(() -> new CustomeException("Employee not found with id :"+request.getEmployeeId()));

        Work work = new Work();
        work.setTitle(request.getTitle());
        work.setDescription(request.getDescription());
        work.setAssignedDate(LocalDate.now());
        work.setDueDate(request.getDueDate());
        work.setStatus(WorkStatus.ASSIGNED);
        work.setEmployee(emp);
        
        if (file != null && !file.isEmpty()) {
            // validate PDF
            if (!"application/pdf".equals(file.getContentType())) {
                throw new CustomeException("Only PDF attachment allowed");
            }
            
            try {
            	// ensure directory exists
                Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
                Files.createDirectories(uploadPath);

                String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
                // optionally add unique suffix
                String fileName = System.currentTimeMillis() + "_" + originalFilename;
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                // set URL or path in work
                work.setAttachmentUrl("/uploads/tasks/" + fileName);

            }catch (IOException ex) {
                throw new CustomeException("Could not store file. Error: " + ex.getMessage());
            }
        }
        
        Work savedWork = workRepo.save(work);
        
        if(savedWork!=null) {
        	return "Task Assigned Successfully";
        }
        else
        return "failed to assign task";
    }

    public List<WorkResponseDto> getAllWorks(Long empId) {
    	List<Work> allWork = workRepo.findByEmployeeId(empId);
    	if(allWork==null) {
    		throw new CustomeException("works not found");
    	}
        return allWork
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    	
    }

    public List<WorkResponseDto> getWorkByEmployee(Long empId) {
    	
    	List<Work> workListByEmployeeId = workRepo.findByEmployeeId(empId);
    	if(workListByEmployeeId==null) {
    		throw new CustomeException("works not found for employee with employee id :"+empId);
    	}
        return workListByEmployeeId
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    
    }
       
    private WorkResponseDto mapToDto(Work work) {
        WorkResponseDto dto = new WorkResponseDto();
        dto.setId(work.getId());
        dto.setTitle(work.getTitle());
        dto.setDescription(work.getDescription());
        dto.setAssignedDate(work.getAssignedDate());
        dto.setDueDate(work.getDueDate());
        dto.setStatus(work.getStatus());
        dto.setReportDetails(work.getReportDetails());
        dto.setSubmittedDate(work.getSubmittedDate());
        dto.setEmployeeId(work.getEmployee().getId());
        dto.setEmployeeName(work.getEmployee().getFirstName() + " " + work.getEmployee().getLastName());
        dto.setEmployeeEmail(work.getEmployee().getEmail());
        dto.setAttachmentUrl(work.getAttachmentUrl());
        dto.setReportAttachmentUrl(work.getReportAttachmentUrl());
        return dto;
    }

	@Override
	public String deleteHr(Long id) {
		
		Optional<Employee> emp = empRepo.findById(id);	
		if(emp.isPresent()) {
			empRepo.deleteById(id);
			return "Hr deleted with id:"+id;
		}
		else {
			throw new CustomeException("Hr not found with id :"+id);
		}
	}

//	@Override
//	public UpdateEmployeeResponseDto updateHrDetails(Long empId, UpdateEmployeeRequestDto updateReq) {
//		
//		Employee hr = empRepo.findById(empId).orElseThrow(()->new CustomeException("hr not found with id :"+empId));
//		hr.setFirstName(updateReq.getFirstName());
//		hr.setLastName(updateReq.getLastName());
//		hr.setEmail(updateReq.getEmail());
//		hr.setMobile(updateReq.getMobile());
//		hr.setCardNumber(updateReq.getCardNumber());
//		hr.setLoginRole(updateReq.getLoginRole());
//		hr.setJobRole(updateReq.getJobRole());
//		hr.setDomain(updateReq.getDomain());
//		hr.setGender(updateReq.getGender());
//		hr.setJoiningDate(updateReq.getJoiningDate());
//		hr.setSalary(updateReq.getSalary());
//		hr.setDaysPresent(updateReq.getDaysPresent());
//		hr.setPaidLeaves(updateReq.getPaidLeaves());
//		hr.setBankAccountNumber(updateReq.getBankAccountNumber());
//		hr.setBankName(updateReq.getBankName());
//		hr.setPfNumber(updateReq.getPfNumber());
//		hr.setPanNumber(updateReq.getPanNumber());
//		hr.setUanNumber(updateReq.getUanNumber());
//		hr.setEpsNumber(updateReq.getEpsNumber());
//		hr.setEsiNumber(updateReq.getEsiNumber());
//		
//		Employee emp = empRepo.save(hr);
//		UpdateEmployeeResponseDto updateHrRes = mapper.map(emp, UpdateEmployeeResponseDto.class);
//		return updateHrRes;
//	}
	@Override
	public EmployeeDetailsResponseDto getEmployee(Long id) {

		Employee emp = empRepo.findById(id).orElseThrow(()->new CustomeException("Employee not found wuith id :"+id));
		return mapper.map(emp, EmployeeDetailsResponseDto.class);	
	}
	@Override
	public byte[] generateDailyReport(LocalDate date) throws Exception {
	    List<Work> works = workRepo.findBySubmittedDate(date);
	    if (works == null || works.isEmpty()) {
	        // error handling
	    }
	    ReportGeneratorPdf pdfGenerator = new ReportGeneratorPdf();
	    byte[] dailyReportPDF = pdfGenerator.generateReportPDF(works, date);
	    return dailyReportPDF;
	}


	// add  admin 
	@Override
	public AddEmployeeResponseDto addAdmin(AddEmployeeRequestDto addEmpReq) 
	{
		 // check if employee with same email already exists
        if (empRepo.existsByEmail(addEmpReq.getEmail())) {
        	throw new CustomeException("Admin with email :" + addEmpReq.getEmail() + " already exists");
        }

        // map DTO to entity
        Employee emp = mapper.map(addEmpReq, Employee.class);
        emp.setLoginRole("admin");

        // save entity
        Employee savedEmp = empRepo.save(emp);

        // map saved entity to response DTO
        AddEmployeeResponseDto addEmpRes = mapper.map(savedEmp, AddEmployeeResponseDto.class);

        return addEmpRes;
		
	}

     // delete the admin based on ID
	@Override
	public String deleteAdmin(Long id)
	{

		Optional<Employee> emp = empRepo.findById(id);	
		if(emp.isPresent()) {
			empRepo.deleteById(id);
			return "Admin deleted with id:"+id;
		}
		else {
			throw new CustomeException("Admin not found with id :"+id);
		}
	}
	
	
	}
