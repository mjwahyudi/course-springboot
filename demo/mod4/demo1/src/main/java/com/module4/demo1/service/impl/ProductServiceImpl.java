package com.module4.demo1.service.impl;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.module4.demo1.entity.Product;
import com.module4.demo1.repository.ProductRepository;
import com.module4.demo1.service.ProductService;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository repo;

    public ProductServiceImpl(ProductRepository repo) {
        this.repo = repo;
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public List<Product> findAll() {
        return repo.findAll();
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public Product create(Product p) {
        return repo.save(p);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteById(Long id) {
        repo.deleteById(id);
    }
}
