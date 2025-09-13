package com.module5.order.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.module5.order.service.OrderService;

@RestController
@RequestMapping("/api/orders")
class OrderController {
    private final OrderService svc;

    public OrderController(OrderService svc) {
        this.svc = svc;
    }

    @PostMapping("/demo")
    public String demo(@RequestParam String to) {
        return svc.placeOrder(to);
    }
}