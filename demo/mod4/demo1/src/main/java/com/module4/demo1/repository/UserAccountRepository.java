package com.module4.demo1.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.module4.demo1.entity.UserAccount;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {
    Optional<UserAccount> findByUsername(String username);
}
