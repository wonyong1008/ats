package com.ainjob.ats.domain.applicant;

import com.ainjob.ats.common.enums.JobCategory;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "Career")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Career {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applicant_id", nullable = false)
    private Applicant applicant;

    @Column(name = "company_name", nullable = false, length = 100)
    private String companyName;

    @Column(name = "job_category", nullable = false, columnDefinition = "TINYINT")
    private JobCategory jobCategory;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "is_current", nullable = false)
    private boolean isCurrent;
}
