package com.prime.rushhour.repository;

import com.prime.rushhour.entities.Role;
import com.prime.rushhour.entities.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role,Integer> {
    Optional<Role> findByName(RoleType name);
}
