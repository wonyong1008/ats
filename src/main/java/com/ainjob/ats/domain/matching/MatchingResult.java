package com.ainjob.ats.domain.matching;

import com.ainjob.ats.common.enums.MatchStatus;
import com.ainjob.ats.domain.applicant.Applicant;
import com.ainjob.ats.domain.company.Company;
import com.ainjob.ats.domain.job.JobPosting;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "MatchingResult",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_matching",
        columnNames = {"company_id", "applicant_id", "job_posting_id"}
    )
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MatchingResult {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applicant_id", nullable = false)
    private Applicant applicant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_posting_id", nullable = false)
    private JobPosting jobPosting;

    @Column(name = "match_score", nullable = false, precision = 5, scale = 2)
    private BigDecimal matchScore;

    @Column(name = "match_status", nullable = false, columnDefinition = "TINYINT")
    private MatchStatus matchStatus;

    @Column(columnDefinition = "TEXT")
    private String reason;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() { this.createdAt = LocalDateTime.now(); }
}
