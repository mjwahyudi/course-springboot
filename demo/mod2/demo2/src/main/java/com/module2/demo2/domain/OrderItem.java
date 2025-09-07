package com.module2.demo2.domain;

import lombok.Data;

@Data
public class OrderItem {
  private String sku;
  private String name;
  private int qty;
  private long price; // in cents
}
