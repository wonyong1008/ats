package com.ainjob.ats.common.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum JobPostingStatus {
    DRAFT(1), OPEN(2), CLOSED(3);

    private final int code;

    public static JobPostingStatus of(int code) {
        return Arrays.stream(values())
                .filter(e -> e.code == code)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown JobPostingStatus code: " + code));
    }

    @Converter(autoApply = true)
    public static class EnumConverter implements AttributeConverter<JobPostingStatus, Integer> {
        @Override public Integer convertToDatabaseColumn(JobPostingStatus e) { return e == null ? null : e.code; }
        @Override public JobPostingStatus convertToEntityAttribute(Integer c) { return c == null ? null : JobPostingStatus.of(c); }
    }
}
