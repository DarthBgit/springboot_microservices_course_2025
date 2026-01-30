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
    private String locationSummary;
    private String name;
    private Double originalPrice;
    private String originalCurrency;
    private Double exchangeRate;
    private Double priceInEur;
    private String country;
    private Integer port;
}
