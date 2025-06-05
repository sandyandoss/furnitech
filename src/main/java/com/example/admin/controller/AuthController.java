package com.example.admin.controller;

import com.example.admin.model.Admin;
import com.example.admin.repository.AdminRepository;
import com.example.admin.request.LoginRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api")
public class AuthController {

    @Autowired
    private AdminRepository adminRepository;
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();

        Optional<Admin> adminOpt = adminRepository.findByEmailAndPassword(email, password);
        if (adminOpt.isPresent()) {
            return ResponseEntity.ok().build(); // or return admin data
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }}