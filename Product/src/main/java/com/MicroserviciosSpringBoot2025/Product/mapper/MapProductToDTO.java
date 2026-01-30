package com.MicroserviciosSpringBoot2025.Product.mapper;

import com.MicroserviciosSpringBoot2025.Product.entity.Product;
import com.MicroserviciosSpringBoot2025.Product.entity.ProductDTO;

public class MapProductToDTO {

    public static ProductDTO map(Product product) {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setName(product.getName());
        productDTO.setDescription(product.getDescription());
        productDTO.setPrice(product.getPrice());
        productDTO.setCountryCode(product.getCountryCode().name());
        productDTO.setCurrency(product.getCurrency().name());
        return productDTO;
    }
}
