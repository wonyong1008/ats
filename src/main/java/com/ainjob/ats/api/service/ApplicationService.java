package com.ainjob.ats.api.service;

import com.ainjob.ats.api.dto.ApplicantSearchDto;
import com.ainjob.ats.api.dto.ApplicantSearchResponse;
import com.ainjob.ats.api.dto.PassedApplicantDto;
import com.ainjob.ats.api.dto.PassedApplicantsResponse;
import com.ainjob.ats.api.dto.StageChangeRequest;
import com.ainjob.ats.api.dto.StageChangeResponse;
import com.ainjob.ats.common.enums.StageType;
import com.ainjob.ats.common.exception.BusinessException;
import com.ainjob.ats.common.exception.NotFoundException;
import com.ainjob.ats.domain.ats.AppHistory;
import com.ainjob.ats.domain.ats.AppHistoryRepository;
import com.ainjob.ats.domain.ats.Application;
import com.ainjob.ats.domain.ats.ApplicationRepository;
import com.ainjob.ats.domain.ats.Stage;
import com.ainjob.ats.domain.ats.StageRepository;
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
    private final StageRepository stageRepository;
    private final AppHistoryRepository appHistoryRepository;
    private final EmailNotificationService emailNotificationService;

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

    @Transactional
    public StageChangeResponse changeStage(Long companyId, Long applicationId, StageChangeRequest req) {
        Application app = applicationRepository.findByIdAndCompanyId(applicationId, companyId)
                .orElseThrow(() -> new NotFoundException("Application not found: " + applicationId));

        Stage fromStage = app.getCurrentStage();
        Stage toStage = stageRepository.findByIdAndCompanyId(req.getToStageId(), companyId)
                .orElseThrow(() -> new NotFoundException("Stage not found: " + req.getToStageId()));

        validateTransition(fromStage.getStageType(), toStage.getStageType());

        app.changeStage(toStage);
        AppHistory history = appHistoryRepository.save(
                AppHistory.of(app, fromStage, toStage, "system", req.getNote()));

        // 이메일은 트랜잭션 커밋 후 비동기 발송 — API 응답 지연 없음
        String email = app.getApplicant().getEmail();
        String name  = app.getApplicant().getName();
        String title = app.getJobPosting().getTitle();
        emailNotificationService.sendStageChangeNotification(email, name, title, toStage.getStageType(), toStage.getName());

        return StageChangeResponse.builder()
                .applicationId(applicationId)
                .applicantName(name)
                .fromStageName(fromStage.getName())
                .toStageName(toStage.getName())
                .changedAt(history.getChangedAt())
                .build();
    }

    private void validateTransition(StageType from, StageType to) {
        boolean valid = switch (from) {
            case DOCUMENT_RECEIVED   -> to == StageType.DOCUMENT_PASSED || to == StageType.DOCUMENT_FAILED || to == StageType.CANCELLED;
            case DOCUMENT_PASSED     -> to == StageType.INTERVIEW_SCHEDULED || to == StageType.CANCELLED;
            case INTERVIEW_SCHEDULED -> to == StageType.INTERVIEW_DONE || to == StageType.CANCELLED;
            case INTERVIEW_DONE      -> to == StageType.FINAL_PASSED || to == StageType.FINAL_FAILED || to == StageType.CANCELLED;
            case CANCELLED           -> to == StageType.DOCUMENT_RECEIVED;
            default                  -> false; // DOCUMENT_FAILED, FINAL_PASSED, FINAL_FAILED: terminal
        };
        if (!valid) {
            throw new BusinessException(
                    from.name() + " → " + to.name() + " 전이는 허용되지 않습니다.");
        }
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
