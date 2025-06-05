package com.example.admin.controller;

import com.example.admin.model.Admin;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RegisterController {

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("admin", new Admin());
        return "register";  // Must correspond to register.html in templates
    }

    @PostMapping("/register")
    public String processRegister(Admin admin) {
        // your save logic here
        return "redirect:/login";
    }

    // REMOVE this method from here
    // @GetMapping("/home")
    // public String showHomePage() {
    //     return "home";
    // }
}