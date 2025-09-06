# Practice 501: Spring Cloud Microservices

Latihan ini ditujukan agar peserta dapat membuat **Spring microservices** dengan Spring Boot dan Spring Cloud: **Discovery Server (Eureka)**, **Config Server**, **API Gateway**, serta dua service aplikasi (**Order** dan **Notification**) di mana **Order memanggil Notification via OpenFeign**.  

## Learning Goals
1. Peserta dapat membangun microservice dengan Spring Cloud framework.  
2. Peserta dapat membuat Discovery Service (Eureka).  
3. Peserta dapat membuat Config Service (Native backend / folder `/shared`).  
4. Peserta dapat membuat API Gateway.  
5. Peserta dapat membangun 2 service: **Order** dan **Notification**.  
6. Peserta dapat melakukan interkoneksi Order → Notification melalui **Feign**.

---

## Struktur Proyek
Setiap service adalah **independen** dengan `pom.xml` masing‑masing.

```
spring-microservices/
└── spring-microservices/
    ├── api-gateway/
    │   ├── src/
    │   │   └── main/
    │   │       ├── java/
    │   │       └── resources/
    │   └── pom.xml
    ├── config-server/
    │   ├── shared/
    │   ├── src/
    │   │   └── main/
    │   │       ├── java/
    │   │       └── resources/
    │   └── pom.xml
    ├── discovery-server/
    │   ├── src/
    │   │   └── main/
    │   │       ├── java/
    │   │       └── resources/
    │   └── pom.xml
    ├── notification-service/
    │   ├── src/
    │   │   └── main/
    │   │       ├── java/
    │   │       └── resources/
    │   └── pom.xml
    └── order-service/
        ├── src/
        │   └── main/
        │       ├── java/
        │       └── resources/
        └── pom.xml
```

> Catatan: Folder `/shared` berada di **dalam** `config-server/` dan akan dipakai oleh Config Server (native file system) sebagai sumber configuration.

---

## Versi & Prasyarat
- **Java 17+** (disarankan).  
- **Spring Boot 3.x** dan **Spring Cloud 2023.0.x (Leyton)**.  
- **Maven** terpasang.  
- Port default yang akan digunakan:
  - Discovery Server: **8761**
  - Config Server: **8888**
  - Order Service: **8081**
  - Notification Service: **8082**
  - API Gateway: **8080**

> Jika perlu, sesuaikan versi Spring Boot & Spring Cloud sesuai lingkungan Anda. Contoh properti versi ada di setiap `pom.xml` template di bawah.

---

## Discovery Server

### Konfigurasi Aplikasi
Buat `src/main/resources/application.yml`:

```yaml
server:
  port: 8761

spring:
  application:
    name: discovery-server

eureka:
  client:
    register-with-eureka: false
    fetch-registry: false
  server:
    waitTimeInMsWhenSyncEmpty: 0

management:
  endpoints:
    web:
      exposure:
        include: health,info
```

### Entry Point
Buat class `DiscoveryServerApplication.java`. Tambahkan anotasi `@EnableEurekaServer`

```java
@SpringBootApplication
@EnableEurekaServer
public class DiscoveryServerApplication { 
    public static void main(String[] args) { 
        SpringApplication.run(DiscoveryServerApplication.class, args); 
    } 
}
```

---

## 3) Config Server

### Konfigurasi Aplikasi
Buat `src/main/resources/application.yml`:

```yaml
server:
  port: 8888

spring:
  application:
    name: config-server
  profiles:
    active: native
  cloud:
    config:
      server:
        native:
          search-locations: "file:./shared"

management:
  endpoints:
    web:
      exposure:
        include: health,info
```

> **Catatan:** `search-locations: "file:./shared"` menunjuk ke folder **`config-server/shared`** di dalam project ini.

### Mengisi Konfigurasi di `/shared`
Di folder `config-server/shared/`, buat file‑file berikut untuk setiap aplikasi client:

**`order-service.yml`**
```yaml
server:
  port: 8081

spring:
  application:
    name: order-service
  config:
    import: "optional:configserver:http://localhost:8888"

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka

management:
  endpoints:
    web:
      exposure:
        include: health,info
```

**`notification-service.yml`**
```yaml
server:
  port: 8082

spring:
  application:
    name: notification-service
  config:
    import: "optional:configserver:http://localhost:8888"

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka

management:
  endpoints:
    web:
      exposure:
        include: health,info
```

**`api-gateway.yml`**
```yaml
server:
  port: 8080

spring:
  application:
    name: api-gateway
  config:
    import: "optional:configserver:http://localhost:8888"
  cloud:
    gateway:
      discovery:
        locator:
          enabled: true
      routes:
        - id: order-service
          uri: lb://order-service
          predicates:
            - Path=/orders/**
        - id: notification-service
          uri: lb://notification-service
          predicates:
            - Path=/notifications/**
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka
```

### 3.4 Entry Point
Buat class `ConfigServerApplication.java`

```java
@SpringBootApplication
@EnableConfigServer
public class ConfigServerApplication { 
    public static void main(String[] args) { 
        SpringApplication.run(ConfigServerApplication.class, args); 
    } 
}
```

---

## API Gateway

### Konfigurasi Aplikasi
**Tidak perlu** `application.yml` lokal jika sudah memakai Config Server (file `api-gateway.yml` pada `/shared`). Pastikan setiap client memiliki:

```yaml
spring:
  config:
    import: "optional:configserver:http://localhost:8888"
```

### Entry Point
Buat `ApiGatewayApplication.java`.

