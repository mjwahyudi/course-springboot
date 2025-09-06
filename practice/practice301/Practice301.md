# 🚀 Practice 301: Interkoneksi Database dengan Spring JPA

Latihan ini ditujukan agar peserta dapat membangun **interkoneksi ke database PostgreSQL**, membuat **Entity**, serta mengoperasikan **database CRUD** menggunakan **Spring Data JPA**.

---

## 🎯 Learning Goals
Setelah menyelesaikan latihan ini, peserta akan mampu:
1. Melakukan konfigurasi Database ke **PostgreSQL**.  
2. Melakukan mapping **Entity**.  
3. Melakukan operasi database menggunakan **JPA Repository**: Query, Aggregation, Insert, Update, Delete.  

---

## 📋 Prasyarat
1. Database koneksi informasi tersedia.  
2. Terminal bisa mengakses database.  
3. DDL untuk praktek sudah ter-deploy:  

**Table Order**
- OrderID: UUID  
- OrderDate: Timestamp  
- OrderEmail: Varchar2(200)  
- OrderTotalAmount: Number  
- OrderTotalQty: Number  

**Table OrderItem**
- DetailID: UUID  
- ItemCode: Varchar2(10)  
- ItemQty: Number  
- ItemPrice: Number  

---

## 📝 Step by Step

### 1. Setup Project via Spring Initializr
Buka [Spring Initializr](https://start.spring.io/) dengan konfigurasi:  

- **Project**: Maven  
- **Language**: Java  
- **Spring Boot**: versi terbaru (`3.3.x`)  
- **Group**: `com.practice301`  
- **Artifact**: `springboot301`  
- **Dependencies**:  
  - Spring Web  
  - Spring Data JPA  
  - PostgreSQL Driver  

Download project, extract, lalu buka di IDE.  

Tambahkan konfigurasi database di `application.properties`:  
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/practice301
spring.datasource.username=postgres
spring.datasource.password=yourpassword
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

---

### 2. Membuat Entity

Buat package `com.practice301.entity` dengan dua class:

**Order.java**
```java
package com.practice301.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    private UUID orderId;

    private LocalDateTime orderDate;
    private String orderEmail;
    private Double orderTotalAmount;
    private Integer orderTotalQty;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> items;

    // getters & setters
}
```

**OrderItem.java**
```java
package com.practice301.entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    private UUID detailId;

    private String itemCode;
    private Integer itemQty;
    private Double itemPrice;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    // getters & setters
}
```

---

### 3. Membuat Repository

Buat package `com.practice301.repository`:

```java
package com.practice301.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.practice301.entity.Order;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
}
```

---

### 4. Membuat Service

Buat package `com.practice301.service`:

```java
package com.practice301.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.practice301.entity.Order;
import com.practice301.repository.OrderRepository;

import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Transactional
    public Order saveOrder(Order order) {
        order.setOrderId(UUID.randomUUID());
        return orderRepository.save(order);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public void deleteOrder(UUID id) {
        orderRepository.deleteById(id);
    }
}
```

---

### 5. Membuat Controller

Buat package `com.practice301.controller`:

```java
package com.practice301.controller;

import org.springframework.web.bind.annotation.*;
import com.practice301.entity.Order;
import com.practice301.service.OrderService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public Order createOrder(@RequestBody Order order) {
        return orderService.saveOrder(order);
    }

    @GetMapping
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    @DeleteMapping("/{id}")
    public void deleteOrder(@PathVariable UUID id) {
        orderService.deleteOrder(id);
    }
}
```

---

### ✅ Testing

1. Jalankan aplikasi dengan:
```bash
mvn spring-boot:run
```

2. Gunakan Postman untuk menguji:  

**POST** `http://localhost:8080/orders`  
```json
{
  "orderDate": "2025-09-07T12:00:00",
  "orderEmail": "customer@email.com",
  "orderTotalAmount": 1500.0,
  "orderTotalQty": 3,
  "items": [
    {
      "detailId": "b6d4db6a-2dcd-42b8-9a30-46e94b770aa1",
      "itemCode": "ITEM001",
      "itemQty": 2,
      "itemPrice": 500.0
    }
  ]
}
```

**GET** `http://localhost:8080/orders`  
**DELETE** `http://localhost:8080/orders/{uuid}`  

---

📌 Catatan  
- Gunakan **UUID** sebagai primary key.  
- Gunakan **PostgreSQL** sebagai database utama.  
- Gunakan **Spring Data JPA** untuk semua operasi CRUD.  