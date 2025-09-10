package com.module4.demo1.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.module4.demo1.entity.Product;
import com.module4.demo1.entity.Role;
import com.module4.demo1.entity.UserAccount;
import com.module4.demo1.repository.ProductRepository;
import com.module4.demo1.repository.RoleRepository;
import com.module4.demo1.repository.UserAccountRepository;

@RestController
@RequestMapping("/bootstrap")
@ConditionalOnProperty(prefix = "app.bootstrap", name = "enabled", havingValue = "true")
public class BootstrapController {

    private final RoleRepository roles;
    private final UserAccountRepository users;
    private final ProductRepository products;
    private final PasswordEncoder encoder;

    public BootstrapController(RoleRepository roles,
            UserAccountRepository users,
            ProductRepository products,
            PasswordEncoder encoder) {
        this.roles = roles;
        this.users = users;
        this.products = products;
        this.encoder = encoder;
    }

    @PostMapping
    public Map<String, Object> run() {
        Role user = roles.findByName("ROLE_USER").orElseGet(() -> {
            Role r = new Role();
            r.setName("ROLE_USER");
            return roles.save(r);
        });
        Role admin = roles.findByName("ROLE_ADMIN").orElseGet(() -> {
            Role r = new Role();
            r.setName("ROLE_ADMIN");
            return roles.save(r);
        });

        users.findByUsername("user").orElseGet(() -> {
            UserAccount u = new UserAccount();
            u.setUsername("user");
            u.setPassword(encoder.encode("password"));
            u.setRoles(Set.of(user));
            return users.save(u);
        });

        users.findByUsername("admin").orElseGet(() -> {
            UserAccount a = new UserAccount();
            a.setUsername("admin");
            a.setPassword(encoder.encode("admin123"));
            a.setRoles(Set.of(admin, user));
            return users.save(a);
        });

        if (products.count() == 0) {
            Product p1 = new Product();
            p1.setName("Keyboard");
            p1.setPrice(new BigDecimal("250000"));
            Product p2 = new Product();
            p2.setName("Mouse");
            p2.setPrice(new BigDecimal("150000"));
            products.save(p1);
            products.save(p2);
        }

        Map<String, Object> out = new HashMap<>();
        out.put("users", users.count());
        out.put("roles", roles.count());
        out.put("products", products.count());
        out.put("status", "ok");
        return out;
    }

    @GetMapping("/status")
    public Map<String, Object> status() {
        Map<String, Object> out = new HashMap<>();
        out.put("users", users.count());
        out.put("roles", roles.count());
        out.put("products", products.count());
        return out;
    }
}
