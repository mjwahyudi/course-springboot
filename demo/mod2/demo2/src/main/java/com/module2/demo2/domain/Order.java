package com.module2.demo2.domain;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class Order {
  private String orderId;
  private String customerEmail;
  private LocalDateTime orderTime;
  private List<OrderItem> items;
}