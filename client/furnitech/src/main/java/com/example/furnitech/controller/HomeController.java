package com.example.furnitech.controller;

import com.example.furnitech.model.Client;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/home")
    public String home(Model model, HttpSession session) {
        Client loggedInUser = (Client) session.getAttribute("loggedInUser");

        if (loggedInUser == null) {
            return "redirect:/login";
        }

        model.addAttribute("client", loggedInUser);
        return "home";
    }
}