package com.MicroserviciosSpringBoot2025.Item.entity;

import lombok.*;

@Data
public class Product {
    private Long id;
    private String name;
    private String description;
    private Double price;
    private String countryCode;
    private String currency;
    private Integer port;
}
