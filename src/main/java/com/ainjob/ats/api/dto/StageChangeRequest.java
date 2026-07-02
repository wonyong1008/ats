package com.ainjob.ats.api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StageChangeRequest {

    @NotNull
    private Long toStageId;

    private String note;
}
