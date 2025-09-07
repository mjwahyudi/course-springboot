package com.module2.demo2.controller;

import org.springframework.web.bind.annotation.RestController;

import com.module2.demo2.domain.Order;
import com.module2.demo2.dto.OrderDto;
import com.module2.demo2.mapping.OrderMapper;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
public class GreetingController {
  private final OrderMapper mapper;

  public GreetingController(OrderMapper mapper) {
    this.mapper = mapper;
  }

  @PostMapping("/map")
  public OrderDto map(@RequestBody Order order) {
    return mapper.convertToDTO(order);
  }
}
