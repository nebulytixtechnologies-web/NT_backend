package com.neb.service.impl;

import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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
import com.neb.entity.DailyReport;
import com.neb.entity.Employee;
import com.neb.entity.Job;
import com.neb.entity.JobApplication;
import com.neb.entity.Payslip;
import com.neb.exception.CustomeException;
import com.neb.repo.DailyReportRepository;
import com.neb.repo.EmployeeRepository;
import com.neb.repo.JobApplicationRepository;
import com.neb.repo.JobRepository;
import com.neb.repo.PayslipRepository;
import com.neb.service.EmailService;
import com.neb.service.HrService;
import com.neb.util.ReportGeneratorPdf;


@Service
public class HrServiceImpl implements HrService{

	@Autowired
    private EmployeeRepository empRepo;
	
	@Autowired
	private PayslipRepository payslipRepo;
	
	@Autowired
	private JobRepository jobRepository;
	
	@Autowired
	private DailyReportRepository dailyReportRepository;

    @Autowired
    private ModelMapper mapper;
    @Autowired
    private EmailService emailService;
    @Autowired
    private JobApplicationRepository jobApplicationRepository;
    
    @Value("${daily-report.folder-path}")
    private String dailyReportFolderPath;
    
                             // --------- LOGIN ----------
    
    @Override
    public EmployeeDetailsResponseDto login(LoginRequestDto loginReq) {

        // fetch employee from DB
        Employee emp = empRepo.findByEmailAndPasswordAndLoginRole(
                loginReq.getEmail(),
                loginReq.getPassword(),
                loginReq.getLoginRole()
        ).orElseThrow(() -> new CustomeException("Invalid credentials. Please check your email and password and login role"));

        // map entity to DTO
        EmployeeDetailsResponseDto loginRes = mapper.map(emp, EmployeeDetailsResponseDto.class);

        return loginRes;
    }
    
	
    @Override
    public AddEmployeeResponseDto addEmployee(AddEmployeeRequestDto addEmpReq) {

        // check if employee with same email already exists
        if (empRepo.existsByEmail(addEmpReq.getEmail())) {
        	 throw new CustomeException("Employee with email " + addEmpReq.getEmail() + " already exists");
        }

        // map DTO to entity
       
        Employee emp = mapper.map(addEmpReq, Employee.class);
        emp.setLoginRole("employee");

        // save entity
        Employee savedEmp = empRepo.save(emp);
      

        // map saved entity to response DTO
        AddEmployeeResponseDto addEmpRes = mapper.map(savedEmp, AddEmployeeResponseDto.class);

        return addEmpRes;
    }
                                       // --------- EMPLOYEE LIST SECTION ----------
   
    @Override
    public List<EmployeeDetailsResponseDto> getEmployeeList() {
		
		//getting all employee list without admin
	    List<Employee> employeeList = empRepo.findByLoginRoleNotIn(List.of("admin","hr"));
	    
	    if(employeeList==null) {
	    	 throw new CustomeException("Employees list not found");
	    }
	    
	    List<EmployeeDetailsResponseDto> empListRes = employeeList.stream().map(emp->{
	    	
	    	EmployeeDetailsResponseDto empResDto = mapper.map(emp, EmployeeDetailsResponseDto.class);
	    	return empResDto;
	    }).collect(Collectors.toList());
	    
	    return empListRes;
	}
        // --------- GET SINGLE EMPLOYEE SECTION ----------
   
	@Override
	public EmployeeDetailsResponseDto getEmployee(Long id) {

		Employee emp = empRepo.findById(id).orElseThrow(()->new CustomeException("Employee not founce wuith id :"+id));
		return mapper.map(emp, EmployeeDetailsResponseDto.class);
		
	}
	      // ---------  DELETE EMPLOYEE SECTION ----------
	
	@Override
	public String deleteById(Long id) {
		empRepo.deleteById(id);
		return id+" Employee Deleted Successfully";
	}
	
	       // ---------  PAYSLIP DOWNLOAD SECTION ----------
	
	@Override
	 public byte[] downloadPayslip(Long payslipId) throws Exception {
        Payslip p = payslipRepo.findById(payslipId)
            .orElseThrow(() -> new CustomeException("Payslip not found"));

        Path path = Paths.get(p.getPdfPath());
        return Files.readAllBytes(path);
    }
	 
	 //getting list of payslips of employee using employee id
	@Override
     public List<PayslipDto> listPayslipsForEmployee(Long employeeId) {
        List<Payslip> payslips = payslipRepo.findByEmployeeId(employeeId);
        
        if(payslips==null) {
        	throw new CustomeException("payslip list is not found with employeeId: "+employeeId);
        }
        List<PayslipDto> paySlipDtos = payslips.stream()
                                        .map(PayslipDto::fromEntity)
                                        .toList();
        return paySlipDtos;
    }
	                              // ---------  ATTENDANCE SECTION  ----------
	@Override
	public EmployeeDetailsResponseDto addAttendence(Long id, int days) {
		
		Employee emp = empRepo.findById(id).orElseThrow(()->new CustomeException("employee not found with id:"+id));
		emp.setDaysPresent(days);
		Employee savedemp = empRepo.save(emp);
		EmployeeDetailsResponseDto updateEmpDto= mapper.map(savedemp, EmployeeDetailsResponseDto.class);
		return updateEmpDto;
	}

                          
	
