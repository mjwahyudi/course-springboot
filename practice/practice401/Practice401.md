# 🚀 Practice 401: Secure Endpoint with Spring Security (Basic Authentication)

Latihan ini ditujukan agar peserta dapat mengamankan endpoint menggunakan **Spring Security** dan menerapkan **Basic Authentication**.

## 🎯 Learning Goals

1. Peserta dapat mengimplementasikan **Spring Security**.  
2. Peserta dapat menerapkan **Basic Authentication** untuk mengamankan endpoint.  

## 📝 Step by Step

### 1. Inisialisasi Proyek
   - Buka [https://start.spring.io](https://start.spring.io)  
   - Gunakan konfigurasi berikut:  
     - Project: **Maven Project**  
     - Language: **Java**  
     - Spring Boot: Versi stabil 3.x  
     - Group: `com.practice401`  
     - Artifact: `springboot401`  
     - Dependencies: **Spring Web**, **Spring Security**, **H2 Database**  
   - Generate dan extract project ke workspace.

### 2. Membuat RestController
   - Buat kelas `HelloController` dengan endpoint `/hello`.  

   ```java
   @RestController
   public class HelloController {

       @GetMapping("/hello")
       public String hello() {
           return "Hello, secured world!";
       }
   }
   ```

### 3. Konfigurasi Spring Security
   - Buat kelas konfigurasi `SecurityConfig` menggunakan `SecurityFilterChain`.  
   - Atur Basic Authentication dengan user in-memory.  

   ```java
   @Configuration
   @EnableWebSecurity
   public class SecurityConfig {

       @Bean
       public UserDetailsService userDetailsService() {
           UserDetails user = User.withDefaultPasswordEncoder()
                   .username("user")
                   .password("password")
                   .roles("USER")
                   .build();
           return new InMemoryUserDetailsManager(user);
       }

       @Bean
       public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
           http
               .authorizeHttpRequests(auth -> auth
                   .anyRequest().authenticated()
               )
               .httpBasic();
           return http.build();
       }
   }
   ```

### 4. Menjalankan Aplikasi
   - Jalankan aplikasi dengan `mvn spring-boot:run`.  
   - Akses endpoint di: [http://localhost:8080/hello](http://localhost:8080/hello).  
   - Browser atau Postman akan meminta **Basic Authentication**.  
   - Gunakan username: `user`, password: `password`.  