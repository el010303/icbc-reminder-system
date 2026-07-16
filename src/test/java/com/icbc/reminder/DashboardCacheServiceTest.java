package com.icbc.reminder;

import com.icbc.reminder.model.DashboardStats;
import com.icbc.reminder.service.DashboardCacheService;
import com.icbc.reminder.service.DashboardService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DashboardCacheServiceTest {

    @Mock
    RedisTemplate<String, Object> redisTemplate;

    @Mock
    ValueOperations<String, Object> valueOperations;

    @Mock
    DashboardService dashboardService;

    @InjectMocks
    DashboardCacheService dashboardCacheService;

    @Test
    void testCacheHit() {
        DashboardStats cached = new DashboardStats();
        cached.setTotalReminders(5);

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("dashboard:stats")).thenReturn(cached);

        DashboardStats result = dashboardCacheService.getStats();
        assertEquals(5, result.getTotalReminders());
        verify(dashboardService, never()).getStats();
    }

    @Test
    void testCacheMiss() {
        DashboardStats fromDb = new DashboardStats();
        fromDb.setTotalReminders(10);

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("dashboard:stats")).thenReturn(null);
        when(dashboardService.getStats()).thenReturn(fromDb);

        DashboardStats result = dashboardCacheService.getStats();
        assertEquals(10, result.getTotalReminders());
        verify(dashboardService, times(1)).getStats();
    }
}
