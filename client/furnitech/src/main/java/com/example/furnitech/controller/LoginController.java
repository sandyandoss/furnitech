package com.example.furnitech.controller;

import com.example.furnitech.model.Client;
import com.example.furnitech.repository.ClientRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private ClientRepository clientRepository;

    @GetMapping("/login")
    public String showLoginPage(Model model,
                                @RequestParam(value = "error", required = false) String error,
                                @RequestParam(value = "logout", required = false) String logout) {
        model.addAttribute("client", new Client());

        if (error != null) {
            model.addAttribute("error", "Invalid username or password.");
        }
        if (logout != null) {
            model.addAttribute("message", "You've been logged out successfully.");
        }

        return "login";
    }

    @PostMapping("/login")
    public String processLogin(@ModelAttribute("client") Client client,
                               HttpSession session,
                               Model model) {
        try {
            // Validate inputs
            if (client.getName() == null || client.getName().trim().isEmpty()) {
                model.addAttribute("error", "Username is required");
                return "login";
            }

            if (client.getPassword() == null || client.getPassword().trim().isEmpty()) {
                model.addAttribute("error", "Password is required");
                return "login";
            }

            // Find client in database
            Client existingClient = clientRepository.findByName(client.getName().trim());

            if (existingClient == null) {
                logger.warn("Login attempt for non-existent user: {}", client.getName());
                model.addAttribute("error", "Invalid credentials");
                return "login";
            }

            // Verify password (in real app, use password encoder)
            if (!existingClient.getPassword().equals(client.getPassword())) {
                logger.warn("Failed login attempt for user: {}", client.getName());
                model.addAttribute("error", "Invalid credentials");
                return "login";
            }

            // Successful login
            session.setAttribute("loggedInUser", existingClient);
            logger.info("User {} logged in successfully", existingClient.getName());
            return "redirect:/home";

        } catch (Exception e) {
            logger.error("Login error: ", e);
            model.addAttribute("error", "An unexpected error occurred. Please try again.");
            return "login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login?logout=true";
    }
}