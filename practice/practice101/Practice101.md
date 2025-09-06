# 🚀 Practice 101: Inisiasi Spring Boot Project

Latihan ini ditujukan agar peserta dapat menginisiasi project **Spring Boot** sederhana dari nol, mulai dari setup project hingga membuat REST API yang membaca konfigurasi dari `application.yaml`.

---

## 🎯 Learning Goals
Setelah menyelesaikan latihan ini, peserta akan mampu:
1. Membuat project Spring Boot dari [Spring Initializr](https://start.spring.io/).
2. Mengubah konfigurasi default menjadi `application.yaml`.
3. Melakukan override standar **server port** dan **application name**.
4. Menambahkan properti custom untuk fitur **Notification** dan **Order**.
5. Membuat **Configuration Properties**, **Service**, dan **Controller** yang terhubung.

---

## 📝 Step by Step

### 1. Setup Project via Spring Initializr
Buka [Spring Initializr](https://start.spring.io/) dan gunakan konfigurasi berikut:

- **Project**: Maven
- **Language**: Java
- **Spring Boot**: versi terbaru (misal `3.3.0`)
- **Group**: `com.practice101`
- **Artifact**: `springboot101`
- **Dependencies**: `Spring Web`

Download project, extract, lalu buka di IDE (IntelliJ / VS Code / Eclipse).

---

### 2. Konfigurasi Aplikasi
Ubah `application.properties` menjadi `application.yaml` di folder `src/main/resources`.

```yaml
server:
  port: 9090

spring:
  application:
    name: SpringBoot101App

features:
  notification:
    enabled: true
    sms: true
    email: false
  order:
    enabled: true
    customer: true
    message: false
```

---

### 3. Configuration Properties

Buat package `com.practice101.config`, lalu tambahkan dua class:

**NotificationProperties.java**
```java
package com.practice101.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "features.notification")
public class NotificationProperties {
    private boolean enabled;
    private boolean sms;
    private boolean email;

    // getters & setters
}
```
dan
**OrderProperties.java**
```java
package com.practice101.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "features.order")
public class OrderProperties {
    private boolean enabled;
    private boolean customer;
    private boolean message;

    // getters & setters
}
```

---

### 4. Configuration Bean

Untuk menghubungkan **Properties** dengan **Service**, buat konfigurasi menggunakan `@Configuration`, `@EnableConfigurationProperties`, dan `@ConditionalOnProperty`. Dengan ini, Bean hanya akan dibuat jika fitur **enabled**.

**NotificationConfig.java**
```java
package com.practice101.config;

import com.practice101.service.NotificationService;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

@Configuration
@EnableConfigurationProperties(NotificationProperties.class)
@ConditionalOnProperty(prefix = "features.notification", name = "enabled", havingValue = "true", matchIfMissing = false)
public class NotificationConfig {

    @Bean
    public NotificationService notificationService(NotificationProperties notificationProperties) {
        return new NotificationService(notificationProperties);
    }
}
```

**OrderConfig.java**
```java
package com.practice101.config;

import com.practice101.service.OrderService;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

@Configuration
@EnableConfigurationProperties(OrderProperties.class)
@ConditionalOnProperty(prefix = "features.order", name = "enabled", havingValue = "true", matchIfMissing = false)
public class OrderConfig {

    @Bean
    public OrderService orderService(OrderProperties orderProperties) {
        return new OrderService(orderProperties);
    }
}
```

---

### 5. Services

Buat package `com.practice101.service`, lalu tambahkan:

**NotificationService.java**
```java
package com.practice101.service;

import com.practice101.config.NotificationProperties;

public class NotificationService {
    private final NotificationProperties notificationProperties;

    public NotificationService(NotificationProperties notificationProperties) {
        this.notificationProperties = notificationProperties;
    }

    public NotificationProperties getNotificationFeatures() {
        return notificationProperties;
    }
}
```

**OrderService.java**
```java
package com.practice101.service;

import com.practice101.config.OrderProperties;

public class OrderService {
    private final OrderProperties orderProperties;

    public OrderService(OrderProperties orderProperties) {
        this.orderProperties = orderProperties;
    }

    public OrderProperties getOrderFeatures() {
        return orderProperties;
    }
}
```

---

### 6. Controllers

Buat package `com.practice101.controller`, lalu tambahkan:

**NotificationController.java**
```java
package com.practice101.controller;

import com.practice101.config.NotificationProperties;
import com.practice101.service.NotificationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NotificationController {
    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/notification")
    public NotificationProperties getNotification() {
        return notificationService.getNotificationFeatures();
    }
}
```

**OrderController.java**
```java
package com.practice101.controller;

import com.practice101.config.OrderProperties;
import com.practice101.service.OrderService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/order")
    public OrderProperties getOrder() {
        return orderService.getOrderFeatures();
    }
}
```
---
### ✅ Testings

**1.** Jalankan aplikasi dengan:
```bash
mvn spring-boot:run
``` 
**2.** Akses endpoint di browser atau Postman:
* http://localhost:9090/notification
* http://localhost:9090/order

Contoh hasil JSON (jika sesuai dengan `application.yaml`):
```json
{
  "enabled": true,
  "sms": true,
  "email": false
}
```
```json
{
  "enabled": true,
  "customer": true,
  "message": false
}
```
📌 Catatan

* Gunakan struktur folder modular: config, service, controller.
* `@ConfigurationProperties` + `@EnableConfigurationProperties` memastikan properti YAML bisa dibaca dengan aman.
* `@ConditionalOnProperty` membuat konfigurasi lebih fleksibel (aktif/nonaktif berdasarkan flag enabled).