package com.icbc.reminder;

import com.icbc.reminder.mapper.EmailRetryMapper;
import com.icbc.reminder.model.EmailRetryLog;
import com.icbc.reminder.service.AuditLogService;
import com.icbc.reminder.service.EmailRetryService;
import com.icbc.reminder.service.EmailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailRetryServiceTest {

    @Mock
    EmailRetryMapper emailRetryMapper;

    @Mock
    AuditLogService auditLogService;

    @Mock
    EmailService emailService;

    @InjectMocks
    EmailRetryService emailRetryService;

    private EmailRetryLog pendingLog(int retryCount) {
        EmailRetryLog log = new EmailRetryLog();
        log.setId(1L);
        log.setEmail("test@example.com");
        log.setTitle("Test Email");
        log.setRetryCount(retryCount);
        log.setStatus("PENDING");
        return log;
    }

    private EmailRetryLog runRetryAndCaptureUpdate(EmailRetryLog log) {
        when(emailRetryMapper.findPendingRetries()).thenReturn(List.of(log));
        emailRetryService.retryFailedEmails();
        ArgumentCaptor<EmailRetryLog> captor = ArgumentCaptor.forClass(EmailRetryLog.class);
        verify(emailRetryMapper).updateRetryLog(captor.capture());
        return captor.getValue();
    }

    @Test
    void successfulSend_marksSuccess_withoutIncrementingRetryCount() {
        EmailRetryLog updated = runRetryAndCaptureUpdate(pendingLog(0));

        verify(emailService).sendEmail(eq("test@example.com"), eq("Test Email"), anyString());
        assertEquals("SUCCESS", updated.getStatus());
        assertEquals(0, updated.getRetryCount());
        verify(auditLogService).log(eq("SYSTEM"), eq("EMAIL_RETRY_SUCCESS"), any(), any(), any(), eq("SUCCESS"));
    }

    @Test
    void failureBelowLimit_staysPending_andIncrementsRetryCount() {
        doThrow(new RuntimeException("SMTP timeout"))
                .when(emailService).sendEmail(anyString(), anyString(), anyString());

        EmailRetryLog updated = runRetryAndCaptureUpdate(pendingLog(0));

        assertEquals("PENDING", updated.getStatus());
        assertEquals(1, updated.getRetryCount());
        assertEquals("SMTP timeout", updated.getErrorMsg());
        verify(auditLogService).log(eq("SYSTEM"), eq("EMAIL_RETRY_FAILED"), any(), any(), any(), eq("FAILED"));
    }

    @Test
    void thirdFailure_marksFailed_soItIsNeverRetriedAgain() {
        doThrow(new RuntimeException("SMTP timeout"))
                .when(emailService).sendEmail(anyString(), anyString(), anyString());

        EmailRetryLog updated = runRetryAndCaptureUpdate(pendingLog(2));

        assertEquals(3, updated.getRetryCount());
        assertEquals("FAILED", updated.getStatus());
    }

    @Test
    void recordFailure_insertsPendingRow_withPerDayIdempotencyKey() {
        emailRetryService.recordFailure("42", "test@example.com", "Title", "connection timeout");

        ArgumentCaptor<EmailRetryLog> captor = ArgumentCaptor.forClass(EmailRetryLog.class);
        verify(emailRetryMapper).insertRetryLog(captor.capture());
        // Same reminder + recipient on the same day maps to the same key, so the
        // unique constraint + INSERT IGNORE turns a duplicate failure report into a no-op.
        assertEquals("42_test@example.com_" + LocalDate.now(), captor.getValue().getIdempotencyKey());
        verifyNoInteractions(emailService);
    }
}
