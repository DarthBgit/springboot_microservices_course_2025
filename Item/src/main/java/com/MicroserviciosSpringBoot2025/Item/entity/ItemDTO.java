package com.MicroserviciosSpringBoot2025.Item.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemDTO {
    private ProductDTO product;
    private Integer quantity;
    private Double totalPrice;
    private String locationSummary;
    private String taxMessage;

    public ItemDTO(ProductDTO product, Integer quantity) {
        this.product = product;
        this.quantity = quantity;
    }
}
