package com.icbc.reminder.service;

import com.icbc.reminder.model.DashboardStats;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class DashboardCacheService {

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @Autowired
    DashboardService dashboardService;

    private static final String CACHE_KEY = "dashboard:stats";
    private static final long CACHE_TTL = 60; // Cache time-to-live in seconds (1 minute)

    public DashboardStats getStats(){
        DashboardStats cached = (DashboardStats) redisTemplate.opsForValue().get(CACHE_KEY);
        if (cached != null) {
            System.out.println("从Redis缓存读取数据");
            return cached;
        }
        System.out.println("从数据库读取数据,并写入redis缓存");
        DashboardStats stats = dashboardService.getStats();
        redisTemplate.opsForValue().set(CACHE_KEY, stats, CACHE_TTL, TimeUnit.SECONDS);
        return stats;
    }

    public void clearCache() {
        redisTemplate.delete(CACHE_KEY);
        System.out.println("已清除Redis缓存");
    }
}
