package com.MicroserviciosSpringBoot2025.Item.client;

import com.MicroserviciosSpringBoot2025.Item.entity.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class WebClientService {

    private static final Logger log = LoggerFactory.getLogger(WebClientService.class);

    @Autowired
    private WebClient webClient;

    public Flux<Product> findAll() {
        return webClient
                .get()
                .retrieve()
                .bodyToFlux(Product.class);
    }

    public Mono<Product> getProduct(Long id) {
        log.info("Attempting to retrieve product with id: {}", id);
        return webClient
                .get()
                .uri("/{id}", id)
                .retrieve()
                .onStatus(status -> status.isSameCodeAs(HttpStatus.NOT_FOUND),
                          clientResponse -> {
                              log.warn("Product not found (404) for id: {}", id);
                              return Mono.empty();
                          })
                .bodyToMono(Product.class)
                .log();
    }
}
