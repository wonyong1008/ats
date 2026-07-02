package com.ainjob.ats.common.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum JobCategory {
    BACKEND(1), FRONTEND(2), FULLSTACK(3), DATA(4), DEVOPS(5);

    private final int code;

    public static JobCategory of(int code) {
        return Arrays.stream(values())
                .filter(e -> e.code == code)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown JobCategory code: " + code));
    }

    @Converter(autoApply = true)
    public static class EnumConverter implements AttributeConverter<JobCategory, Integer> {
        @Override public Integer convertToDatabaseColumn(JobCategory e) { return e == null ? null : e.code; }
        @Override public JobCategory convertToEntityAttribute(Integer c) { return c == null ? null : JobCategory.of(c); }
    }
}
