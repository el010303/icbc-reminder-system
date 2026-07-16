package com.icbc.reminder.model;
import lombok.Data;

@Data
public class AuditLog {
    private Long id;
    private String operator;
    private String action;
    private String targetId;
    private String targetType;
    private String detail;
    private String result;
    private String createTime;
}
