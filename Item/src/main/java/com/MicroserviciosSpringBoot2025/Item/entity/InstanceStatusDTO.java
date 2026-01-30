package com.MicroserviciosSpringBoot2025.Item.entity;

public class InstanceStatusDTO {
    private String country;
    private String url;
    private String status;
    private Integer port;

    public InstanceStatusDTO(String country, String url, String status, Integer port) {
        this.country = country;
        this.url = url;
        this.status = status;
        this.port = port;
    }
}
