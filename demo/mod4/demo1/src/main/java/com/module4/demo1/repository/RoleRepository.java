package com.module4.demo1.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.module4.demo1.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
  Optional<Role> findByName(String name);
}
