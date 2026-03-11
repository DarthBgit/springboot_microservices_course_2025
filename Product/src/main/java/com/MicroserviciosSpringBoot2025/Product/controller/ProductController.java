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

@RestController
@RequestMapping("api/v1/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Value("${country.code}")
    private String countryCode; // ES, UK, US, CN get this value from docker-compose

    @Value("${country.name}")
    private String countryName; // get this value from application.properties or docker-compose

    private final HealthEndpoint healthEndpoint;

    public ProductController(HealthEndpoint healthEndpoint) {
        this.healthEndpoint = healthEndpoint;
    }

    /**
     * Retrieves all products.
     * @return A ResponseEntity containing a list of all products.
     */
    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        return ResponseEntity.ok(productService.findAll());
    }

    /**
     * Retrieves products filtered by a specific country code.
     * Example: http://localhost:8080/api/v1/products/country?countryCode=ES
     * @param countryCode The country code to filter products by (e.g., "ES", "UK", "US", "CN").
     * @return A ResponseEntity containing a list of products for the given country code.
     */
    @GetMapping("/country")
    public ResponseEntity<List<ProductDTO>> getProductsByCountry(@RequestParam String countryCode) {
        return ResponseEntity.ok(productService.findByCountryCode(countryCode));
    }

    /**
     * Retrieves a product by its unique identifier.
     * @param id The ID of the product to retrieve.
     * @return A ResponseEntity containing the found product.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.findById(id));
    }

    /**
     * Provides status information for the product service instance, including country details
     * and health status. This endpoint simulates the status of different instances (ES, UK, US, CN).
     * @return A Map containing country code, country name, health status, and current timestamp.
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
     * Creates a new product.
     * @param product The product object to be created.
     * @return A ResponseEntity containing the created product and HTTP status CREATED.
     */
    @PostMapping
    public ResponseEntity<Product> createProduct(@Valid @RequestBody Product product) {
        return new ResponseEntity<>(productService.save(product), HttpStatus.CREATED);
    }

    /**
     * Updates an existing product identified by its ID.
     * @param id The ID of the product to update.
     * @param productDetails The updated product details.
     * @return A ResponseEntity containing the updated product.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long id, @Valid @RequestBody Product productDetails) {
        return ResponseEntity.ok(productService.update(id, productDetails));
    }

    /**
     * Deletes a product by its unique identifier.
     * @param id The ID of the product to delete.
     * @return A ResponseEntity with no content and HTTP status NO_CONTENT.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
