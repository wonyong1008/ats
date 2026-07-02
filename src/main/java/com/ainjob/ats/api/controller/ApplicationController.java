package com.ainjob.ats.api.controller;

import com.ainjob.ats.api.dto.PassedApplicantsResponse;
import com.ainjob.ats.api.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/companies/{companyId}/job-postings/{jobPostingId}/applicants")
public class ApplicationController {

    private final ApplicationService applicationService;

    @GetMapping("/passed")
    public ResponseEntity<PassedApplicantsResponse> getPassedApplicants(
            @PathVariable Long companyId,
            @PathVariable Long jobPostingId
    ) {
        return ResponseEntity.ok(applicationService.getPassedApplicants(companyId, jobPostingId));
    }
}
