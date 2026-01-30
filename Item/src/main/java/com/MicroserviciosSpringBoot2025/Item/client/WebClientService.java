package com.MicroserviciosSpringBoot2025.Item.client;

import com.MicroserviciosSpringBoot2025.Item.entity.ProductDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class WebClientService {

    private static final Logger log = LoggerFactory.getLogger(WebClientService.class);

    private final WebClient webClient;

    public WebClientService(WebClient webClient) {
        this.webClient = webClient;
    }

    public Flux<ProductDTO> findAll() {
        return webClient
                .get()
                .retrieve()
                .bodyToFlux(ProductDTO.class);
    }

    public Flux<ProductDTO> findByCountry(String countryCode) {
        log.info("Fetching products for country: {}", countryCode);
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/country")
                        .queryParam("countryCode", countryCode)
                        .build())
                .retrieve()
                .bodyToFlux(ProductDTO.class);
    }

    public Mono<ProductDTO> getProduct(Long id) {
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
                .bodyToMono(ProductDTO.class)
                .log();
    }
}
