package com.icbc.reminder.controller;

import com.icbc.reminder.service.EmailRetryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/email")
@Tag(name = "Email", description = "邮件管理 API")
public class EmailController {

    @Autowired
    EmailRetryService emailRetryService;

    @PostMapping("/test")
    @Operation(summary = "触发测试邮件", description = "加入重试队列，30秒内自动发送")
    public ResponseEntity<String> testEmail(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String title = body.get("title");
        emailRetryService.recordFailure("frontend_test", email, title, "前端测试触发");
        return ResponseEntity.ok("ok");
    }
}
