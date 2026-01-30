package com.MicroserviciosSpringBoot2025.Item.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InstanceStatusDTO {
    private String countryCode;
    private String countryName;
    private String url;
    private String status;
    private Integer port;
}
