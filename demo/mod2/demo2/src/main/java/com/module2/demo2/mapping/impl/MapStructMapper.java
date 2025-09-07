
package com.module2.demo2.mapping.impl;

import java.time.format.DateTimeFormatter;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.context.annotation.Profile;

import com.module2.demo2.domain.Order;
import com.module2.demo2.domain.OrderItem;
import com.module2.demo2.dto.OrderDto;
import com.module2.demo2.dto.OrderItemDto;
import com.module2.demo2.mapping.OrderMapper;

@Mapper(componentModel = "spring")
@Profile("mapstruct")
public interface MapStructMapper extends OrderMapper {

  @Mapping(target = "email", source = "customerEmail")
  @Mapping(target = "orderTime", ignore = true) // we'll format manually
  OrderDto convertToDTO(Order source);

  @Mapping(target = "code", source = "sku")
  @Mapping(target = "label", source = "name")
  OrderItemDto convertToDTO(OrderItem source);

  @AfterMapping
  default void formatTime(Order source, @MappingTarget OrderDto target) {
    if (source != null && source.getOrderTime() != null) {
      target.setOrderTime(source.getOrderTime().format(DateTimeFormatter.ISO_DATE_TIME));
    }
  }
}