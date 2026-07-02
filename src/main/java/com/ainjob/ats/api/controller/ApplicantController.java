package com.ainjob.ats.api.controller;

import com.ainjob.ats.api.dto.ApplicantSearchRequest;
import com.ainjob.ats.api.dto.ApplicantSearchResponse;
import com.ainjob.ats.api.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/companies/{companyId}/applicants")
public class ApplicantController {

    private final ApplicationService applicationService;

    @GetMapping
    public ResponseEntity<ApplicantSearchResponse> searchApplicants(
            @PathVariable Long companyId,
            @ModelAttribute ApplicantSearchRequest request
    ) {
        return ResponseEntity.ok(applicationService.searchApplicants(companyId, request));
    }
}
