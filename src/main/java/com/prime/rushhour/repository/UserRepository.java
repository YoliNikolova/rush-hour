package com.prime.rushhour.repository;

import com.prime.rushhour.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository extends JpaRepository<User,Integer> {
   // Optional<User> finUserByFirstName(String fistName);
}
