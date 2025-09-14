package com.module6.order.feign;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.module6.order.service.OrderService;

@Component
public class NotificationFeignFallback implements NotificationFeign {
  private static final Logger log = LoggerFactory.getLogger(OrderService.class);

  @Override public String send(String to, String message) {
    log.warn("Fallback feign call: {}, message: {}", to, message);
    
    return "queued:" + to; // degrade: pretend queued, or return cached data
  }
}