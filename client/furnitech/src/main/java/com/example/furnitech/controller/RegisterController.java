package com.example.furnitech.controller;

import com.example.furnitech.model.Client;
import com.example.furnitech.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RegisterController {

    @Autowired
    private ClientRepository clientRepository;

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("client", new Client());
        return "register";
    }

    @PostMapping("/register")
    public String processRegister(Client client, Model model) {
        try {
            clientRepository.save(client);
            return "redirect:/login";
        } catch (Exception e) {
            model.addAttribute("error", "Registration failed: " + e.getMessage());
            return "register";
        }

    }

}
