package com.MicroserviciosSpringBoot2025.Item.client;

import com.MicroserviciosSpringBoot2025.Item.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Optional;

@Service
public class WebClientService {

    @Autowired
    private WebClient webClient;

    public List<Product> findAll() {
        return webClient
                .get()
                .retrieve()
                .bodyToFlux(Product.class)
                .collectList()
                .block();
    }

    public Optional<Product> getProduct(Long id) {
        return webClient
                .get()
                .uri("/{id}", id)
                .retrieve()
                .bodyToMono(Product.class)
                .blockOptional();
    }
}
