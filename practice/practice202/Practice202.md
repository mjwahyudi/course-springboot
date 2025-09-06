# 🚀 Practice 202: Validasi, DTO Mapping, dan Error Handling REST API

Latihan ini ditujukan agar peserta dapat menerapkan **validasi input REST API**, melakukan **mapping DTO ke Entity**, serta membangun **error handling** yang rapi menggunakan `@RestControllerAdvice`.

---

## 🎯 Learning Goals
Setelah menyelesaikan latihan ini, peserta akan mampu:
1. Melakukan validasi `@RequestBody` menggunakan **Hibernate Validator**.
2. Melakukan mapping **DTO ke Entity** menggunakan **MapStruct**.
3. Membuat **Error Handling REST API** dengan `@RestControllerAdvice`.

---

## 📝 Step by Step

### 1. Setup Project via Spring Initializr
Buka [Spring Initializr](https://start.spring.io/) dan gunakan konfigurasi berikut:

- **Project**: Maven  
- **Language**: Java  
- **Spring Boot**: versi terbaru (misal `3.3.x`)  
- **Group**: `com.practice202`  
- **Artifact**: `springboot202`  
- **Dependencies**:  
  - Spring Web  
  - Spring Data JPA  
  - H2 Database  
  - Validation  
  - MapStruct  

Download project, extract, lalu buka di IDE (IntelliJ / VS Code / Eclipse).

---

### 2. Membuat DTO

Buat package `com.practice202.dto` dengan dua class:

**OrderDto.java**
```java
package com.practice202.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.List;

public class OrderDto {

    private Long orderId;

    @NotNull(message = "Order date is required")
    private LocalDateTime orderDate;

    @Email(message = "Email must be valid")
    private String orderEmail;

    @Positive(message = "Total amount must be positive")
    private Double orderTotalAmount;

    @Positive(message = "Total item must be positive")
    private Integer orderTotalItem;

    @NotBlank(message = "Order name is required")
    private String orderName;

    @NotEmpty(message = "Items cannot be empty")
    private List<OrderItemDto> items;

    // getters & setters
}
```

**OrderItemDto.java**
```java
package com.practice202.dto;

import jakarta.validation.constraints.*;

public class OrderItemDto {

    private Long orderId;

    @NotNull(message = "Item ID is required")
    private Long itemId;

    @NotBlank(message = "Item name is required")
    private String itemName;

    @Positive(message = "Item price must be positive")
    private Double itemPrice;

    @Positive(message = "Item quantity must be positive")
    private Integer itemQty;

    // getters & setters
}
```

---

### 3. Membuat Entity

Buat package `com.practice202.entity` dengan dua class:

**Order.java**
```java
package com.practice202.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    private LocalDateTime orderDate;
    private String orderEmail;
    private Double orderTotalAmount;
    private Integer orderTotalItem;
    private String orderName;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> items;

    // getters & setters
}
```

**OrderItem.java**
```java
package com.practice202.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long itemId;

    private String itemName;
    private Double itemPrice;
    private Integer itemQty;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    // getters & setters
}
```

---

### 4. Mapping DTO ke Entity dengan MapStruct

Buat package `com.practice202.mapper`:

**OrderMapper.java**
```java
package com.practice202.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import com.practice202.dto.OrderDto;
import com.practice202.entity.Order;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

    Order toEntity(OrderDto dto);
    OrderDto toDto(Order entity);
}
```

---

### 5. Membuat Repository

Buat package `com.practice202.repository`:

```java
package com.practice202.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.practice202.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
```

---

### 6. Membuat Service

Buat package `com.practice202.service`:

```java
package com.practice202.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.practice202.dto.OrderDto;
import com.practice202.entity.Order;
import com.practice202.mapper.OrderMapper;
import com.practice202.repository.OrderRepository;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    public OrderService(OrderRepository orderRepository, OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
    }

    @Transactional
    public OrderDto saveOrder(OrderDto dto) {
        Order order = orderMapper.toEntity(dto);
        Order saved = orderRepository.save(order);
        return orderMapper.toDto(saved);
    }
}
```

---

### 7. Membuat Controller

Buat package `com.practice202.controller`:

```java
package com.practice202.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import com.practice202.dto.OrderDto;
import com.practice202.service.OrderService;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public OrderDto createOrder(@Valid @RequestBody OrderDto orderDto) {
        return orderService.saveOrder(orderDto);
    }
}
```

---

### 8. Error Handling REST API

Buat package `com.practice202.exception`:

```java
package com.practice202.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
}
```

---

### ✅ Testing

1. Jalankan aplikasi dengan:
```bash
mvn spring-boot:run
```

2. Gunakan Postman untuk mengirim request:

**POST** `http://localhost:8080/orders`
```json
{
  "orderDate": "2025-09-06T12:00:00",
  "orderEmail": "invalid-email",
  "orderTotalAmount": -500,
  "orderTotalItem": 0,
  "orderName": "",
  "items": []
}
```

3. Hasil respon error (contoh):
```json
{
  "orderEmail": "Email must be valid",
  "orderTotalAmount": "Total amount must be positive",
  "orderTotalItem": "Total item must be positive",
  "orderName": "Order name is required",
  "items": "Items cannot be empty"
}
```

---

📌 Catatan  
- Gunakan **Hibernate Validator** untuk validasi.  
- Gunakan **MapStruct** untuk mapping otomatis DTO ↔ Entity.  
- Gunakan `@RestControllerAdvice` agar error lebih rapi dan konsisten.  
