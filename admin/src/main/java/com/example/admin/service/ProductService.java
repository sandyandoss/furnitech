package com.example.admin.service;


import com.example.admin.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProductService {

    // Basic CRUD operations
    Product createProduct(Product product);
    Product updateProduct(Long id, Product product);
    Optional<Product> getProductById(Long id);
    List<Product> getAllProducts();
    void deleteProduct(Long id);

    // Advanced search and filtering
    Page<Product> getProductsWithPagination(Pageable pageable);
    List<Product> searchProductsByName(String name);
    List<Product> getProductsByCategory(String category);
    List<Product> getProductsByBrand(String brand);
    List<Product> getActiveProducts();
    List<Product> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice);

    // Stock management
    List<Product> getProductsInStock();
    List<Product> getOutOfStockProducts();
    Product updateStock(Long id, Integer quantity);

    // Utility methods
    List<String> getAllCategories();
    List<String> getAllBrands();
    long getTotalProductCount();
    BigDecimal getAveragePrice();
    BigDecimal getTotalInventoryValue();

    // Advanced search with multiple criteria
    Page<Product> searchProducts(String name, String category, String brand,
                                 BigDecimal minPrice, BigDecimal maxPrice,
                                 Boolean isActive, Pageable pageable);

    // Bulk operations
    List<Product> createMultipleProducts(List<Product> products);
    void toggleProductStatus(Long id);
}
