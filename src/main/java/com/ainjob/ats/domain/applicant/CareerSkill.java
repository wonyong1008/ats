package com.ainjob.ats.domain.applicant;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "CareerSkill",
    uniqueConstraints = @UniqueConstraint(name = "uq_careerskill", columnNames = {"career_id", "skill_id"})
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CareerSkill {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "career_id", nullable = false)
    private Career career;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;
}
