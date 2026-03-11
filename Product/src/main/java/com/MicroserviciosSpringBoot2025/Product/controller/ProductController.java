package com.MicroserviciosSpringBoot2025.Product.controller;

import com.MicroserviciosSpringBoot2025.Product.entity.Product;
import com.MicroserviciosSpringBoot2025.Product.entity.ProductDTO;
import com.MicroserviciosSpringBoot2025.Product.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handles all incoming HTTP requests for the /api/v1/products endpoint.
 * This controller is the main entry point for interacting with Product data.
 */
@RestController
@RequestMapping("api/v1/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    // These values are injected from environment variables, making each instance unique.
    // This is key for simulating a multi-country deployment.
    @Value("${country.code}")
    private String countryCode; // ES, UK, US, CN get this value from docker-compose

    @Value("${country.name}")
    private String countryName; // get this value from application.properties or docker-compose

    // Spring Actuator's HealthEndpoint is used to get the real health status of the application.
    private final HealthEndpoint healthEndpoint;

    public ProductController(HealthEndpoint healthEndpoint) {
        this.healthEndpoint = healthEndpoint;
    }

    /**
     * Fetches all products from the database.
     */
    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        return ResponseEntity.ok(productService.findAll());
    }

    /**
     * Finds all products belonging to a specific country.
     * This allows the Item service to request data only from relevant instances.
     * e.g., localhost:8080/api/v1/products/country?countryCode=ES
     */
    @GetMapping("/country")
    public ResponseEntity<List<ProductDTO>> getProductsByCountry(@RequestParam String countryCode) {
        return ResponseEntity.ok(productService.findByCountryCode(countryCode));
    }

    /**
     * Gets a single product by its primary key.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.findById(id));
    }

    /**
     * A custom endpoint for other services to check the status and identity of this instance.
     * It combines the instance's location (from environment variables) with its live health status.
     */
    @GetMapping("/status")
    public Map<String, Object> getStatus() {
        Map<String, Object> response = new HashMap<>();
        String realStatus = healthEndpoint.health().getStatus().getCode();
        response.put("country_code", countryCode);
        response.put("country_name", countryName);
        response.put("status", realStatus);
        response.put("timestamp", LocalDateTime.now());
        return response;
    }

    /**
     * Creates a new product. The @Valid annotation triggers input validation.
     */
    @PostMapping
    public ResponseEntity<Product> createProduct(@Valid @RequestBody Product product) {
        return new ResponseEntity<>(productService.save(product), HttpStatus.CREATED);
    }

    /**
     * Updates an existing product.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long id, @Valid @RequestBody Product productDetails) {
        return ResponseEntity.ok(productService.update(id, productDetails));
    }

    /**
     * Deletes a product from the database.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
