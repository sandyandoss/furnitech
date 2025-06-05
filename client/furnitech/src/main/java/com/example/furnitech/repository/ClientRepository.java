package com.example.furnitech.repository;

import com.example.furnitech.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, Long> {
    Client findByName(String name);  // ✅ Add this line
}
