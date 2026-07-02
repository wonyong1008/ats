package com.ainjob.ats.domain.applicant;

import com.ainjob.ats.common.enums.DegreeType;
import com.ainjob.ats.common.enums.GraduationStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "Education")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Education {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applicant_id", nullable = false)
    private Applicant applicant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "major_id")
    private Major major;

    @Column(name = "school_name", nullable = false, length = 100)
    private String schoolName;

    @Column(name = "degree_type", nullable = false, columnDefinition = "TINYINT")
    private DegreeType degreeType;

    @Column(name = "graduation_status", nullable = false, columnDefinition = "TINYINT")
    private GraduationStatus graduationStatus;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;
}
