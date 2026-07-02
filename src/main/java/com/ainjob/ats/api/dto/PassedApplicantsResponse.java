package com.ainjob.ats.api.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PassedApplicantsResponse {

    private Long companyId;
    private Long jobPostingId;
    private String jobTitle;
    private int totalCount;
    private List<ApplicantItem> passedApplicants;

    @Getter
    @Builder
    public static class ApplicantItem {
        private Long applicantId;
        private String name;
        private String email;

        public static ApplicantItem from(PassedApplicantDto dto) {
            return ApplicantItem.builder()
                    .applicantId(dto.getApplicantId())
                    .name(dto.getApplicantName())
                    .email(dto.getEmail())
                    .build();
        }
    }
}
