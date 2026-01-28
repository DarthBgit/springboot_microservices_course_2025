package com.MicroserviciosSpringBoot2025.Item.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${product.service.url}")
    private String productServiceUrl;

    // 1. This bean provides the "blueprint" with Load Balancing capabilities
    @Bean
    @LoadBalanced
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    // 2. This bean creates the actual client using that blueprint
    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        return builder
                .baseUrl(productServiceUrl)
                .build();
    }
}
