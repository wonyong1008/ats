package com.ainjob.ats.domain.ats;

import com.ainjob.ats.common.enums.StageType;
import com.ainjob.ats.domain.company.Company;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "Stage",
    uniqueConstraints = @UniqueConstraint(name = "uq_stage_order", columnNames = {"company_id", "step_order"})
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Stage {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(name = "stage_type", nullable = false, columnDefinition = "TINYINT")
    private StageType stageType;

    @Column(name = "step_order", nullable = false, columnDefinition = "TINYINT")
    private int stepOrder;

    @Column(name = "is_terminal", nullable = false)
    private boolean isTerminal;
}
