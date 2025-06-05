package com.example.furnitech.controller;

import com.example.furnitech.model.Product;
import com.example.furnitech.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor // this injects productRepository automatically
public class ClientController {

    private final ProductRepository productRepository;

    @GetMapping("/products")
    public String viewProducts(Model model) {
        List<Product> products = productRepository.findAll();
        model.addAttribute("products", products);
        return "products"; // Matches templates/product.html
    }
}