package com.icbc.reminder;

import com.icbc.reminder.mapper.DashboardMapper;
import com.icbc.reminder.service.DashboardService;
import com.icbc.reminder.model.DashboardStats;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    DashboardMapper dashboardMapper;

    @InjectMocks
    DashboardService dashboardService;

    @Test
    void testGetStats(){
        when(dashboardMapper.getTotalReminders()).thenReturn(10);
        when(dashboardMapper.getPendingReminders()).thenReturn(3);
        when(dashboardMapper.getCompletedReminders()).thenReturn(7);
        when(dashboardMapper.getTotalUsers()).thenReturn(5);
        when(dashboardMapper.getTodayEmailsSent()).thenReturn(2);
        when(dashboardMapper.getMonthlyNewReminders()).thenReturn(10);
        when(dashboardMapper.getSendSuccessRate()).thenReturn(95.5);
        when(dashboardMapper.getFailedEmails()).thenReturn(1);

        DashboardStats stats = dashboardService.getStats();

        assertEquals(10, stats.getTotalReminders());
        assertEquals(3, stats.getPendingReminders());
        assertEquals(7, stats.getCompletedReminders());
        assertEquals(5, stats.getTotalUsers());
        assertEquals(95.5, stats.getSendSuccessRate());
        assertEquals(1, stats.getFailedEmails());
        assertEquals(2, stats.getTodayEmailsSent());
        assertEquals(10, stats.getMonthlyNewReminders());
    }
    
}
