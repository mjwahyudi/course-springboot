package com.module2.demo2.mapping.impl;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.module2.demo2.domain.Order;
import com.module2.demo2.dto.OrderDto;
import com.module2.demo2.mapping.OrderMapper;

@Service
@Profile("modelmapper")
public class ModelMapperMap implements OrderMapper {
  private final ModelMapper mm;
  
  public ModelMapperMap(ModelMapper mm) { this.mm = mm; }

  @Override
  public OrderDto convertToDTO(Order s) {
    return s == null ? null : mm.map(s, OrderDto.class);
  }
}
