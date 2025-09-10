package com.module4.demo1.service;

import java.util.List;

import com.module4.demo1.entity.Product;

public interface ProductService {
  List<Product> findAll();
  Product create(Product p);
  void deleteById(Long id);
}
