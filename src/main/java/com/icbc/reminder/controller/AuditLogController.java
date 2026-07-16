package com.icbc.reminder.controller;

import com.icbc.reminder.model.AuditLog;
import com.icbc.reminder.service.AuditLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Tag(name = "AuditLog", description = "审计日志 API")
@RequestMapping("/audit")
public class AuditLogController {
    @Autowired
    AuditLogService auditLogService;

    @GetMapping("/logs")
    @Operation(summary = "获取最近的审计日志", description = "返回最近的100条审计日志记录, 按时间倒序排列")
    public List<AuditLog> getRecentLogs() {
        return auditLogService.getRecentLogs();
    }
}
