package com.ainjob.ats.domain.job;

import com.ainjob.ats.common.enums.JobCategory;
import com.ainjob.ats.common.enums.JobPostingStatus;
import com.ainjob.ats.domain.company.Company;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "JobPosting")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class JobPosting {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(name = "job_category", nullable = false, columnDefinition = "TINYINT")
    private JobCategory jobCategory;

    @Column(name = "min_exp_years", nullable = false, columnDefinition = "TINYINT")
    private int minExpYears;

    @Column(nullable = false, columnDefinition = "TINYINT")
    private JobPostingStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @PrePersist
    protected void onCreate() { this.createdAt = LocalDateTime.now(); }
}
