package com.ainjob.ats.api.service;

import com.ainjob.ats.api.dto.PassedApplicantDto;
import com.ainjob.ats.api.dto.PassedApplicantsResponse;
import com.ainjob.ats.common.exception.NotFoundException;
import com.ainjob.ats.domain.ats.ApplicationRepository;
import com.ainjob.ats.domain.company.CompanyRepository;
import com.ainjob.ats.domain.job.JobPosting;
import com.ainjob.ats.domain.job.JobPostingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final CompanyRepository companyRepository;
    private final JobPostingRepository jobPostingRepository;

    public PassedApplicantsResponse getPassedApplicants(Long companyId, Long jobPostingId) {
        if (!companyRepository.existsById(companyId)) {
            throw new NotFoundException("Company not found: " + companyId);
        }

        JobPosting jobPosting = jobPostingRepository.findByIdAndCompanyId(jobPostingId, companyId)
                .orElseThrow(() -> new NotFoundException("JobPosting not found: " + jobPostingId));

        List<PassedApplicantDto> results = applicationRepository.findPassedApplicants(companyId, jobPostingId);

        return PassedApplicantsResponse.builder()
                .companyId(companyId)
                .jobPostingId(jobPostingId)
                .jobTitle(jobPosting.getTitle())
                .totalCount(results.size())
                .passedApplicants(results.stream()
                        .map(PassedApplicantsResponse.ApplicantItem::from)
                        .toList())
                .build();
    }
}
