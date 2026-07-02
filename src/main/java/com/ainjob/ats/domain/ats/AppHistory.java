package com.ainjob.ats.domain.ats;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "AppHistory")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AppHistory {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    private Application application;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_stage_id")
    private Stage fromStage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_stage_id", nullable = false)
    private Stage toStage;

    @Column(name = "changed_by", length = 100)
    private String changedBy;

    @Column(length = 500)
    private String note;

    @Column(name = "changed_at", nullable = false, updatable = false)
    private LocalDateTime changedAt;

    @PrePersist
    protected void onCreate() { this.changedAt = LocalDateTime.now(); }

    public static AppHistory of(Application application, Stage fromStage, Stage toStage, String changedBy, String note) {
        AppHistory h = new AppHistory();
        h.application = application;
        h.fromStage = fromStage;
        h.toStage = toStage;
        h.changedBy = changedBy;
        h.note = note;
        return h;
    }
}
