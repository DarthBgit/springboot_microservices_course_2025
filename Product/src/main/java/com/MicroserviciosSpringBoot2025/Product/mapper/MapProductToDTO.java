package com.MicroserviciosSpringBoot2025.Product.mapper;

import com.MicroserviciosSpringBoot2025.Product.entity.Product;
import com.MicroserviciosSpringBoot2025.Product.entity.ProductDTO;

public class MapProductToDTO {

    /**
     * Maps a Product entity to a ProductDTO.
     * This static method converts a Product object, including its name, description, price,
     * country code, currency, and the port it was served from, into a ProductDTO.
     *
     * @param product The Product entity to be mapped.
     * @return A new ProductDTO object populated with data from the given Product.
     */
    public static ProductDTO map(Product product) {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setName(product.getName());
        productDTO.setDescription(product.getDescription());
        productDTO.setPrice(product.getPrice());
        productDTO.setCountryCode(product.getCountryCode().name());
        productDTO.setCurrency(product.getCurrency().name());
        productDTO.setPort(product.getPort());
        return productDTO;
    }
}
