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

    @Value("${app.country}")
    private String countryNode; // ES, UK, US, CN get this value from docker-compose

    @Value("${server.port}")
    private int serverPort; // get this value from application.properties or docker-compose

    private final HealthEndpoint healthEndpoint;

    public ProductController(HealthEndpoint healthEndpoint) {
        this.healthEndpoint = healthEndpoint;
    }

    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        return ResponseEntity.ok(productService.findAll());
    }

    @GetMapping("/country")
    public ResponseEntity<List<ProductDTO>> getProductsByCountry(@RequestParam String countryCode) {
        return ResponseEntity.ok(productService.findByCountryCode(countryCode));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.findById(id));
    }

    // Status endpoint with additional info to simulate th status of the different instances ES, UK, US, CN
    @GetMapping("/status")
    public Map<String, Object> getStatus() {
        Map<String, Object> response = new HashMap<>();

        String realStatus = healthEndpoint.health().getStatus().getCode();

        response.put("node", countryNode);
        response.put("port", serverPort);
        response.put("status", realStatus);
        response.put("timestamp", LocalDateTime.now());

        return response;
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(@Valid @RequestBody Product product) {
        return new ResponseEntity<>(productService.save(product), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long id, @Valid @RequestBody Product productDetails) {
        return ResponseEntity.ok(productService.update(id, productDetails));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
