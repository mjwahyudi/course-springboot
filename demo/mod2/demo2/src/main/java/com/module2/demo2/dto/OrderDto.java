package com.module2.demo2.dto;

import java.util.List;
import lombok.Data;

@Data
public class OrderDto {
  private String orderId;
  private String email;      // different name on purpose
  private String orderTime;  // as ISO string
  private List<OrderItemDto> items;
}
