package com.ainjob.ats.domain.job;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JobPostingRepository extends JpaRepository<JobPosting, Long> {

    Optional<JobPosting> findByIdAndCompanyId(Long id, Long companyId);
}
