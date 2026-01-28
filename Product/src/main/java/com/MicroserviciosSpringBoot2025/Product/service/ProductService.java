package com.MicroserviciosSpringBoot2025.Product.service;

import com.MicroserviciosSpringBoot2025.Product.entity.Product;
import com.MicroserviciosSpringBoot2025.Product.exception.ResourceNotFoundException;
import com.MicroserviciosSpringBoot2025.Product.repository.ProductRepository;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ProductService {


    final private ProductRepository productRepository;
    final private Environment environment;

    public ProductService(ProductRepository productRepository, Environment environment) {
        this.productRepository = productRepository;
        this.environment = environment;
    }

    @Transactional(readOnly = true)
    public List<Product> findAll(){
        return productRepository.findAll().stream()
                .map(product -> {
                    // Injecting the port information into each product
                    product.setPort(Integer.parseInt(environment.getProperty("local.server.port")));
                    return product;
                }).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Product findById(Long id) {
        return productRepository.findById(id).map(product -> {
            // Injecting the port information into the product
            product.setPort(Integer.parseInt(Objects.requireNonNull(environment.getProperty("local.server.port"))));
            return product;
        })
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    @Transactional
    public Product save(Product product) {
        return productRepository.save(product);
    }

    @Transactional
    public Product update(Long id, Product productDetails) {
        Product existingProduct = findById(id); // Reuse findById to handle not-found case

        existingProduct.setName(productDetails.getName());
        existingProduct.setPrice(productDetails.getPrice());
        // The creation date should not be updated

        return productRepository.save(existingProduct);
    }

    @Transactional
    public void deleteById(Long id) {
        Product productToDelete = findById(id); // Ensures product exists before attempting to delete
        productRepository.delete(productToDelete);
    }
}
