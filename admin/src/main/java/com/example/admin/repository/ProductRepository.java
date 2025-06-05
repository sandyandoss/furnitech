package com.example.admin.repository;


import com.example.admin.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Find products by name (case-insensitive)
    List<Product> findByNameContainingIgnoreCase(String name);

    // Find products by category
    List<Product> findByCategoryIgnoreCase(String category);

    // Find products by brand
    List<Product> findByBrandIgnoreCase(String brand);

    // Find active products only
    List<Product> findByIsActiveTrue();

    // Find products within price range
    List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    // Find products by stock quantity greater than specified value
    List<Product> findByStockQuantityGreaterThan(Integer quantity);

    // Find out of stock products
    List<Product> findByStockQuantityEquals(Integer quantity);

    // Custom query to search products by multiple criteria
    @Query("SELECT p FROM Product p WHERE " +
            "(:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
            "(:category IS NULL OR LOWER(p.category) = LOWER(:category)) AND " +
            "(:brand IS NULL OR LOWER(p.brand) = LOWER(:brand)) AND " +
            "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
            "(:maxPrice IS NULL OR p.price <= :maxPrice) AND " +
            "(:isActive IS NULL OR p.isActive = :isActive)")
    Page<Product> findProductsByCriteria(
            @Param("name") String name,
            @Param("category") String category,
            @Param("brand") String brand,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("isActive") Boolean isActive,
            Pageable pageable);

    // Get all distinct categories
    @Query("SELECT DISTINCT p.category FROM Product p WHERE p.category IS NOT NULL ORDER BY p.category")
    List<String> findAllCategories();

    // Get all distinct brands
    @Query("SELECT DISTINCT p.brand FROM Product p WHERE p.brand IS NOT NULL ORDER BY p.brand")
    List<String> findAllBrands();

    // Count products by category
    @Query("SELECT p.category, COUNT(p) FROM Product p WHERE p.category IS NOT NULL GROUP BY p.category")
    List<Object[]> countProductsByCategory();

    // Find top selling products (assuming you have sales data later)
    @Query("SELECT p FROM Product p WHERE p.isActive = true ORDER BY p.stockQuantity DESC")
    List<Product> findTopProducts(Pageable pageable);
}