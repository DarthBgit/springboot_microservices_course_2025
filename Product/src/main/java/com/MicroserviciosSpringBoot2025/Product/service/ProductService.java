package com.MicroserviciosSpringBoot2025.Product.service;

import com.MicroserviciosSpringBoot2025.Product.entity.Country;
import com.MicroserviciosSpringBoot2025.Product.entity.Product;
import com.MicroserviciosSpringBoot2025.Product.entity.ProductDTO;
import com.MicroserviciosSpringBoot2025.Product.exception.ResourceNotFoundException;
import com.MicroserviciosSpringBoot2025.Product.mapper.MapProductToDTO;
import com.MicroserviciosSpringBoot2025.Product.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {


    final private ProductRepository productRepository;
    // Inject the Environment to get the runtime port
    @Autowired
    private Environment environment;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public List<ProductDTO> findAll(){
        return productRepository.findAll().stream()
        // Injecting the port information into the product
                .map(product -> {
                    product.setPort(environment.getProperty("local.server.port", Integer.class));
                    return MapProductToDTO.map(product);
                }).collect(Collectors.toList());
    }

    @Transactional
    public List<ProductDTO> findByCountryCode(String countryCode) {
        Country countryEnum = Country.valueOf(countryCode.toUpperCase());

        return productRepository.findProductsByCountryCode(countryEnum).stream()
                .map(product -> {
                    product.setPort(environment.getProperty("local.server.port", Integer.class));
                    return MapProductToDTO.map(product);
                }).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProductDTO findById(Long id) {
        return productRepository.findById(id).map(product -> {
            // Injecting the port information into the product
            product.setPort(environment.getProperty("local.server.port", Integer.class));
            return MapProductToDTO.map(product);
        })
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    @Transactional
    public Product save(Product product) {
        return productRepository.save(product);
    }

    @Transactional
    public ProductDTO update(Long id, Product productDetails) {
        Product existingProduct = findEntityById(id); // Reuse findById to handle not-found case

        existingProduct.setName(productDetails.getName());
        existingProduct.setDescription(productDetails.getDescription());
        existingProduct.setPrice(productDetails.getPrice());
        existingProduct.setCountryCode(productDetails.getCountryCode());
        existingProduct.setCurrency(productDetails.getCurrency());

        Product updatedProduct = productRepository.save(existingProduct);
        updatedProduct.setPort(environment.getProperty("local.server.port", Integer.class));

        return MapProductToDTO.map(updatedProduct);
    }

    @Transactional
    public void deleteById(Long id) {
        Product productToDelete = findEntityById(id); // Ensures product exists before attempting to delete
        productRepository.delete(productToDelete);
    }

    /**
     * Return the Product entity
     * @param id
     * @return
     */
    private Product findEntityById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }
}
