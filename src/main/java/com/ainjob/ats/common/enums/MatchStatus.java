package com.ainjob.ats.common.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum MatchStatus {
    RECOMMENDED(1), REVIEWING(2), ADOPTED(3), REJECTED(4);

    private final int code;

    public static MatchStatus of(int code) {
        return Arrays.stream(values())
                .filter(e -> e.code == code)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown MatchStatus code: " + code));
    }

    @Converter(autoApply = true)
    public static class EnumConverter implements AttributeConverter<MatchStatus, Integer> {
        @Override public Integer convertToDatabaseColumn(MatchStatus e) { return e == null ? null : e.code; }
        @Override public MatchStatus convertToEntityAttribute(Integer c) { return c == null ? null : MatchStatus.of(c); }
    }
}
