package com.ainjob.ats.api.controller;

import com.ainjob.ats.api.dto.StageChangeRequest;
import com.ainjob.ats.api.dto.StageChangeResponse;
import com.ainjob.ats.api.service.ApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/companies/{companyId}/applications/{applicationId}")
public class StageChangeController {

    private final ApplicationService applicationService;

    @PatchMapping("/stage")
    public ResponseEntity<StageChangeResponse> changeStage(
            @PathVariable Long companyId,
            @PathVariable Long applicationId,
            @Valid @RequestBody StageChangeRequest request
    ) {
        return ResponseEntity.ok(applicationService.changeStage(companyId, applicationId, request));
    }
}
