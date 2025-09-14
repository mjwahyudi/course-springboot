package com.module6.notification.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    private static final Logger log = LoggerFactory.getLogger(NotificationController.class);
    
    @PostMapping("/send")
    public String send(@RequestParam String to, @RequestParam String message) {
        log.info("Sending notification to {}: {}", to, message);
        return "ok:" + to;
    }
}
