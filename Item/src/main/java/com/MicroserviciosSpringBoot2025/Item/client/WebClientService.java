package com.MicroserviciosSpringBoot2025.Item.client;

import com.MicroserviciosSpringBoot2025.Item.entity.InstanceStatusDTO;
import com.MicroserviciosSpringBoot2025.Item.entity.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.time.Duration;
import java.util.List;
import java.util.Map;

@Service
public class WebClientService {

    private static final Logger log = LoggerFactory.getLogger(WebClientService.class);

    private final WebClient webClient;

    public WebClientService(WebClient webClient) {
        this.webClient = webClient;
    }

    public Flux<Product> findAll() {
        return webClient
                .get()
                .retrieve()
                .bodyToFlux(Product.class);
    }

    public Flux<Product> findByCountry(String countryCode) {
        log.info("Fetching products for country: {}", countryCode);
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/country")
                        .queryParam("countryCode", countryCode)
                        .build())
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

    public Flux<InstanceStatusDTO> getStatusInstances(List<ServiceInstance> instances) {
        // Convert the list of service instances into a reactive Flux
        return Flux.fromIterable(instances)
                .flatMap(instance -> {
                    // Get the physical URI of the specific container (e.g., http://172.18.0.5:8080)
                    String baseUrl = instance.getUri().toString();

                    // Perform a direct HTTP GET request to the instance's status endpoint
                    return webClient.get()
                            .uri(baseUrl + "/api/v1/products/status")
                            .retrieve()
                            .bodyToMono(Map.class)
                            .map(response -> new InstanceStatusDTO(
                                    response.get("node").toString(),
                                    baseUrl,
                                    response.get("status").toString(),
                                    (Integer) response.get("port")
                            ))
                            // Define a 2-second timeout to avoid hanging on unresponsive nodes
                            .timeout(Duration.ofSeconds(2))
                            // Fallback mechanism: if the node is down or times out, return a custom status
                            .onErrorReturn(new InstanceStatusDTO(
                                    "UNKNOWN",
                                    baseUrl,
                                    "DOWN / UNREACHABLE",
                                    instance.getPort())
                            );
                });
    }
}
