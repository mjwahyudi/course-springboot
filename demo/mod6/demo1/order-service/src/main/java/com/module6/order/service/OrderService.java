package com.module6.order.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.module6.order.feign.NotificationFeign;

@Service
public class OrderService {
  private final NotificationFeign client;

  private static final Logger log = LoggerFactory.getLogger(OrderService.class);

  public OrderService(NotificationFeign client) { this.client = client; }

  public String placeOrder(String to) {
    String message = "Your order was received";
    log.info("Invoking feign call: {}, message: {}", to, message);
    return client.send(to, message);
  }
}
