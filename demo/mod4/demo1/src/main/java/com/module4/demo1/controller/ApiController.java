package com.module4.demo1.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.module4.demo1.entity.Product;
import com.module4.demo1.service.ProductService;

@RestController
@RequestMapping("/api/products")
public class ApiController {

  private final ProductService service;

  public ApiController(ProductService service) {
    this.service = service;
  }

  @GetMapping("/greet")
  public String greet() {
    return "hello";
  }

  @GetMapping
  public List<Product> list() {
    return service.findAll();
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Product create(@RequestBody Product p) {
    return service.create(p);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable Long id) {
    service.deleteById(id);
  }
}
