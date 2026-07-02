package com.ainjob.ats.domain.ats;

import com.ainjob.ats.api.dto.ApplicantSearchDto;
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

    // 목적: 기업 내 지원자 통합 검색 — 진행상태/학력/전공/경력연차/기술스택 복합 필터
    // 전제: skillIds 비어있으면 List.of(-1L) + skillCount=0 전달, IS NULL OR 패턴으로 옵셔널 필터 처리
    // 인덱스 전제: idx_app_company_stage(company_id, current_stage_id), idx_cs_skill(skill_id)
    @Query(nativeQuery = true, value = """
            SELECT DISTINCT
                   a.id          AS applicantId,
                   a.name        AS applicantName,
                   a.email       AS email,
                   st.name       AS stageName,
                   st.stage_type AS stageType
            FROM Application app
            JOIN Applicant a  ON a.id  = app.applicant_id
            JOIN Stage     st ON st.id = app.current_stage_id
            WHERE app.company_id = :companyId
              AND (:stageType   IS NULL OR st.stage_type = :stageType)
              AND (:degreeType  IS NULL OR EXISTS (
                      SELECT 1 FROM Education e
                      WHERE e.applicant_id = a.id AND e.degree_type >= :degreeType))
              AND (:majorId     IS NULL OR EXISTS (
                      SELECT 1 FROM Education e
                      WHERE e.applicant_id = a.id AND e.major_id = :majorId))
              AND (:minExpMonths = 0 OR (
                      SELECT COALESCE(SUM(TIMESTAMPDIFF(MONTH, c.start_date, IFNULL(c.end_date, CURDATE()))), 0)
                      FROM Career c WHERE c.applicant_id = a.id
                  ) >= :minExpMonths)
              AND (:skillCount = 0 OR (
                      SELECT COUNT(DISTINCT cs.skill_id)
                      FROM Career c2 JOIN CareerSkill cs ON cs.career_id = c2.id
                      WHERE c2.applicant_id = a.id AND cs.skill_id IN (:skillIds)
                  ) >= :skillCount)
            ORDER BY a.name
            """)
    List<ApplicantSearchDto> searchApplicants(
            @Param("companyId") Long companyId,
            @Param("stageType") Integer stageType,
            @Param("degreeType") Integer degreeType,
            @Param("majorId") Long majorId,
            @Param("minExpMonths") int minExpMonths,
            @Param("skillIds") List<Long> skillIds,
            @Param("skillCount") int skillCount
    );
}
