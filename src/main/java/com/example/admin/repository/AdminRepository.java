package com.example.admin.repository;

import com.example.admin.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    Optional<Admin> findByEmailAndPassword(String email, String password);

    Admin findByName(String name);

    Optional<Admin> findByEmail(String email);

    boolean existsByEmail(String email);
}
