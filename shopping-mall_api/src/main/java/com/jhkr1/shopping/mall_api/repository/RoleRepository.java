package com.jhkr1.shopping.mall_api.repository;

import com.jhkr1.shopping.mall_api.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
}
