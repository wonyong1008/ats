package com.ainjob.ats.common.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum DegreeType {
    HIGH_SCHOOL(1), ASSOCIATE(2), BACHELOR(3), MASTER(4), DOCTOR(5);

    private final int code;

    public static DegreeType of(int code) {
        return Arrays.stream(values())
                .filter(e -> e.code == code)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown DegreeType code: " + code));
    }

    @Converter(autoApply = true)
    public static class EnumConverter implements AttributeConverter<DegreeType, Integer> {
        @Override public Integer convertToDatabaseColumn(DegreeType e) { return e == null ? null : e.code; }
        @Override public DegreeType convertToEntityAttribute(Integer c) { return c == null ? null : DegreeType.of(c); }
    }
}
