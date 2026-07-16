package com.icbc.reminder.service;

import com.icbc.reminder.model.AuditLog;
import com.icbc.reminder.mapper.AuditLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class AuditLogService {

    @Autowired
    AuditLogMapper auditLogMapper;

    private static final DateTimeFormatter formatter = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    public void log(String operator, String action, String targetId, String targetType, String detail, String result) {
        AuditLog log = new AuditLog();
        log.setOperator(operator);
        log.setAction(action);
        log.setTargetId(targetId);
        log.setTargetType(targetType);
        log.setDetail(detail);
        log.setResult(result);
        log.setCreateTime(LocalDateTime.now().format(formatter));
        auditLogMapper.insertAuditLog(log);
    }

    public List<AuditLog> getRecentLogs() {
        return auditLogMapper.getRecentLogs();
    }
}
