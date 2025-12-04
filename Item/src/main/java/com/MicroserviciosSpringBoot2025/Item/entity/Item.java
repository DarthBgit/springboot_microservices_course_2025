package com.MicroserviciosSpringBoot2025.Item.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Item {
    private Product product;
    private Integer quantity;

    public Double getTotalPrice() {
        return product.getPrice() * quantity;
    }
}
