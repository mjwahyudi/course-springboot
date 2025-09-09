package com.module3.demo1.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.module3.demo1.entity.Customer;
import com.module3.demo1.repository.CustomerRepository;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public List<Customer> list() {
        return customerRepository.findAll();
    }

}
