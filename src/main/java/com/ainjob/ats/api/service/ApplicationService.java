package com.ainjob.ats.api.service;

import com.ainjob.ats.api.dto.ApplicantSearchDto;
import com.ainjob.ats.api.dto.ApplicantSearchResponse;
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

    public ApplicantSearchResponse searchApplicants(
            Long companyId, Integer stageType, Integer degreeType,
            Long majorId, Integer minExpYears, List<Long> skillIds) {

        if (!companyRepository.existsById(companyId)) {
            throw new NotFoundException("Company not found: " + companyId);
        }

        int minExpMonths = (minExpYears != null) ? minExpYears * 12 : 0;
        List<Long> effectiveSkillIds = (skillIds == null || skillIds.isEmpty()) ? List.of(-1L) : skillIds;
        int skillCount = (skillIds == null || skillIds.isEmpty()) ? 0 : skillIds.size();

        List<ApplicantSearchDto> results = applicationRepository.searchApplicants(
                companyId, stageType, degreeType, majorId, minExpMonths,
                effectiveSkillIds, skillCount);

        return ApplicantSearchResponse.builder()
                .companyId(companyId)
                .totalCount(results.size())
                .applicants(results.stream()
                        .map(ApplicantSearchResponse.ApplicantItem::from)
                        .toList())
                .build();
    }
}
