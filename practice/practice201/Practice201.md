# 🚀 Practice 201: Controller Service Repository Pattern

Latihan ini ditujukan agar peserta dapat menerapkan Controller Service Repository Pattern dalam project **Spring Boot** sederhana dari nol.

---

## **Learning Goals**

 1. Peserta dapat membuat **REST API** dengan menerapkan anotasi:
 * `@RestController`, 
 * `@RequestMapping`, 
 * `@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping`
 *  `@RequestParam`, `@RequestBody`, `@ResponseBody`.
 2. Peserta menerapkan **pola Controller–Service–Repository** dengan **database H2**.

---

## 📝 Step by Step

## 1. Setup Project via Spring Initializr

1. Buka **[https://start.spring.io](https://start.spring.io)**
2. Pilih opsi:

   * **Project**: Maven
   * **Language**: Java
   * **Spring Boot**: 3.3.x (atau terbaru)
   * **Group**: `com.practice201`
   * **Artifact**: `practice201`
   * **Name**: `springboot201`
3. **Dependencies** (minimum):
   * **Spring Web**
   * **Spring Data JPA**
   * **H2 Database**
   * (Opsional tapi dianjurkan) **Lombok**
4. Klik **Generate** → ekstrak zip → buka di IDE.

---

## 2) Struktur Paket yang Disarankan

```
com.practice201
├─ springboot201.java
├─ config/                 (opsional)
├─ controller/
├─ dto/                    (opsional)
├─ entity/
├─ repository/
└─ service/
   ├─ impl/
```

---

## 3) Konfigurasi `application.yml`

Buat/ubah **`src/main/resources/application.yml`**:

```yaml
server:
  port: 8080
  servlet:
    context-path: /

spring:
  datasource:
    url: jdbc:h2:mem:practice201;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
    driverClassName: org.h2.Driver
    username: sa
    password: ""
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate.format_sql: true
  h2:
    console:
      enabled: true
      path: /h2-console
```

> **Catatan:** H2 berjalan **in-memory** (hilang saat aplikasi berhenti). Untuk persistens yang tidak hilang, gunakan file: `jdbc:h2:file:./data/practice201`.

---

## 4) Domain & Entity

Buat **`src/main/java/com/example/practice201/entity/Product.java`**:

```java
package com.practice201.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private Double price;

    public Product() {}

    public Product(String name, Double price) {
        this.name = name;
        this.price = price;
    }

    // getters & setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
}
```

*(Jika menggunakan Lombok, Anda bisa mengganti dengan `@Data`, `@NoArgsConstructor`, `@AllArgsConstructor`.)*

---

## 5) Repository

Buat **`src/main/java/com/example/practice201/repository/ProductRepository.java`**:

```java
package com.practice201.repository;

import com.practice201.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByNameContainingIgnoreCase(String q);
}
```

---

## 6) Service (Interface & Implementasi)

**Interface** – `src/main/java/com/example/practice201/service/ProductService.java`:

```java
package com.practice201.service;

import com.practice201.entity.Product;
import java.util.List;
import java.util.Optional;

public interface ProductService {
    List<Product> findAll(String q);
    Optional<Product> findById(Long id);
    Product create(Product p);
    Product update(Long id, Product p);
    void delete(Long id);
}
```

**Implementasi** – `src/main/java/com/example/practice201/service/impl/ProductServiceImpl.java`:

```java
package com.practice201.service.impl;

import com.practice201.entity.Product;
import com.practice201.repository.ProductRepository;
import com.practice201.service.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {
    private final ProductRepository repo;

    public ProductServiceImpl(ProductRepository repo) {
        this.repo = repo;
    }

    @Override
    public List<Product> findAll(String q) {
        if (q == null || q.isBlank()) return repo.findAll();
        return repo.findByNameContainingIgnoreCase(q);
    }

    @Override
    public Optional<Product> findById(Long id) { return repo.findById(id); }

    @Override
    public Product create(Product p) { return repo.save(p); }

    @Override
    public Product update(Long id, Product p) {
        Product existing = repo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Product not found: " + id));
        existing.setName(p.getName());
        existing.setPrice(p.getPrice());
        return repo.save(existing);
    }

    @Override
    public void delete(Long id) { repo.deleteById(id); }
}
```

---

## 7) Controller (REST API)

Buat **`src/main/java/com/example/practice201/controller/ProductController.java`**:

```java
package com.example.practice201.controller;

import com.example.practice201.entity.Product;
import com.example.practice201.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    // Contoh eksplisit @ResponseBody (sebenarnya @RestController sudah mengimplikasikannya)
    @GetMapping("/ping")
    @ResponseBody
    public String ping() { return "pong"; }

    // GET /api/products?q=phone
    @GetMapping
    public List<Product> list(@RequestParam(value = "q", required = false) String q) {
        return service.findAll(q);
    }

    // GET /api/products/item?id=1  (menggunakan @RequestParam sesuai materi)
    @GetMapping("/item")
    public ResponseEntity<Product> getById(@RequestParam("id") Long id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /api/products  (body JSON)
    @PostMapping
    public ResponseEntity<Product> create(@RequestBody Product p) {
        Product created = service.create(p);
        return ResponseEntity
                .created(URI.create("/api/products/item?id=" + created.getId()))
                .body(created);
    }

    // PUT /api/products?id=1
    @PutMapping
    public ResponseEntity<Product> update(@RequestParam("id") Long id,
                                          @RequestBody Product p) {
        return ResponseEntity.ok(service.update(id, p));
    }

    // DELETE /api/products?id=1
    @DeleteMapping
    public ResponseEntity<Void> delete(@RequestParam("id") Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
```

> **Kenapa bukan `@PathVariable`?**
> Untuk memastikan seluruh anotasi yang diminta **(@RequestParam, @RequestBody, @ResponseBody)** terpakai eksplisit dalam satu contoh. Pada praktik lanjutan, Anda sangat dianjurkan memakai pola REST standar (`/api/products/{id}` dengan `@PathVariable`).

---

## 8) (Opsional) Seed Data Awal

Buat **`src/main/resources/data.sql`** agar H2 terisi saat startup:

```sql
INSERT INTO products(name, price) VALUES ('Keyboard', 320000);
INSERT INTO products(name, price) VALUES ('Mouse', 150000);
INSERT INTO products(name, price) VALUES ('Monitor', 2100000);
```

---

## 9) Menjalankan Aplikasi

Pilih salah satu:

* **Via Maven**: `./mvnw spring-boot:run`
* **Via IDE**: jalankan kelas `Practice201Application`.

Jika sukses, aplikasi berjalan di: `http://localhost:8080`

**H2 Console**: `http://localhost:8080/h2-console`

* JDBC URL: `jdbc:h2:mem:practice201`
* User: `sa`
* Password: *(kosong)*

---

## 10) Uji Coba API (cURL)

> Gunakan **Terminal** atau **REST Client** (Postman/VS Code REST).

**Ping**

```bash
curl -X GET "http://localhost:8080/api/products/ping"
```

**List (dengan/atau tanpa query)**

```bash
curl -X GET "http://localhost:8080/api/products"
curl -X GET "http://localhost:8080/api/products?q=key"
```

**Get by id (pakai `@RequestParam`)**

```bash
curl -G "http://localhost:8080/api/products/item" --data-urlencode "id=1"
```

**Create**

```bash
curl -X POST "http://localhost:8080/api/products" \
  -H "Content-Type: application/json" \
  -d '{"name":"Tablet","price":3500000}'
```

**Update**

```bash
curl -X PUT "http://localhost:8080/api/products?id=1" \
  -H "Content-Type: application/json" \
  -d '{"name":"Keyboard Pro","price":450000}'
```

**Delete**

```bash
curl -X DELETE "http://localhost:8080/api/products?id=1"
```

---

## 11) Ringkasan Penerapan Anotasi

* `@RestController` → menandai controller REST (implisit `@ResponseBody` untuk semua handler).
* `@RequestMapping("/api/products")` → base path seluruh endpoint controller.
* `@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping` → HTTP verbs.
* `@RequestParam` → baca parameter query (?q=..., ?id=...).
* `@RequestBody` → baca JSON body dan bind ke objek.
* `@ResponseBody` → ditunjukkan eksplisit pada `/ping` untuk demonstrasi (opsional pada `@RestController`).

---

## 12) Latihan Lanjutan (Opsional)

1. Ubah endpoint menjadi bergaya **RESTful**: `/api/products/{id}` dengan `@PathVariable`.
2. Tambahkan validasi field (`spring-boot-starter-validation`) dan gunakan `@Valid` pada request body.
3. Buat DTO request/response terpisah dari entity.
4. Tambah pagination (`Pageable`) di endpoint GET.
5. Tambahkan layer **exception handler** global (`@ControllerAdvice`).

Selamat berlatih! 🎯
