# 🚀 Practice 402: Secure Endpoint with Spring Security (JWT & RBAC)

Latihan ini ditujukan agar peserta dapat mengamankan endpoint menggunakan **Spring Security** dan menerapkan **JWT (JSON Web Token)** serta **RBAC (Role-Based Access Control)**.

## Learning Goals

1. Peserta dapat mengimplementasikan **Spring Security**.  
2. Peserta dapat menerapkan **JWT** dan **RBAC** untuk mengamankan endpoint.  

## Praktek yang dilakukan (Spring Boot 3.x)

### 1. Inisialisasi Proyek
   - Buka https://start.spring.io  
   - Konfigurasi:  
     - Project: **Maven** | Language: **Java** | Spring Boot: **3.x**  
     - Group: `com.practice402`  
     - Artifact: `springboot402`  
     - Dependencies: **Spring Web**, **Spring Security**, **H2 Database**, **Validation**  
   - (JWT) Tambahkan dependensi berikut di `pom.xml` (versi dapat menyesuaikan):
     ```xml
     <dependencies>
       <!-- ... starter dependencies ... -->
       <dependency>
         <groupId>io.jsonwebtoken</groupId>
         <artifactId>jjwt-api</artifactId>
         <version>0.11.5</version>
       </dependency>
       <dependency>
         <groupId>io.jsonwebtoken</groupId>
         <artifactId>jjwt-impl</artifactId>
         <version>0.11.5</version>
         <scope>runtime</scope>
       </dependency>
       <dependency>
         <groupId>io.jsonwebtoken</groupId>
         <artifactId>jjwt-jackson</artifactId>
         <version>0.11.5</version>
         <scope>runtime</scope>
       </dependency>
     </dependencies>
     ```

### 2. Buat DDL dan Data H2 yang Konsisten (schema.sql & data.sql)
   - Lokasi file: `src/main/resources/schema.sql` dan `src/main/resources/data.sql`
   - **schema.sql** (H2 compatible; nama tabel jamak & konsisten dengan entitas JPA):
     ```sql
     DROP TABLE IF EXISTS user_roles;
     DROP TABLE IF EXISTS roles;
     DROP TABLE IF EXISTS users;

     CREATE TABLE users (
       id BIGINT AUTO_INCREMENT PRIMARY KEY,
       username VARCHAR(50) NOT NULL UNIQUE,
       password VARCHAR(100) NOT NULL,
       enabled BOOLEAN NOT NULL DEFAULT TRUE
     );

     CREATE TABLE roles (
       id BIGINT AUTO_INCREMENT PRIMARY KEY,
       name VARCHAR(50) NOT NULL UNIQUE
     );

     CREATE TABLE user_roles (
       user_id BIGINT NOT NULL,
       role_id BIGINT NOT NULL,
       PRIMARY KEY (user_id, role_id),
       CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
       CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
     );
     ```
   - **data.sql** (gunakan *BCrypt* agar Spring Security dapat memverifikasi password):
     > Catatan: Prefix `{noop}` dapat dipakai untuk demo tanpa encoder, tetapi **direkomendasikan BCrypt**.
     ```sql
     -- Roles
     INSERT INTO roles (name) VALUES ('ROLE_USER');
     INSERT INTO roles (name) VALUES ('ROLE_ADMIN');

     -- Users (password: 'password' di-hash BCrypt)
     -- BCrypt untuk "password" contoh: $2a$10$7J0g9m4mKkE8bV2Gk7m9Qe2TjPZb2g7VbwV8G1oZCq0Z8w2N1nW9C
     -- Anda boleh mengganti dengan hash BCrypt Anda sendiri.
     INSERT INTO users (username, password, enabled) VALUES ('user', '$2a$10$7J0g9m4mKkE8bV2Gk7m9Qe2TjPZb2g7VbwV8G1oZCq0Z8w2N1nW9C', TRUE);
     INSERT INTO users (username, password, enabled) VALUES ('admin', '$2a$10$7J0g9m4mKkE8bV2Gk7m9Qe2TjPZb2g7VbwV8G1oZCq0Z8w2N1nW9C', TRUE);

     -- Mapping user <-> roles
     INSERT INTO user_roles (user_id, role_id) VALUES (1, 1); -- user -> ROLE_USER
     INSERT INTO user_roles (user_id, role_id) VALUES (2, 1); -- admin -> ROLE_USER
     INSERT INTO user_roles (user_id, role_id) VALUES (2, 2); -- admin -> ROLE_ADMIN
     ```
   - **application.properties** (agar `schema.sql` & `data.sql` terbaca di Spring Boot 3):
     ```properties
     # H2 console (opsional untuk inspeksi)
     spring.h2.console.enabled=true
     spring.h2.console.path=/h2

     # Datasource in-memory
     spring.datasource.url=jdbc:h2:mem:testdb;MODE=LEGACY;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
     spring.datasource.driverClassName=org.h2.Driver
     spring.datasource.username=sa
     spring.datasource.password=

     # Pastikan init SQL berjalan setelah JPA siap
     spring.jpa.hibernate.ddl-auto=none
     spring.sql.init.mode=always
     spring.jpa.defer-datasource-initialization=true

     # Logging SQL (opsional)
     spring.jpa.show-sql=true
     ```

