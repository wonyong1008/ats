package com.ainjob.ats.api.service;

import com.ainjob.ats.common.enums.StageType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailNotificationService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.enabled:false}")
    private boolean mailEnabled;

    @Value("${app.mail.from:noreply@ainjob.com}")
    private String from;

    @Async
    public void sendStageChangeNotification(
            String toEmail, String applicantName,
            String jobTitle, StageType toStageType, String toStageName) {

        if (!mailEnabled) {
            log.info("[MAIL-SKIP] to={} subject={} stage={}", toEmail, "[AINJOB] 채용 단계 변경 안내", toStageName);
            return;
        }

        try {
            var message = mailSender.createMimeMessage();
            var helper = new MimeMessageHelper(message, false, "UTF-8");
            helper.setFrom(from);
            helper.setTo("lwy1008@naver.com"); // 테스트용 고정 수신자
            helper.setSubject("[AINJOB] " + stageSubject(toStageType));
            helper.setText(buildHtml(applicantName, jobTitle, toStageName, toStageType), true);
            mailSender.send(message);
            log.info("[MAIL-SENT] to=lwy1008@naver.com (applicant={}) stage={}", toEmail, toStageName);
        } catch (Exception e) {
            log.warn("[MAIL-FAIL] to={} reason={}", toEmail, e.getMessage());
        }
    }

    private String stageSubject(StageType type) {
        return switch (type) {
            case DOCUMENT_PASSED      -> "서류 전형 합격을 축하드립니다";
            case DOCUMENT_FAILED      -> "서류 전형 결과 안내";
            case INTERVIEW_SCHEDULED  -> "면접 일정이 확정되었습니다";
            case INTERVIEW_DONE       -> "면접 완료 안내";
            case FINAL_PASSED         -> "최종 합격을 축하드립니다";
            case FINAL_FAILED         -> "최종 전형 결과 안내";
            case CANCELLED            -> "지원 취소 안내";
            default                   -> "채용 단계 변경 안내";
        };
    }

    private String buildHtml(String name, String jobTitle, String stageName, StageType type) {
        String color = switch (type) {
            case DOCUMENT_PASSED, FINAL_PASSED, INTERVIEW_SCHEDULED -> "#3b6ef8";
            case DOCUMENT_FAILED, FINAL_FAILED, CANCELLED           -> "#ef4444";
            default                                                  -> "#7b8ab8";
        };
        String message = switch (type) {
            case DOCUMENT_PASSED     -> "서류 전형을 통과하셨습니다. 다음 단계인 면접 일정을 기다려 주세요.";
            case DOCUMENT_FAILED     -> "아쉽게도 서류 전형에서 탈락하셨습니다. 다음 기회에 다시 도전해 주시기 바랍니다.";
            case INTERVIEW_SCHEDULED -> "면접 일정이 확정되었습니다. 담당자가 별도로 연락드릴 예정입니다.";
            case INTERVIEW_DONE      -> "면접이 완료되었습니다. 결과는 빠른 시일 내에 안내드리겠습니다.";
            case FINAL_PASSED        -> "최종 합격을 진심으로 축하드립니다! 입사 관련 안내는 별도로 연락드리겠습니다.";
            case FINAL_FAILED        -> "아쉽게도 이번 채용에서는 함께하지 못하게 되었습니다. 귀하의 앞날을 응원합니다.";
            case CANCELLED           -> "지원이 취소 처리되었습니다. 문의사항이 있으시면 담당자에게 연락해 주세요.";
            default                  -> "채용 단계가 변경되었습니다.";
        };

        return """
                <div style="font-family:'Noto Sans KR',Arial,sans-serif;max-width:560px;margin:0 auto;padding:32px 24px;background:#f8f9ff;">
                  <div style="background:#fff;border-radius:12px;border:1px solid #dde3f0;overflow:hidden;">
                    <div style="background:%s;padding:20px 28px;">
                      <span style="color:#fff;font-size:18px;font-weight:800;letter-spacing:-0.5px;">AINJOB</span>
                      <span style="color:rgba(255,255,255,.7);font-size:13px;margin-left:8px;">채용 알림</span>
                    </div>
                    <div style="padding:28px;">
                      <p style="font-size:15px;font-weight:700;color:#1a1a2e;margin-bottom:8px;">안녕하세요, %s 님</p>
                      <p style="font-size:13px;color:#555;margin-bottom:20px;">
                        <strong>%s</strong> 포지션의 채용 단계가 변경되었습니다.
                      </p>
                      <div style="background:#f0f4ff;border-left:4px solid %s;border-radius:6px;padding:14px 16px;margin-bottom:20px;">
                        <span style="font-size:12px;color:#7b8ab8;display:block;margin-bottom:4px;">현재 단계</span>
                        <span style="font-size:16px;font-weight:800;color:%s;">%s</span>
                      </div>
                      <p style="font-size:13px;color:#555;line-height:1.7;">%s</p>
                    </div>
                    <div style="padding:16px 28px;border-top:1px solid #dde3f0;background:#f8f9ff;">
                      <p style="font-size:11px;color:#aaa;margin:0;">본 메일은 발신 전용입니다. 문의: recruit@ainjob.com</p>
                    </div>
                  </div>
                </div>
                """.formatted(color, name, jobTitle, color, color, stageName, message);
    }
}
