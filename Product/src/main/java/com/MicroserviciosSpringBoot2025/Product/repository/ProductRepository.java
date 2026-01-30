package com.MicroserviciosSpringBoot2025.Product.repository;

import com.MicroserviciosSpringBoot2025.Product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    public List<Product> findProductsByCountryCode(String countryCode);
}
