package com.neb.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.neb.entity.JobApplication;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long>{

	boolean existsByEmailIgnoreCase(String email);
	Optional<JobApplication> findByEmailIgnoreCase(String email);
	List<JobApplication> findByJob_Id(Long jobId);
}
