package com.ainjob.ats.api.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class StageChangeResponse {
    private Long applicationId;
    private String applicantName;
    private String fromStageName;
    private String toStageName;
    private LocalDateTime changedAt;
}