### 3. Entitas JPA (User, Role, UserRole)
   - Gunakan nama tabel sesuai DDL di atas agar *auto mapping* tidak gagal.
   - Contoh ringkas (sesuaikan paket & import):
     ```java
     @Entity
     @Table(name = "users")
     public class UserEntity {
       @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
       private Long id;

       @Column(nullable = false, unique = true, length = 50)
       private String username;

       @Column(nullable = false, length = 100)
       private String password;

       @Column(nullable = false)
       private boolean enabled = true;

       @ManyToMany(fetch = FetchType.EAGER)
       @JoinTable(
         name = "user_roles",
         joinColumns = @JoinColumn(name = "user_id"),
         inverseJoinColumns = @JoinColumn(name = "role_id")
       )
       private Set<RoleEntity> roles = new HashSet<>();
     }

     @Entity
     @Table(name = "roles")
     public class RoleEntity {
       @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
       private Long id;

       @Column(nullable = false, unique = true, length = 50)
       private String name;
     }
     ```

### 4. Service: UserDetailsService berbasis DB
   ```java
   @Service
   public class JpaUserDetailsService implements UserDetailsService {
     private final UserRepository userRepo;

     public JpaUserDetailsService(UserRepository userRepo) {
       this.userRepo = userRepo;
     }

     @Override
     public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
       UserEntity u = userRepo.findByUsername(username)
         .orElseThrow(() -> new UsernameNotFoundException("User not found"));

       List<GrantedAuthority> auth = u.getRoles().stream()
         .map(r -> new SimpleGrantedAuthority(r.getName()))
         .toList();

       return new org.springframework.security.core.userdetails.User(
         u.getUsername(), u.getPassword(), u.isEnabled(), true, true, true, auth
       );
     }
   }
   ```

### 5. JWT Utility & AuthController
   - Buat util sederhana untuk membuat & memverifikasi token, misalnya `JwtUtil` (gunakan secret HS256):
     ```java
     @Component
     public class JwtUtil {
       @Value("${app.jwt.secret:dev-secret-change-me}")
       private String secret;

       @Value("${app.jwt.ttl-seconds:3600}")
       private long ttlSeconds;

       public String generateToken(String username, Collection<? extends GrantedAuthority> roles) {
         Date now = new Date();
         Date exp = new Date(now.getTime() + ttlSeconds * 1000);
         return Jwts.builder()
             .setSubject(username)
             .claim("roles", roles.stream().map(GrantedAuthority::getAuthority).toList())
             .setIssuedAt(now)
             .setExpiration(exp)
             .signWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
             .compact();
       }

       public Jws<Claims> parse(String token) {
         return Jwts.parserBuilder()
           .setSigningKey(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)))
           .build()
           .parseClaimsJws(token);
       }
     }
     ```
   - Controller login `POST /auth/login` (terima username/password, kembalikan JWT):
     ```java
     @RestController
     @RequestMapping("/auth")
     public class AuthController {
       private final AuthenticationManager authManager;
       private final JwtUtil jwt;

       public AuthController(AuthenticationManager authManager, JwtUtil jwt) {
         this.authManager = authManager;
         this.jwt = jwt;
       }

       @PostMapping("/login")
       public Map<String, Object> login(@RequestBody Map<String, String> req) {
         Authentication auth = authManager.authenticate(
           new UsernamePasswordAuthenticationToken(req.get("username"), req.get("password"))
         );
         String token = jwt.generateToken(auth.getName(), auth.getAuthorities());
         return Map.of("access_token", token, "token_type", "Bearer");
       }
     }
     ```

