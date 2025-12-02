package com.rentconnect.backend.repository;

import com.rentconnect.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // Custom query to find user by email (for login later)
    Optional<User> findByEmail(String email);
}