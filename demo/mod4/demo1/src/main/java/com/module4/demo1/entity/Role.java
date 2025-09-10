package com.module4.demo1.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Role {
  @Id 
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // store as "ROLE_USER", "ROLE_ADMIN"
  @Column(nullable=false, unique=true)
  private String name;

  // getters/setters
}