	@Override
	public EmployeeDetailsResponseDto updateEmployee(Long id, UpdateEmployeeRequestDto updateReq) {
	    Employee emp = empRepo.findById(id)
	            .orElseThrow(() -> new CustomeException("Employee not found with id: " + id));

	    // Basic string fields
	    if (updateReq.getFirstName() != null && !updateReq.getFirstName().isEmpty())
	        emp.setFirstName(updateReq.getFirstName());

	    if (updateReq.getLastName() != null && !updateReq.getLastName().isEmpty())
	        emp.setLastName(updateReq.getLastName());

	    if (updateReq.getEmail() != null && !updateReq.getEmail().isEmpty())
	        emp.setEmail(updateReq.getEmail());

	    if (updateReq.getMobile() != null && !updateReq.getMobile().isEmpty())
	        emp.setMobile(updateReq.getMobile());

	    if (updateReq.getCardNumber() != null && !updateReq.getCardNumber().isEmpty())
	        emp.setCardNumber(updateReq.getCardNumber());

	    if (updateReq.getJobRole() != null && !updateReq.getJobRole().isEmpty())
	        emp.setJobRole(updateReq.getJobRole());

	    if (updateReq.getDomain() != null && !updateReq.getDomain().isEmpty())
	        emp.setDomain(updateReq.getDomain());

	    if (updateReq.getGender() != null && !updateReq.getGender().isEmpty())
	        emp.setGender(updateReq.getGender());

	    // Salary (Double wrapper allows null -> no change)
	    if (updateReq.getSalary() != null)
	        emp.setSalary(updateReq.getSalary());

	    // paidLeaves — now using Integer in DTO so null means "no change"
	    if (updateReq.getPaidLeaves() != 0)
	        emp.setPaidLeaves(updateReq.getPaidLeaves());

	    // --- Bank & Tax details ---
	    if (updateReq.getBankAccountNumber() != null && !updateReq.getBankAccountNumber().isEmpty())
	        emp.setBankAccountNumber(updateReq.getBankAccountNumber());

	    if (updateReq.getIfscCode() != null && !updateReq.getIfscCode().isEmpty())
	        emp.setIfscCode(updateReq.getIfscCode());

	    if (updateReq.getBankName() != null && !updateReq.getBankName().isEmpty())
	        emp.setBankName(updateReq.getBankName());

	    if (updateReq.getPfNumber() != null && !updateReq.getPfNumber().isEmpty())
	        emp.setPfNumber(updateReq.getPfNumber());

	    if (updateReq.getPanNumber() != null && !updateReq.getPanNumber().isEmpty())
	        emp.setPanNumber(updateReq.getPanNumber());

	    if (updateReq.getUanNumber() != null && !updateReq.getUanNumber().isEmpty())
	        emp.setUanNumber(updateReq.getUanNumber());

	    if (updateReq.getEpsNumber() != null && !updateReq.getEpsNumber().isEmpty())
	        emp.setEpsNumber(updateReq.getEpsNumber());

	    if (updateReq.getEsiNumber() != null && !updateReq.getEsiNumber().isEmpty())
	        emp.setEsiNumber(updateReq.getEsiNumber());

	    Employee updatedEmp = empRepo.save(emp);
	    return mapper.map(updatedEmp, EmployeeDetailsResponseDto.class);
	}
    
    @Override
    public EmployeeDetailsResponseDto updatePassword(Long id, UpdatePasswordRequestDto updatePasswordRequestDto) {
       
        Employee emp = empRepo.findById(id)
                .orElseThrow(() -> new CustomeException("Employee not found with id: " + id));

       
        emp.setPassword(updatePasswordRequestDto.getPassword());

     
        Employee updatedEmp = empRepo.save(emp);

       
        return mapper.map(updatedEmp, EmployeeDetailsResponseDto.class);
    }

	@Override
	public JobDetailsDto addJob(AddJobRequestDto jobRequestDto) {
		
		//dto to entiry
		Job job = mapper.map(jobRequestDto, Job.class);
		job.setIsActive(true);
		
		LocalDate postedDate = jobRequestDto.getPostedDate() != null
                ? jobRequestDto.getPostedDate()
                : LocalDate.now();

		job.setPostedDate(postedDate);
		Job saveJob = jobRepository.save(job);
		
		JobDetailsDto jobDetailsRes = mapper.map(saveJob, JobDetailsDto.class);
		
		return jobDetailsRes;
	}

