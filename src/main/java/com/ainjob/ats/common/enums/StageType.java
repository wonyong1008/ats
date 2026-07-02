package com.ainjob.ats.common.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum StageType {
    DOCUMENT_RECEIVED(1),
    DOCUMENT_PASSED(2),
    DOCUMENT_FAILED(3),
    INTERVIEW_SCHEDULED(4),
    INTERVIEW_DONE(5),
    FINAL_PASSED(6),
    FINAL_FAILED(7),
    CANCELLED(8);

    private final int code;

    public static StageType of(int code) {
        return Arrays.stream(values())
                .filter(e -> e.code == code)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown StageType code: " + code));
    }

    public boolean isTerminal() {
        return this == DOCUMENT_FAILED || this == FINAL_PASSED
                || this == FINAL_FAILED || this == CANCELLED;
    }

    @Converter(autoApply = true)
    public static class EnumConverter implements AttributeConverter<StageType, Integer> {
        @Override public Integer convertToDatabaseColumn(StageType e) { return e == null ? null : e.code; }
        @Override public StageType convertToEntityAttribute(Integer c) { return c == null ? null : StageType.of(c); }
    }
}
