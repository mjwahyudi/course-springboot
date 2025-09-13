package com.module5.order.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
  name = "notification-service",                 // must match notification's spring.application.name
  path = "/api/notifications",
  fallback = NotificationFeignFallback.class)   // simple degradation
public interface NotificationFeign {
  @PostMapping("/send")
  String send(@RequestParam String to, @RequestParam String message);
}
