package com.module6.notification.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    
    @PostMapping("/send")
    public String send(@RequestParam String to, @RequestParam String message) {
        return "ok:" + to;
    }
}
