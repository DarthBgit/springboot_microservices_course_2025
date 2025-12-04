package com.MicroserviciosSpringBoot2025.Item.client;

import com.MicroserviciosSpringBoot2025.Item.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class WebClientService {

    @Autowired
    private WebClient webClient;

    /**
     * Retrieves all products asynchronously and non-blockingly.
     * Returns a Flux<Product> that will emit elements as they arrive.
     */
    public Flux<Product> findAll() {
        return webClient
                .get()
                // The target URL will be: http://product-service:8080/api/v1/products
                .retrieve()
                .bodyToFlux(Product.class); // Returns the reactive stream (Flux) directly
    }

    /**
     * Retrieves a single product by ID asynchronously and non-blockingly.
     * Returns a Mono<Product> that will emit 0 or 1 element.
     */
    public Mono<Product> getProduct(Long id) {
        return webClient
                .get()
                // Appends the ID to the base URI
                .uri("/{id}", id)
                .retrieve()
                // Error handling (4xx or 5xx) is optional but recommended:
                // .onStatus(httpStatus -> httpStatus.is4xxClientError(),
                //           clientResponse -> Mono.error(new RuntimeException("Product not found")))
                .bodyToMono(Product.class); // Returns the reactive publisher (Mono) directly
    }
}
