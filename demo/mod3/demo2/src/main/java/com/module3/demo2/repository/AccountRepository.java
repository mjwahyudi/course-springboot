package com.module3.demo2.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.module3.demo2.entity.Account;

import jakarta.persistence.LockModeType;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    // Optional: pessimistic write lock (blocks concurrent updaters)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from Account a where a.id = :id")
    Optional<Account> findForUpdate(@Param("id") Long id);
}
