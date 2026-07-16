package com.icbc.reminder.model;

import lombok.Data;

@Data
public class EmailRetryLog {
    private Long id;
    private String remindId;
    private String email;
    private String title;
    private int retryCount;
    private String lastRetryTime;
    private String status;
    private String errorMsg;
    private String createTime;
    private String idempotencyKey;
}
