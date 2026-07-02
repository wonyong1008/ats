package com.ainjob.ats.domain.ats;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StageRepository extends JpaRepository<Stage, Long> {
    Optional<Stage> findByIdAndCompanyId(Long id, Long companyId);
}
