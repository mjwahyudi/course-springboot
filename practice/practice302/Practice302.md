# 🚀 Practice 302: JPQL & Native Query dengan Spring Data JPA

Latihan ini ditujukan agar peserta dapat membangun query **advance** menggunakan **JPQL** dan **NativeQuery** dalam Spring Data JPA.

---

## 🎯 Learning Goals
Setelah menyelesaikan latihan ini, peserta akan mampu:
1. Membuat query dengan **JPQL**.  
2. Membuat query dengan **Native Query**.  

---

## 📋 Prasyarat
1. Database koneksi informasi tersedia.  
2. Terminal bisa mengakses database.  
3. DDL untuk praktek sudah ter-deploy.  
4. **Practice301.md** sudah selesai dilakukan.  

---

## 📝 Step by Step

### 1. Membuat JPQL

Tambahkan method JPQL pada `OrderRepository`:

```java
package com.practice302.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.practice301.entity.Order;
import java.util.List;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {

    // JPQL Query: Cari order berdasarkan email
    @Query("SELECT o FROM Order o WHERE o.orderEmail = :email")
    List<Order> findByEmail(String email);

    // JPQL Query: Cari order dengan totalAmount lebih besar dari nilai tertentu
    @Query("SELECT o FROM Order o WHERE o.orderTotalAmount > :amount")
    List<Order> findOrdersGreaterThanAmount(Double amount);
}
```

---

### 2. Membuat Native Query

Tambahkan method dengan Native Query pada `OrderRepository`:

```java
    // Native Query: Cari order_items berdasarkan kode barang
    @Query(value = "SELECT * FROM order_items oi WHERE oi.item_code = :code", nativeQuery = true)
    List<Object[]> findItemsByCode(String code);

    // Native Query: Hitung total order per email
    @Query(value = "SELECT order_email, COUNT(*) AS total_order FROM orders GROUP BY order_email", nativeQuery = true)
    List<Object[]> countOrdersByEmail();
```

---

### 3. Menghubungkan Query dengan Service

Buat package `com.practice302.service`:

```java
package com.practice302.service;

import org.springframework.stereotype.Service;
import com.practice301.entity.Order;
import com.practice302.repository.OrderRepository;

import java.util.List;

@Service
public class OrderQueryService {

    private final OrderRepository orderRepository;

    public OrderQueryService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public List<Order> getOrdersByEmail(String email) {
        return orderRepository.findByEmail(email);
    }

    public List<Order> getOrdersGreaterThanAmount(Double amount) {
        return orderRepository.findOrdersGreaterThanAmount(amount);
    }

    public List<Object[]> getItemsByCode(String code) {
        return orderRepository.findItemsByCode(code);
    }

    public List<Object[]> getOrderCountByEmail() {
        return orderRepository.countOrdersByEmail();
    }
}
```

---

### 4. Menghubungkan Service dengan Controller

Buat package `com.practice302.controller`:

```java
package com.practice302.controller;

import org.springframework.web.bind.annotation.*;
import com.practice301.entity.Order;
import com.practice302.service.OrderQueryService;

import java.util.List;

@RestController
@RequestMapping("/order-query")
public class OrderQueryController {

    private final OrderQueryService orderQueryService;

    public OrderQueryController(OrderQueryService orderQueryService) {
        this.orderQueryService = orderQueryService;
    }

    @GetMapping("/by-email")
    public List<Order> getOrdersByEmail(@RequestParam String email) {
        return orderQueryService.getOrdersByEmail(email);
    }

    @GetMapping("/greater-than")
    public List<Order> getOrdersGreaterThanAmount(@RequestParam Double amount) {
        return orderQueryService.getOrdersGreaterThanAmount(amount);
    }

    @GetMapping("/items-by-code")
    public List<Object[]> getItemsByCode(@RequestParam String code) {
        return orderQueryService.getItemsByCode(code);
    }

    @GetMapping("/count-by-email")
    public List<Object[]> getOrderCountByEmail() {
        return orderQueryService.getOrderCountByEmail();
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

- **GET** `http://localhost:8080/order-query/by-email?email=alice@example.com`  
- **GET** `http://localhost:8080/order-query/greater-than?amount=1000`  
- **GET** `http://localhost:8080/order-query/items-by-code?code=ITEM001`  
- **GET** `http://localhost:8080/order-query/count-by-email`  

---

📌 Catatan  
- JPQL bekerja dengan **Entity dan field class**.  
- Native Query langsung mengeksekusi **SQL di database**.  
- Gunakan JPQL untuk portability, Native Query untuk kebutuhan khusus (misalnya aggregation kompleks).