Tambahkan `@EnableDiscoveryClient` (opsional di Boot 3.x, namun eksplisit lebih jelas).

```java
@SpringBootApplication
@EnableDiscoveryClient
public class ApiGatewayApplication { 
    public static void main(String[] args) { 
        SpringApplication.run(ApiGatewayApplication.class, args); 
    } 
}
```

---

## Notification Service

### Konfigurasi
Tidak butuh `application.yml` lokal; config akan datang dari Config Server lewat `notification-service.yml` (lihat Bagian 3.3). Pastikan ada:

```yaml
spring:
  config:
    import: "optional:configserver:http://localhost:8888"
```

### Controller & Service (Mock/Skeleton)
Contoh kontrak endpoint (cukup skeleton, tidak perlu implementasi penuh):

```java
// @RestController
// @RequestMapping("/notifications")
// public class NotificationController {
//    @PostMapping("/send")
//    public ResponseEntity<String> send(@RequestBody Map<String, Object> payload) {
//       // mock: return sukses
//       return ResponseEntity.ok("Notification sent");
//    }
// }
```

---

## Order Service + Feign ke Notification

### Konfigurasi
Tidak butuh `application.yml` lokal; config akan datang dari Config Server lewat `order-service.yml` (lihat Bagian 3.3). Pastikan ada:

```yaml
spring:
  config:
    import: "optional:configserver:http://localhost:8888"
```

### Enable Feign & Discovery
Buat class `OrderServiceApplication.java`
```java
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class OrderServiceApplication { 
    public static void main(String[] args) { 
        SpringApplication.run(OrderServiceApplication.class, args); 
    } 
}
```

### 6.4 Feign Client ke `notification-service`
Buat package `order-service/src/main/java/.../client/`
Buat class `NotificationClient.java`
```java
@FeignClient(name = "notification-service", path = "/notifications")
public interface NotificationClient {
     @PostMapping("/send")
     String send(@RequestBody Map<String, Object> payload);
}
```

### Controller & Service (Mock/Skeleton)
```java
@Service
public class OrderAppService {
    private final NotificationClient notification;
    
    public OrderAppService(NotificationClient notification) { 
        this.notification = notification; 
    }
    
    public String createOrder(Map<String,Object> order) {
        // mock proses order
        var result = notification.send(Map.of("type","EMAIL","to","user@example.com","message","Order created"));
        return "Order created; notification="+result;
    }
}

@RestController
@RequestMapping("/orders")
public class OrderController {
   private final OrderAppService svc;

   public OrderController(OrderAppService svc) { this.svc = svc; }

   @PostMapping
   public ResponseEntity<String> create(@RequestBody Map<String,Object> order){
      return ResponseEntity.ok(svc.createOrder(order));
   }
}
```

---

## Menjalankan Seluruh Microservice (urutan & verifikasi)
### Urutan Startup
1. **Discovery Server**  
   ```bash
   cd discovery-server
   mvn spring-boot:run
   ```
   Buka **http://localhost:8761** → pastikan dashboard Eureka tampil.

2. **Config Server**  
   ```bash
   cd ../config-server
   mvn spring-boot:run
   ```
   Cek endpoint config (opsional):  
   - `http://localhost:8888/order-service/default`  
   - `http://localhost:8888/notification-service/default`  
   - `http://localhost:8888/api-gateway/default`

3. **Notification Service**, **Order Service**, **API Gateway** (boleh paralel setelah 1 & 2 up)  
   ```bash
   cd ../notification-service && mvn spring-boot:run
   cd ../order-service && mvn spring-boot:run
   cd ../api-gateway && mvn spring-boot:run
   ```
   Masing‑masing service akan **register ke Eureka** dan mengambil konfigurasi dari **Config Server**.

### Verifikasi Registrasi
- Buka **http://localhost:8761** → pastikan terlihat `order-service`, `notification-service`, `api-gateway` sebagai **UP**.

### Uji Endpoint via API Gateway
- **Ping Notification** (jika controller dibuat):  
  ```bash
  curl -X POST http://localhost:8080/notifications/send -H "Content-Type: application/json" -d '{"to":"you@example.com","message":"hello"}'
  ```
- **Create Order** (akan memanggil Notification melalui Feign):  
  ```bash
  curl -X POST http://localhost:8080/orders -H "Content-Type: application/json" -d '{"orderId":"O-1","total":12345}'
  ```

> Respons mock akan menunjukkan bahwa Order berhasil dibuat dan Notification dipanggil.

---

## Troubleshooting Cepat
- **Client tidak dapat mengambil config** → pastikan `spring.config.import: "optional:configserver:http://localhost:8888"` ada di **config client** (file pada `/shared`) dan **Config Server** sudah jalan.  
- **Service tidak muncul di Eureka** → cek dependency **eureka-client** dan properti `eureka.client.service-url.defaultZone`.  
- **Gateway tidak merutekan** → cek `routes` di `api-gateway.yml` dan gunakan prefix `lb://` untuk service ID.  
- **Port bentrok** → ubah `server.port` di file YAML di `/shared`.

---

## Ringkasan
Anda sekarang memiliki:
- **Eureka Discovery Server** (8761)  
- **Config Server (native)** membaca dari **`config-server/shared`** (8888)  
- **API Gateway** (8080) dengan routing ke `order-service` & `notification-service`  
- **Order Service** (Feign client) ↔ **Notification Service** (endpoint `/notifications/send`)  

Semua service **independen** (bukan multi-module) sesuai struktur zip.
