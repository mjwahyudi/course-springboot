package com.module2.demo2.mapping;

import com.module2.demo2.domain.Order;
import com.module2.demo2.dto.OrderDto;

public interface OrderMapper {
  OrderDto convertToDTO(Order source);
}
