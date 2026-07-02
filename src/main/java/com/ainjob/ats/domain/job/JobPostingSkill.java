package com.ainjob.ats.domain.job;

import com.ainjob.ats.domain.applicant.Skill;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "JobPostingSkill",
    uniqueConstraints = @UniqueConstraint(name = "uq_jpskill", columnNames = {"job_posting_id", "skill_id"})
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class JobPostingSkill {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_posting_id", nullable = false)
    private JobPosting jobPosting;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    @Column(name = "is_required", nullable = false)
    private boolean isRequired;
}
