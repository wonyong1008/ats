package com.ainjob.ats.api.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ApplicantSearchResponse {
    private Long companyId;
    private int totalCount;
    private List<ApplicantItem> applicants;

    @Getter
    @Builder
    public static class ApplicantItem {
        private Long applicantId;
        private String name;
        private String email;
        private String stageName;
        private Integer stageType;

        public static ApplicantItem from(ApplicantSearchDto dto) {
            return ApplicantItem.builder()
                    .applicantId(dto.getApplicantId())
                    .name(dto.getApplicantName())
                    .email(dto.getEmail())
                    .stageName(dto.getStageName())
                    .stageType(dto.getStageType())
                    .build();
        }
    }
}
