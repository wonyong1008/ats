package com.ainjob.ats.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ApplicantSearchRequest {
    private Integer stageType;
    private Integer degreeType;
    private Long majorId;
    private Integer minExpYears;
    private List<Long> skillIds;
}
