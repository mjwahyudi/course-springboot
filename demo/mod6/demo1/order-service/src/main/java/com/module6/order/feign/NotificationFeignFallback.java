package com.module6.order.feign;

import org.springframework.stereotype.Component;

@Component
public class NotificationFeignFallback implements NotificationFeign {
  @Override public String send(String to, String message) {
    return "queued:" + to; // degrade: pretend queued, or return cached data
  }
}