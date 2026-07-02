package com.ainjob.ats.api.controller;

import com.ainjob.ats.api.dto.ApplicantSearchResponse;
import com.ainjob.ats.api.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/companies/{companyId}/applicants")
public class ApplicantController {

    private final ApplicationService applicationService;

    @GetMapping
    public ResponseEntity<ApplicantSearchResponse> searchApplicants(
            @PathVariable Long companyId,
            @RequestParam(required = false) Integer stageType,
            @RequestParam(required = false) Integer degreeType,
            @RequestParam(required = false) Long majorId,
            @RequestParam(required = false) Integer minExpYears,
            @RequestParam(required = false) List<Long> skillIds
    ) {
        return ResponseEntity.ok(
                applicationService.searchApplicants(companyId, stageType, degreeType, majorId, minExpYears, skillIds)
        );
    }
}
