-- =====================================================
-- V1: 전체 스키마 생성
-- 목적: AINJOB ATS 멀티테넌트 HR SaaS DDL
-- 전제: MySQL 8.0, 상태값은 TINYINT 코드만 사용
-- =====================================================

CREATE TABLE Major (
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    name          VARCHAR(100) NOT NULL,
    is_cs_related TINYINT(1)   NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE Skill (
    id       BIGINT      NOT NULL AUTO_INCREMENT,
    name     VARCHAR(50) NOT NULL,
    category TINYINT     NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_skill_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE Company (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    name        VARCHAR(100) NOT NULL,
    business_no VARCHAR(20)  NOT NULL,
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at  DATETIME     NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_company_business_no (business_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE Applicant (
    id         BIGINT       NOT NULL AUTO_INCREMENT,
    name       VARCHAR(50)  NOT NULL,
    email      VARCHAR(200) NOT NULL,
    phone      VARCHAR(20)  NULL,
    created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at DATETIME     NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_applicant_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE JobPosting (
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    company_id    BIGINT       NOT NULL,
    title         VARCHAR(200) NOT NULL,
    job_category  TINYINT      NOT NULL,
    min_exp_years TINYINT      NOT NULL DEFAULT 0,
    status        TINYINT      NOT NULL DEFAULT 1,
    created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    closed_at     DATETIME     NULL,
    deleted_at    DATETIME     NULL,
    PRIMARY KEY (id),
    INDEX idx_jp_company (company_id),
    CONSTRAINT fk_jp_company FOREIGN KEY (company_id) REFERENCES Company (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE JobPostingSkill (
    id             BIGINT     NOT NULL AUTO_INCREMENT,
    job_posting_id BIGINT     NOT NULL,
    skill_id       BIGINT     NOT NULL,
    is_required    TINYINT(1) NOT NULL DEFAULT 1,
    PRIMARY KEY (id),
    UNIQUE KEY uq_jpskill (job_posting_id, skill_id),
    CONSTRAINT fk_jps_posting FOREIGN KEY (job_posting_id) REFERENCES JobPosting (id),
    CONSTRAINT fk_jps_skill   FOREIGN KEY (skill_id)       REFERENCES Skill (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE Education (
    id                BIGINT       NOT NULL AUTO_INCREMENT,
    applicant_id      BIGINT       NOT NULL,
    major_id          BIGINT       NULL,
    school_name       VARCHAR(100) NOT NULL,
    degree_type       TINYINT      NOT NULL,
    graduation_status TINYINT      NOT NULL,
    start_date        DATE         NOT NULL,
    end_date          DATE         NULL,
    PRIMARY KEY (id),
    INDEX idx_edu_applicant (applicant_id),
    CONSTRAINT fk_edu_applicant FOREIGN KEY (applicant_id) REFERENCES Applicant (id),
    CONSTRAINT fk_edu_major     FOREIGN KEY (major_id)     REFERENCES Major (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE Career (
    id           BIGINT       NOT NULL AUTO_INCREMENT,
    applicant_id BIGINT       NOT NULL,
    company_name VARCHAR(100) NOT NULL,
    job_category TINYINT      NOT NULL,
    start_date   DATE         NOT NULL,
    end_date     DATE         NULL,
    is_current   TINYINT(1)   NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    INDEX idx_career_applicant (applicant_id),
    CONSTRAINT fk_career_applicant FOREIGN KEY (applicant_id) REFERENCES Applicant (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 목적: 경력별 보유 스킬 매핑 (합격자 필터의 AND 스킬 조건 구현 핵심)
-- 전제: 동일 경력 내 동일 스킬 중복 등록 방지는 DB UNIQUE 제약으로 보장 (앱 로직 의존 금지)
-- 인덱스 전제: idx_cs_skill(skill_id) — JobPostingSkill JOIN 시 스킬 역방향 탐색 지원
CREATE TABLE CareerSkill (
    id        BIGINT NOT NULL AUTO_INCREMENT,
    career_id BIGINT NOT NULL,
    skill_id  BIGINT NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_careerskill (career_id, skill_id),
    INDEX idx_cs_skill (skill_id),
    CONSTRAINT fk_cs_career FOREIGN KEY (career_id) REFERENCES Career (id),
    CONSTRAINT fk_cs_skill  FOREIGN KEY (skill_id)  REFERENCES Skill (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 목적: 기업별 ATS 채용 단계 정의 (상태전이 규칙의 물리적 기반)
-- 전제: stage_type은 TINYINT 코드(1~8)로 고정, company_id별로 독립 구성 가능
-- 인덱스 전제: uq_stage_order(company_id, step_order) — 동일 기업 내 단계 순서 중복 방지 및 정렬 보장
CREATE TABLE Stage (
    id         BIGINT      NOT NULL AUTO_INCREMENT,
    company_id BIGINT      NOT NULL,
    name       VARCHAR(50) NOT NULL,
    stage_type TINYINT     NOT NULL,
    step_order TINYINT     NOT NULL,
    is_terminal TINYINT(1) NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE KEY uq_stage_order (company_id, step_order),
    INDEX idx_stage_company (company_id),
    CONSTRAINT fk_stage_company FOREIGN KEY (company_id) REFERENCES Company (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 목적: 지원 핵심 테이블 (ATS 상태 추적)
-- 전제: company_id 선두 복합 인덱스로 멀티테넌트 격리 보장
-- 제약: (company_id, applicant_id, job_posting_id) UNIQUE → 동일 회사 내 중복 지원 DB 레벨 차단
CREATE TABLE Application (
    id               BIGINT   NOT NULL AUTO_INCREMENT,
    company_id       BIGINT   NOT NULL,
    applicant_id     BIGINT   NOT NULL,
    job_posting_id   BIGINT   NOT NULL,
    current_stage_id BIGINT   NOT NULL,
    applied_at       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uq_application (company_id, applicant_id, job_posting_id),
    INDEX idx_app_company_stage (company_id, current_stage_id),
    INDEX idx_app_applicant (applicant_id),
    CONSTRAINT fk_app_company   FOREIGN KEY (company_id)       REFERENCES Company (id),
    CONSTRAINT fk_app_applicant FOREIGN KEY (applicant_id)     REFERENCES Applicant (id),
    CONSTRAINT fk_app_posting   FOREIGN KEY (job_posting_id)   REFERENCES JobPosting (id),
    CONSTRAINT fk_app_stage     FOREIGN KEY (current_stage_id) REFERENCES Stage (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE AppHistory (
    id             BIGINT       NOT NULL AUTO_INCREMENT,
    application_id BIGINT       NOT NULL,
    from_stage_id  BIGINT       NULL,
    to_stage_id    BIGINT       NOT NULL,
    changed_by     VARCHAR(100) NULL,
    note           VARCHAR(500) NULL,
    changed_at     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    INDEX idx_history_application (application_id),
    CONSTRAINT fk_hist_application FOREIGN KEY (application_id) REFERENCES Application (id),
    CONSTRAINT fk_hist_from_stage  FOREIGN KEY (from_stage_id)  REFERENCES Stage (id),
    CONSTRAINT fk_hist_to_stage    FOREIGN KEY (to_stage_id)    REFERENCES Stage (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE MatchingResult (
    id             BIGINT         NOT NULL AUTO_INCREMENT,
    company_id     BIGINT         NOT NULL,
    applicant_id   BIGINT         NOT NULL,
    job_posting_id BIGINT         NOT NULL,
    match_score    DECIMAL(5,2)   NOT NULL,
    match_status   TINYINT        NOT NULL DEFAULT 1,
    reason         TEXT           NULL,
    created_at     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uq_matching (company_id, applicant_id, job_posting_id),
    INDEX idx_matching_company_job (company_id, job_posting_id),
    CONSTRAINT fk_match_company   FOREIGN KEY (company_id)     REFERENCES Company (id),
    CONSTRAINT fk_match_applicant FOREIGN KEY (applicant_id)   REFERENCES Applicant (id),
    CONSTRAINT fk_match_posting   FOREIGN KEY (job_posting_id) REFERENCES JobPosting (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
