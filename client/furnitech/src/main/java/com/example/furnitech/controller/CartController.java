package com.example.furnitech.controller;


import com.example.furnitech.model.Product;
import com.example.furnitech.model.CartItem;
import com.example.furnitech.repository.ProductRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/cart")
public class CartController {

    private final ProductRepository productRepository;

    @PostMapping("/add/{productId}")
    public String addToCart(@PathVariable Long productId,
                            @RequestParam(defaultValue = "1") int quantity,
                            HttpSession session) {

        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) {
            return "redirect:/?error=ProductNotFound";
        }

        Map<Long, CartItem> cart = (Map<Long, CartItem>) session.getAttribute("cart");
        if (cart == null) {
            cart = new HashMap<>();
        }

        if (cart.containsKey(productId)) {
            CartItem item = cart.get(productId);
            item.setQuantity(item.getQuantity() + quantity);
        } else {
            cart.put(productId, new CartItem(product, quantity));
        }

        session.setAttribute("cart", cart);
        return "redirect:/products?success=added";
    }

    @GetMapping
    public String viewCart(HttpSession session, Model model) {
        Map<Long, CartItem> cart = (Map<Long, CartItem>) session.getAttribute("cart");
        model.addAttribute("cartItems", cart != null ? cart.values() : Collections.emptyList());
        return "cart/view";
    }

    @PostMapping("/remove/{productId}")
    public String removeFromCart(@PathVariable Long productId, HttpSession session) {
        Map<Long, CartItem> cart = (Map<Long, CartItem>) session.getAttribute("cart");
        if (cart != null) {
            cart.remove(productId);
            session.setAttribute("cart", cart);
        }
        return "redirect:/cart";
    }
}
