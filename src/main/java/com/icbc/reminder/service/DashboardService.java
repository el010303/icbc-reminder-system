package com.icbc.reminder.service;

import com.icbc.reminder.mapper.DashboardMapper;
import com.icbc.reminder.model.DashboardStats;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    @Autowired
    DashboardMapper dashboardMapper;

    public DashboardStats getStats() {
        DashboardStats stats = new DashboardStats();
        stats.setTotalReminders(dashboardMapper.getTotalReminders());
        stats.setPendingReminders(dashboardMapper.getPendingReminders());
        stats.setCompletedReminders(dashboardMapper.getCompletedReminders());
        stats.setTotalUsers(dashboardMapper.getTotalUsers());
        stats.setTodayEmailsSent(dashboardMapper.getTodayEmailsSent());
        stats.setMonthlyNewReminders(dashboardMapper.getMonthlyNewReminders());
        stats.setSendSuccessRate(dashboardMapper.getSendSuccessRate());
        stats.setFailedEmails(dashboardMapper.getFailedEmails());
        return stats;
    }
}