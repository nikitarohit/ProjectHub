package com.projectmanagement.clientprojectmanagement.repository;

import com.projectmanagement.clientprojectmanagement.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    // Find user by email only (password check done in controller via BCrypt)
    User findByEmail(String email);

    // Check if email already exists (for signup duplicate check)
    boolean existsByEmail(String email);
}