package com.module2.demo2.config;

import java.time.format.DateTimeFormatter;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.module2.demo2.dto.OrderDto;
import com.module2.demo2.dto.OrderItemDto;
import com.module2.demo2.domain.Order;
import com.module2.demo2.domain.OrderItem;

@Configuration
@Profile("modelmapper")
public class ModelMapperConfig {
  
  @Bean
  ModelMapper modelMapper() {
    ModelMapper mm = new ModelMapper();
    DateTimeFormatter ISO = DateTimeFormatter.ISO_DATE_TIME;

    mm.addMappings(new PropertyMap<Order, OrderDto>() {
      @Override
      protected void configure() {
        map().setEmail(source.getCustomerEmail());
        using(ctx -> ctx.getSource() == null ? null
          : ((java.time.LocalDateTime) ctx.getSource()).format(ISO))
          .map(source.getOrderTime(), destination.getOrderTime());
      }
    });

    mm.addMappings(new PropertyMap<OrderItem, OrderItemDto>() {
      @Override
      protected void configure() {
        map().setCode(source.getSku());
        map().setLabel(source.getName());
      }
    });

    return mm;
  }
}
