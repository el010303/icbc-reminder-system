package com.icbc.reminder.service;

import com.icbc.reminder.mapper.EmailRetryMapper;
import com.icbc.reminder.model.EmailRetryLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class EmailRetryService {
    @Autowired
    EmailRetryMapper emailRetryMapper;

    @Autowired
    AuditLogService auditLogService;

    @Autowired
    EmailService emailService;

    private static final int MAX_RETRY_COUNT = 3;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public void recordFailure(String remindId, String email, String title, String errorMsg) {
        String idempotencyKey = remindId + "_" + email + "_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        EmailRetryLog log = new EmailRetryLog();
        log.setRemindId(remindId);
        log.setEmail(email);
        log.setTitle(title);
        log.setErrorMsg(errorMsg);
        log.setIdempotencyKey(idempotencyKey);
        log.setCreateTime(LocalDateTime.now().format(formatter));
        emailRetryMapper.insertRetryLog(log);
    }

    @Scheduled(fixedDelay = 30000) // Retry every 5 mins
    public void retryFailedEmails() {
        List<EmailRetryLog> pendingList = emailRetryMapper.findPendingRetries();

        for (EmailRetryLog log : pendingList) {
            try{
                simulateSendEmail(log.getEmail(), log.getTitle());
                log.setStatus("SUCCESS");
                log.setLastRetryTime(LocalDateTime.now().format(formatter));
                emailRetryMapper.updateRetryLog(log);
                auditLogService.log("SYSTEM", "EMAIL_RETRY_SUCCESS", log.getId().toString(), "EMAIL_RETRY", "重新发送成功：" + log.getEmail(), "SUCCESS");
            } catch (Exception e) {
                log.setRetryCount(log.getRetryCount() + 1);
                log.setLastRetryTime(LocalDateTime.now().format(formatter));
                log.setErrorMsg(e.getMessage());
                if (log.getRetryCount() >= MAX_RETRY_COUNT) {
                    log.setStatus("FAILED");
                } else {
                    log.setStatus("PENDING");
                }
                emailRetryMapper.updateRetryLog(log);
                auditLogService.log("SYSTEM", "EMAIL_RETRY_FAILED", log.getId().toString(), "EMAIL_RETRY", "重新发送失败，第" + log.getRetryCount() + "次尝试：" + e.getMessage(), "FAILED");
            }
        }
    }

    private void simulateSendEmail(String email, String title) {
        emailService.sendEmail(email, title, "这是来自 ICBC 重要事项提醒系统的提醒邮件");
    }

}