### 6. JWT Filter & SecurityConfig (RBAC)
   ```java
   @Component
   public class JwtAuthFilter extends OncePerRequestFilter {
     private final JwtUtil jwt;
     private final UserDetailsService uds;

     public JwtAuthFilter(JwtUtil jwt, UserDetailsService uds) {
       this.jwt = jwt; this.uds = uds;
     }

     @Override
     protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
         throws ServletException, IOException {
       String auth = req.getHeader("Authorization");
       if (auth != null && auth.startsWith("Bearer ")) {
         String token = auth.substring(7);
         try {
           var claims = jwt.parse(token).getBody();
           String username = claims.getSubject();
           UserDetails user = uds.loadUserByUsername(username);
           UsernamePasswordAuthenticationToken at =
             new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
           at.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
           SecurityContextHolder.getContext().setAuthentication(at);
         } catch (Exception e) {
           // token invalid/expired -> biarkan ke entry point
         }
       }
       chain.doFilter(req, res);
     }
   }
   ```

   ```java
   @Configuration
   @EnableWebSecurity
   @EnableMethodSecurity
   public class SecurityConfig {
     private final JwtAuthFilter jwtFilter;

     public SecurityConfig(JwtAuthFilter jwtFilter) {
       this.jwtFilter = jwtFilter;
     }

     @Bean
     public PasswordEncoder passwordEncoder() {
       return new BCryptPasswordEncoder();
     }

     @Bean
     public AuthenticationManager authenticationManager(AuthenticationConfiguration cfg) throws Exception {
       return cfg.getAuthenticationManager();
     }

     @Bean
     public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
       http.csrf(csrf -> csrf.disable())
         .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
         .authorizeHttpRequests(auth -> auth
           .requestMatchers("/auth/login", "/h2/**").permitAll()
           .requestMatchers("/hello/admin").hasRole("ADMIN")
           .requestMatchers("/hello/user").hasAnyRole("USER","ADMIN")
           .anyRequest().authenticated()
         )
         .headers(h -> h.frameOptions(f -> f.disable()))  // untuk H2 console
         .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
         .httpBasic(httpBasic -> {}); // opsional
       return http.build();
     }
   }
   ```

### 7. Controller Uji RBAC
   ```java
   @RestController
   @RequestMapping("/hello")
   public class HelloController {

     @GetMapping("/user")
     @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
     public String helloUser() {
       return "Hello USER/ADMIN (JWT)";
     }

     @GetMapping("/admin")
     @PreAuthorize("hasRole('ADMIN')")
     public String helloAdmin() {
       return "Hello ADMIN (JWT)";
     }
   }
   ```

### 8. Menjalankan & Menguji
   - Jalankan: `mvn spring-boot:run`
   - Login untuk memperoleh token:
     ```bash
     curl -X POST http://localhost:8080/auth/login        -H "Content-Type: application/json"        -d '{"username":"user","password":"password"}'
     ```
   - Panggil endpoint dengan JWT:
     ```bash
     curl http://localhost:8080/hello/user -H "Authorization: Bearer <access_token>"
     curl http://localhost:8080/hello/admin -H "Authorization: Bearer <access_token>"
     ```

> Dengan langkah di atas, **DDL dipastikan terinisialisasi** di H2 melalui `schema.sql` dan `data.sql`, serta Spring Boot 3.x dikonfigurasi agar membaca script tersebut sebelum aplikasi siap menerima request.