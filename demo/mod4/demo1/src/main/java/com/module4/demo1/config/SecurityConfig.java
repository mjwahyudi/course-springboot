package com.module4.demo1.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.module4.demo1.entity.UserAccount;
import com.module4.demo1.repository.UserAccountRepository;

@Configuration
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // OK for demo + Basic; keep enabled for forms
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/health", "/bootstrap/**").permitAll()
                    .anyRequest().authenticated())
            .httpBasic(Customizer.withDefaults());
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(UserAccountRepository users) {
        return username -> {
            UserAccount ua = users.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            return new User(
                    ua.getUsername(),
                    ua.getPassword(),
                    ua.isEnabled(),
                    true, true, true,
                    ua.getRoles().stream()
                            .map(r -> new SimpleGrantedAuthority(r.getName())) // already "ROLE_*"
                            .toList());
        };
    }
}