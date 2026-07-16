package com.icbc.reminder;

import com.icbc.reminder.mapper.EmailRetryMapper;
import com.icbc.reminder.model.EmailRetryLog;
import com.icbc.reminder.service.EmailRetryService;
import com.icbc.reminder.service.AuditLogService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailRetryServiceTest {
    
    @Mock
    EmailRetryMapper emailRetryMapper;

    @Mock
    AuditLogService auditLogService;

    @InjectMocks
    EmailRetryService emailRetryService;

    @Test
    void testRetryEmails() {
        EmailRetryLog log = new EmailRetryLog();
        log.setId(1L);
        log.setEmail("test@icbc.com");
        log.setTitle("Test Email");
        log.setRetryCount(0);
        log.setStatus("Pending");

        when(emailRetryMapper.findPendingRetries()).thenReturn(Arrays.asList(log));

        emailRetryService.retryFailedEmails();

        verify(emailRetryMapper, times(1)).updateRetryLog(any());
    }

    @Test
    void testRecordFailure() {
        emailRetryService.recordFailure("1", "test@icbc.com", "测试", "链接超时");
        verify (emailRetryMapper, times(1)).insertRetryLog(any());
    }
}
