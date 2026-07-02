package com.ainjob.ats.domain.applicant;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Major")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Major {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "is_cs_related", nullable = false)
    private boolean isCsRelated;
}
