package com.module2.demo2.dto;

import lombok.Data;

@Data
public class OrderItemDto {
  private String code;   // different name on purpose (maps from sku)
  private String label;  // maps from name
  private int qty;
  private long price;
}