	@Override
	public List<JobDetailsDto> getAllJobs() {
		
		List<Job> allJobs = jobRepository.findAll();
		LocalDate today = LocalDate.now();
		
		List<JobDetailsDto> jobListRes= allJobs.stream().map(job->{
			if (job.getClosingDate() != null && job.getClosingDate().isBefore(today)) {
                job.setIsActive(false);
            } else {
                job.setIsActive(true);
            }
			return mapper.map(job, JobDetailsDto.class);
		}).collect(Collectors.toList());
		
		
		return jobListRes;
	}

	@Override
    public String generateDailyReport(LocalDate reportDate) {

        List<DailyReport> reports = dailyReportRepository.findByReportDate(reportDate);

        if (reports.isEmpty()) {
            return "No daily reports found for date: " + reportDate;
        }

        ReportGeneratorPdf reportGeneratorPdf = new ReportGeneratorPdf();

        try {
            // Generate PDF bytes
            byte[] pdfBytes = reportGeneratorPdf.generateDailyReportForEmployees(reports, reportDate);

            // Use Path for robust path handling
            Path folder = Paths.get(dailyReportFolderPath);
            // Create folder if not exists
            if (!Files.exists(folder)) {
                Files.createDirectories(folder);
            }

            String fileName = "daily-report-" + reportDate.toString() + ".pdf";
            Path filePath = folder.resolve(fileName);

            // Write / Replace PDF (atomic write option if desired)
            try (OutputStream os = Files.newOutputStream(filePath)) {
                os.write(pdfBytes);
            }

            // Construct file URL that matches your resource handler mapping
            String fileUrl = "/reports/daily/" + fileName;

            // Save URL to DB for each report of that date
            for (DailyReport dr : reports) {
                dr.setDailyReportUrl(fileUrl);
            }
            dailyReportRepository.saveAll(reports);

            // return the URL so controller can send it back to frontend
            return fileUrl;

        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomeException("Failed to generate PDF: " + e.getMessage());
        }
    }

    @Override
    public String getDailyReportUrl(LocalDate reportDate) {
        List<DailyReport> reports = dailyReportRepository.findByReportDate(reportDate);
        if (reports.isEmpty()) {
            return null;
        }
        return reports.get(0).getDailyReportUrl();
    }
	
	@Override
	public void updateJobApplicationStatus(Long applicationId, Boolean status) {
		
		JobApplication app = jobApplicationRepository.findById(applicationId)
	            .orElseThrow(() -> new RuntimeException("Application not found"));

	    if (status) {  // TRUE = SHORTLISTED
	        app.setStatus("SHORTLISTED");

	        emailService.sendApplicationMail(
	                app.getEmail(),
	                "Congratulations! You Have Been Shortlisted 🎉",
	                "Dear " + app.getFullName() + ",\n\n"
	                + "We are pleased to inform you that your application has been *shortlisted* for the next stage of the recruitment process at "
	                + "NEBULYTIX TECHNOLOGIES PRIVATE LIMITED.\n\n"
	                + "Your skills and experience stood out among many applicants, and we truly appreciate the time you invested in applying.\n\n"
	                + "Our HR team will reach out to you soon with details regarding the next round, including schedule, instructions, and documentation (if required).\n\n"
	                + "If you have any queries, feel free to contact us:\n"
	                + "📧 hr@nebulytixtechnologies.com\n"
	                + "📞 7660999155 / 8125263737\n\n"
	                + "We wish you the very best for the upcoming steps!\n\n"
	                + "Warm Regards,\n"
	                + "HR Team\n"
	                + "NEBULYTIX TECHNOLOGIES PRIVATE LIMITED"
	        );

	    } else { // FALSE = REJECTED
	        app.setStatus("REJECTED");

	        emailService.sendApplicationMail(
	                app.getEmail(),
	                "Update on Your Application",
	                "Dear " + app.getFullName() + ",\n\n"
	                + "Thank you for your interest in joining NEBULYTIX TECHNOLOGIES PRIVATE LIMITED and for the time you spent applying.\n\n"
	                + "After careful consideration, we regret to inform you that your application has not been shortlisted at this stage.\n\n"
	                + "This decision was difficult, as we received many strong applications. "
	                + "We truly appreciate your effort and encourage you to apply again in the future when suitable opportunities arise.\n\n"
//	                + "If you have any queries or need further clarification, you may contact us:\n"
//	                + "📧 hr@nebulytixtechnologies.com\n"
//	                + "📞 7660999155 / 8125263737\n\n"
	                + "Thank you once again for considering us. We wish you success in all your future endeavours.\n\n"
	                + "Warm Regards,\n"
	                + "HR Team\n"
	                + "NEBULYTIX TECHNOLOGIES PRIVATE LIMITED"
	        );
	    }

	    jobApplicationRepository.save(app);
	    
		
	}
	@Override
	public String deleteJob(Long jobId) {

	    Job job = jobRepository.findById(jobId)
	            .orElseThrow(() -> new RuntimeException("Job not found"));

	    jobRepository.delete(job);

	    return "Job with ID " + jobId + " deleted successfully";
	}

}
