package com.module3.demo2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.module3.demo2.entity.TransferAudit;

@Repository
public interface TransferAuditRepository extends JpaRepository<TransferAudit, Long> {}
