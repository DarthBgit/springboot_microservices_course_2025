package com.MicroserviciosSpringBoot2025.Item.mapper;

import com.MicroserviciosSpringBoot2025.Item.entity.ItemDTO;
import com.MicroserviciosSpringBoot2025.Item.entity.Product;

public class ProductToItemDTO {

    public static ItemDTO map(Product product, Integer quantity, Double tax, String locationSummary) {
        ItemDTO item = new ItemDTO();
        item.setName(product.getName());
        item.setOriginalPrice(product.getPrice());
        item.setOriginalCurrency(product.getCurrency());
        item.setExchangeRate(tax);
        item.setPriceInEur(product.getPrice() * quantity * tax);
        item.setCountry(product.getCountryCode());
        item.setPort(product.getPort());
        item.setLocationSummary(locationSummary + " " + product.getDescription());
        return item;
    }
}
