package com.icbc.reminder.service;

import com.icbc.reminder.model.DashboardStats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class DashboardCacheService {
    private static final Logger logger = LoggerFactory.getLogger(DashboardCacheService.class);

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @Autowired
    DashboardService dashboardService;

    private static final String CACHE_KEY = "dashboard:stats";
    private static final long CACHE_TTL = 60; // Cache time-to-live in seconds (1 minute)

    public DashboardStats getStats(){
        DashboardStats cached = (DashboardStats) redisTemplate.opsForValue().get(CACHE_KEY);
        if (cached != null) {
            logger.debug("Dashboard stats served from Redis cache");
            return cached;
        }
        logger.debug("Cache miss: loading dashboard stats from MySQL");
        DashboardStats stats = dashboardService.getStats();
        redisTemplate.opsForValue().set(CACHE_KEY, stats, CACHE_TTL, TimeUnit.SECONDS);
        return stats;
    }

    public void clearCache() {
        redisTemplate.delete(CACHE_KEY);
        logger.info("Dashboard stats cache cleared");
    }
}
