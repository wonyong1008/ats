package com.ainjob.ats.domain.ats;

import com.ainjob.ats.api.dto.PassedApplicantDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

    // 목적: company_id + job_posting_id 기준 최종합격자 필터
    // 전제: stage_type=6(최종합격), degree_type=3(학사) + is_cs_related=1, 직군 경력 합산 >= min_exp_years
    // 인덱스 전제: idx_app_company_stage (company_id, current_stage_id)
    @Query(nativeQuery = true, value = """
            SELECT a.id          AS applicantId,
                   a.name        AS applicantName,
                   a.email       AS email
            FROM Application ap
            JOIN Applicant  a  ON a.id  = ap.applicant_id
            JOIN JobPosting jp ON jp.id = ap.job_posting_id
            JOIN Stage      s  ON s.id  = ap.current_stage_id
            WHERE ap.company_id     = :companyId
              AND ap.job_posting_id = :jobPostingId
              AND s.stage_type      = 6
              AND EXISTS (
                  SELECT 1
                  FROM Education e
                  JOIN Major m ON m.id = e.major_id
                  WHERE e.applicant_id  = a.id
                    AND e.degree_type   = 3
                    AND m.is_cs_related = 1
              )
              AND (
                  SELECT COALESCE(SUM(
                      TIMESTAMPDIFF(MONTH, c.start_date, IFNULL(c.end_date, CURDATE()))
                  ), 0)
                  FROM Career c
                  WHERE c.applicant_id = a.id
                    AND c.job_category = (SELECT job_category FROM JobPosting WHERE id = :jobPostingId)
              ) >= (SELECT min_exp_years * 12 FROM JobPosting WHERE id = :jobPostingId)
              AND (
                  SELECT COUNT(DISTINCT cs.skill_id)
                  FROM Career c
                  JOIN CareerSkill cs ON cs.career_id = c.id
                  WHERE c.applicant_id = a.id
                    AND cs.skill_id IN (
                        SELECT skill_id FROM JobPostingSkill
                        WHERE job_posting_id = :jobPostingId AND is_required = 1
                    )
              ) = (
                  SELECT COUNT(*) FROM JobPostingSkill
                  WHERE job_posting_id = :jobPostingId AND is_required = 1
              )
            ORDER BY a.name
            """)
    List<PassedApplicantDto> findPassedApplicants(
            @Param("companyId") Long companyId,
            @Param("jobPostingId") Long jobPostingId
    );
}
