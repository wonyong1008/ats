# AINJOB ATS — 백엔드 리더 기술 과제

이원용 | 제출 기한: 2026-07-06

---

## 기술 스택

| 항목 | 내용 |
|------|------|
| Language | Java 17 |
| Framework | Spring Boot 3.2.5 |
| ORM | Spring Data JPA + QueryDSL 5.0.0 (Jakarta) |
| DB | MySQL 8.0 |
| Migration | Flyway |
| Build | Gradle |

---

## 실행 방법

**1. DB 시작**
```bash
docker compose up -d
```

**2. 애플리케이션 시작**
```bash
./gradlew bootRun
```

**3. 시연 화면**
```
http://localhost:8080
```

---

## 구현 내용

### 합격자 필터 API

```
GET /api/v1/companies/{companyId}/job-postings/{jobPostingId}/applicants/passed
```

아래 조건을 모두 만족하는 최종합격자를 조회합니다.

| 조건 | 백엔드(BE) | 프런트엔드(FE) |
|------|-----------|---------------|
| 학력 | 4년제 학사 이상 | 4년제 학사 이상 |
| 전공 | CS 관련 학과 | CS 관련 학과 |
| 경력 | 백엔드 ≥ 5년 | 프런트엔드 ≥ 3년 |
| 스킬 | Java, Spring Boot, AWS | React, Next.js, TypeScript |

**응답 예시**
```json
{
  "companyId": 1,
  "jobPostingId": 1,
  "jobTitle": "백엔드 개발자 (시니어)",
  "totalCount": 3,
  "passedApplicants": [
    { "applicantId": 7, "name": "문지후", "email": "jihoo.moon@email.com" },
    { "applicantId": 2, "name": "이서윤", "email": "seoyun.lee@email.com" },
    { "applicantId": 3, "name": "한예진", "email": "yejin.han@email.com" }
  ]
}
```

### 상태변경 API (설계)

```
PATCH /api/v1/companies/{companyId}/applications/{applicationId}/stage
```

ATS 상태전이 규칙을 적용한 단계 변경 API. 잘못된 전이 시 422 반환.

```
서류접수(1) → 서류합격(2) → 면접예정(4) → 면접완료(5) → 최종합격(6)
                                                        → 최종불합격(7)
모든 비terminal 단계 → 취소(8) → 서류접수(1) [복구]
```

---

## 설계 핵심 결정

**멀티테넌트 격리**
- 모든 핵심 테이블 `company_id` 선두 인덱스
- API 레벨에서 `companyId` path variable로 테넌트 식별

**상태값 정책**
- 모든 상태값 TINYINT 코드 사용 (문자열 금지)
- Enum ↔ TINYINT 변환은 `AttributeConverter(autoApply=true)`

**DB 무결성**
- `Application` UNIQUE `(company_id, applicant_id, job_posting_id)` — 동일 공고 중복지원 방지
- `CareerSkill` UNIQUE `(career_id, skill_id)` — 동일 경력 내 스킬 중복 방지
- 비즈니스 규칙은 서비스 레이어, 데이터 정합성은 DB 제약으로 분리

**스킬 AND 조건 구현**
- `COUNT(DISTINCT skill_id) = 필수스킬수` 방식 (HAVING COUNT)
- 실행계획: `idx_cs_skill(skill_id)` 역방향 탐색으로 N+1 방지

---

## 프로젝트 구조

```
src/main/java/com/ainjob/ats/
├── api/
│   ├── controller/   # REST 컨트롤러
│   ├── service/      # 비즈니스 로직
│   └── dto/          # 요청/응답 DTO
├── domain/
│   ├── ats/          # Application, Stage, AppHistory
│   ├── applicant/    # Applicant, Career, Education, Skill 등
│   ├── company/      # Company
│   └── job/          # JobPosting, JobPostingSkill
├── common/
│   ├── enums/        # TINYINT 코드 기반 Enum
│   └── exception/    # 전역 예외 처리
└── config/           # QueryDSL 설정

src/main/resources/
├── db/migration/
│   ├── V1__create_schema.sql    # 전체 DDL
│   └── V2__insert_dummy_data.sql # 공식 더미 데이터 14명
└── static/
    └── index.html               # 시연용 UI
```

---

## DB 접속 정보 (로컬)

| 항목 | 값 |
|------|----|
| Host | localhost:3306 |
| DB | ainjob_ats |
| User | ainjob |
| Password | ainjob1234 |
