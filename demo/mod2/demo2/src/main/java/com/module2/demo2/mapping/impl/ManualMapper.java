package com.module2.demo2.mapping.impl;

import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.module2.demo2.domain.Order;
import com.module2.demo2.domain.OrderItem;
import com.module2.demo2.dto.OrderDto;
import com.module2.demo2.dto.OrderItemDto;
import com.module2.demo2.mapping.OrderMapper;

@Service
@Profile("manual")
public class ManualMapper implements OrderMapper {
  private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_DATE_TIME;

  @Override
  public OrderDto convertToDTO(Order s) {
    if (s == null) return null;
    OrderDto d = new OrderDto();
    d.setOrderId(s.getOrderId());
    d.setEmail(s.getCustomerEmail());
    d.setOrderTime(s.getOrderTime() != null ? s.getOrderTime().format(ISO) : null);
    if (s.getItems() != null) {
      d.setItems(s.getItems().stream().map(this::itemToDTO).collect(Collectors.toList()));
    }
    return d;
  }

  private OrderItemDto itemToDTO(OrderItem i) {
    OrderItemDto d = new OrderItemDto();
    d.setCode(i.getSku());
    d.setLabel(i.getName());
    d.setQty(i.getQty());
    d.setPrice(i.getPrice());
    return d;
  }
}
