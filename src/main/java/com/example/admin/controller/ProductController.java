package com.example.admin.controller;


import com.example.admin.model.Product;
import com.example.admin.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // ==================== WEB CONTROLLER METHODS ====================

    @GetMapping
    public String showProductsPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String brand,
            Model model) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() :
                Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Product> productPage;
        if (search != null || category != null || brand != null) {
            productPage = productService.searchProducts(search, category, brand,
                    null, null, null, pageable);
        } else {
            productPage = productService.getProductsWithPagination(pageable);
        }

        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("totalElements", productPage.getTotalElements());
        model.addAttribute("categories", productService.getAllCategories());
        model.addAttribute("brands", productService.getAllBrands());
        model.addAttribute("newProduct", new Product());

        // Add statistics
        model.addAttribute("totalProducts", productService.getTotalProductCount());
        model.addAttribute("averagePrice", productService.getAveragePrice());
        model.addAttribute("totalInventoryValue", productService.getTotalInventoryValue());

        return "products";
    }

    @PostMapping
    public String createProduct(@Valid @ModelAttribute("newProduct") Product product,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes,
                                Model model) {

        if (bindingResult.hasErrors()) {
            // Re-populate the model with necessary data
            model.addAttribute("products", productService.getAllProducts());
            model.addAttribute("categories", productService.getAllCategories());
            model.addAttribute("brands", productService.getAllBrands());
            return "products";
        }

        try {
            productService.createProduct(product);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Product '" + product.getName() + "' created successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Error creating product: " + e.getMessage());
        }

        return "redirect:/products";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Optional<Product> product = productService.getProductById(id);
        if (product.isPresent()) {
            model.addAttribute("product", product.get());
            model.addAttribute("categories", productService.getAllCategories());
            model.addAttribute("brands", productService.getAllBrands());
            return "edit-product";
        } else {
            return "redirect:/products?error=Product not found";
        }
    }

    @PostMapping("/{id}/update")
    public String updateProduct(@PathVariable Long id,
                                @Valid @ModelAttribute Product product,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes,
                                Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("product", product);
            model.addAttribute("categories", productService.getAllCategories());
            model.addAttribute("brands", productService.getAllBrands());
            return "edit-product";
        }

        try {
            productService.updateProduct(id, product);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Product updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Error updating product: " + e.getMessage());
        }

        return "redirect:/products";
    }

    @PostMapping("/{id}/delete")
    public String deleteProduct(@PathVariable Long id,
                                RedirectAttributes redirectAttributes) {
        try {
            Optional<Product> product = productService.getProductById(id);
            if (product.isPresent()) {
                productService.deleteProduct(id);
                redirectAttributes.addFlashAttribute("successMessage",
                        "Product '" + product.get().getName() + "' deleted successfully!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "Product not found!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Error deleting product: " + e.getMessage());
        }

        return "redirect:/products";
    }

    @PostMapping("/{id}/toggle-status")
    public String toggleProductStatus(@PathVariable Long id,
                                      RedirectAttributes redirectAttributes) {
        try {
            productService.toggleProductStatus(id);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Product status updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Error updating product status: " + e.getMessage());
        }

        return "redirect:/products";
    }

    // ==================== REST API ENDPOINTS ====================

    @RestController
    @RequestMapping("/api/products")
    public static class ProductRestController {

        private final ProductService productService;

        @Autowired
        public ProductRestController(ProductService productService) {
            this.productService = productService;
        }

        @GetMapping
        public ResponseEntity<List<Product>> getAllProducts() {
            List<Product> products = productService.getAllProducts();
            return ResponseEntity.ok(products);
        }

        @GetMapping("/{id}")
        public ResponseEntity<Product> getProductById(@PathVariable Long id) {
            Optional<Product> product = productService.getProductById(id);
            return product.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        }

        @PostMapping
        public ResponseEntity<Product> createProduct(@Valid @RequestBody Product product) {
            try {
                Product createdProduct = productService.createProduct(product);
                return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
            } catch (Exception e) {
                return ResponseEntity.badRequest().build();
            }
        }

        @PutMapping("/{id}")
        public ResponseEntity<Product> updateProduct(@PathVariable Long id,
                                                     @Valid @RequestBody Product product) {
            try {
                Product updatedProduct = productService.updateProduct(id, product);
                return ResponseEntity.ok(updatedProduct);
            } catch (RuntimeException e) {
                return ResponseEntity.notFound().build();
            } catch (Exception e) {
                return ResponseEntity.badRequest().build();
            }
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
            try {
                productService.deleteProduct(id);
                return ResponseEntity.noContent().build();
            } catch (RuntimeException e) {
                return ResponseEntity.notFound().build();
            }
        }

        @GetMapping("/search")
        public ResponseEntity<List<Product>> searchProducts(@RequestParam String name) {
            List<Product> products = productService.searchProductsByName(name);
            return ResponseEntity.ok(products);
        }

        @GetMapping("/category/{category}")
        public ResponseEntity<List<Product>> getProductsByCategory(@PathVariable String category) {
            List<Product> products = productService.getProductsByCategory(category);
            return ResponseEntity.ok(products);
        }

        @GetMapping("/active")
        public ResponseEntity<List<Product>> getActiveProducts() {
            List<Product> products = productService.getActiveProducts();
            return ResponseEntity.ok(products);
        }

        @PatchMapping("/{id}/stock")
        public ResponseEntity<Product> updateStock(@PathVariable Long id,
                                                   @RequestParam Integer quantity) {
            try {
                Product updatedProduct = productService.updateStock(id, quantity);
                return ResponseEntity.ok(updatedProduct);
            } catch (RuntimeException e) {
                return ResponseEntity.notFound().build();
            } catch (Exception e) {
                return ResponseEntity.badRequest().build();
            }
        }
    }
}