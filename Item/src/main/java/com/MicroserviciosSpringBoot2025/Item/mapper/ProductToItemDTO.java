package com.MicroserviciosSpringBoot2025.Item.mapper;

import com.MicroserviciosSpringBoot2025.Item.entity.ItemDTO;
import com.MicroserviciosSpringBoot2025.Item.entity.Product;

public class ProductToItemDTO {

    public static ItemDTO map(Product product, Double exchangeRate, Double tax, Integer quantity, String locationSummary) {
        ItemDTO item = new ItemDTO();
        item.setName(product.getName());
        item.setOriginalPrice(product.getPrice());
        item.setOriginalCurrency(product.getCurrency());
        item.setExchangeRate(exchangeRate);
        item.setQuantity(quantity);
        item.setPriceInEur(((product.getPrice() * exchangeRate) * tax) * quantity);
        item.setCountry(product.getCountryCode());
        item.setLocationSummary(locationSummary + " " + product.getDescription());
        return item;
    }
}
