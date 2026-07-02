package com.ainjob.ats.common.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum GraduationStatus {
    ENROLLED(1), GRADUATED(2), DROPPED(3), ON_LEAVE(4);

    private final int code;

    public static GraduationStatus of(int code) {
        return Arrays.stream(values())
                .filter(e -> e.code == code)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown GraduationStatus code: " + code));
    }

    @Converter(autoApply = true)
    public static class EnumConverter implements AttributeConverter<GraduationStatus, Integer> {
        @Override public Integer convertToDatabaseColumn(GraduationStatus e) { return e == null ? null : e.code; }
        @Override public GraduationStatus convertToEntityAttribute(Integer c) { return c == null ? null : GraduationStatus.of(c); }
    }
}
