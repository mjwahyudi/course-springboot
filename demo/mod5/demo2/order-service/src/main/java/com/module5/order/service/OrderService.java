package com.module5.order.service;

import org.springframework.stereotype.Service;

import com.module5.order.feign.NotificationFeign;

@Service
public class OrderService {
  private final NotificationFeign client;

  public OrderService(NotificationFeign client) { this.client = client; }

  public String placeOrder(String to) {
    return client.send(to, "Your order was received");
  }
}
