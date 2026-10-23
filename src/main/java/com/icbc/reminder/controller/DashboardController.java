package com.icbc.reminder.controller;

import com.icbc.reminder.model.DashboardStats;
import com.icbc.reminder.service.DashboardCacheService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
@Tag(name = "Dashboard", description = "系统监控 Dashboard API")
public class DashboardController {

    @Autowired
    private DashboardCacheService dashboardCacheService;

    @GetMapping("/stats")
    @Operation(summary = "获取系统统计数据", description = "返回时实时系统指标, 包括Redis缓存, 60秒过期")
    public DashboardStats getStats() {
        return dashboardCacheService.getStats();
    }

    // POST, not GET: invalidating the cache is a state change, and GET requests
    // can be repeated by browsers, prefetchers and crawlers.
    @PostMapping("/refresh")
    @Operation(summary = "刷新缓存", description = "清除缓存, 下次请求将从数据库重新加载")
    public String refreshCache() {
        dashboardCacheService.clearCache();
        return "Dashboard cache cleared and will be refreshed on next request.";
    }
} 
