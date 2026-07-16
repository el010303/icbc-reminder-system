package com.icbc.reminder.model;

import lombok.Data;

@Data
public class DashboardStats {
    private int totalReminders;
    private int pendingReminders;
    private int completedReminders;
    private int totalUsers;
    private int todayEmailsSent;
    private int monthlyNewReminders;
    private double sendSuccessRate;
    private int failedEmails;
}
