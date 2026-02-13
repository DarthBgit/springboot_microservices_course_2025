package com.MicroserviciosSpringBoot2025.Item.service;

import com.MicroserviciosSpringBoot2025.Item.client.WebClientService;
import com.MicroserviciosSpringBoot2025.Item.entity.InstanceStatusDTO;
import com.MicroserviciosSpringBoot2025.Item.entity.ItemDTO;
import com.MicroserviciosSpringBoot2025.Item.entity.Product;
import com.MicroserviciosSpringBoot2025.Item.mapper.ProductToItemDTO;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
public class ItemServiceImpl implements ItemService {

    private final Random random = new Random();

    // Dependency injection of WebClientService (HTTP client to make requests to product service)
    private final WebClientService webClientService;
    // Dependency injection of DiscoveryClient to get info about red (Eureka client)
    private final DiscoveryClient discoveryClient;
    // Dependency injection of Client rest
    private final RestTemplate restTemplate = new RestTemplate();

    public ItemServiceImpl(WebClientService webClientService, DiscoveryClient discoveryClient) {
        this.webClientService = webClientService;
        this.discoveryClient = discoveryClient;
    }

    @Override
    @Retry(name = "products") // First, it tries 3 times (configurable) before going to the fallback method
    @CircuitBreaker(name = "products", fallbackMethod = "getProductsFallback")
    public Flux<ItemDTO> findAll() {
        return webClientService.findAll()
                .map(product -> createItemWithLogic(product, random.nextInt(10) + 1));
    }

    /**
     * Fallback method for the findAll operation.
     * Returns a Flux containing a default ItemDTO when the product service is unreachable.
     */
    public Flux<ItemDTO> getProductsFallback(Throwable t) {
        ItemDTO fallbackItem = new ItemDTO();

        // Setting default data to inform the user or the next service
        fallbackItem.setName("Product Service Unavailable");
        fallbackItem.setLocationSummary("Information currently not available (Circuit Breaker)");
        fallbackItem.setCountry("N/A");
        fallbackItem.setQuantity(0);
        fallbackItem.setOriginalPrice(0.0);
        fallbackItem.setPriceInEur(0.0);
        fallbackItem.setOriginalCurrency("None");
        fallbackItem.setExchangeRate(1.0);

        return Flux.just(fallbackItem);
    }

    @Override
    public Mono<ItemDTO> getItem(Long id, Integer quantity) {
        return webClientService.getProduct(id)
                .map(product -> createItemWithLogic(product, quantity));
    }

    @Override
    public Flux<ItemDTO> findByCountry(String countryCode) {
        return webClientService.findByCountry(countryCode)
                .map(product -> createItemWithLogic(product, 1));
    }

    /**
     * Get the status of all instances of the product-service registered in Eureka.
     */

    public List<InstanceStatusDTO> getGlobalStatus() {
        // 1. Recuperamos las 4 instancias de Eureka (que ya vimos que funciona)
        List<ServiceInstance> instances = discoveryClient.getInstances("product-service");

        return instances.stream().map(inst -> {
            // 2. Construimos la URL interna (http://172.18.0.x:8080/api/v1/products/status)
            String url = inst.getUri().toString() + "/api/v1/products/status";

            try {
                // 3. Llamada directa a la IP
                Map<String, Object> response = restTemplate.getForObject(url, Map.class);

                return new InstanceStatusDTO(
                        response.get("country_code") != null ? response.get("country_code").toString() : "UNKNOWN",
                        response.get("country_name") != null ? response.get("country_name").toString() : "UNKNOWN",
                        url,
                        response.get("status") != null ? response.get("status").toString() : "DOWN",
                        inst.getPort()
                );
            } catch (Exception e) {
                // Si una instancia falla, que al menos nos diga por qué en el JSON
                return new InstanceStatusDTO("ERROR", "ERROR", url, "DOWN (Connection Refused)", 0);
            }
        }).toList();
    }

    private ItemDTO createItemWithLogic(Product product, Integer quantity) {
        String lang = getMessageByCountry(product.getCountryCode());
        Double tax = getTaxByCountry(product.getCountryCode());
        Double exchangeRate = getExchangeRate(product);
        return ProductToItemDTO.map(product, exchangeRate, tax, quantity, lang);
    }

    private Double getTaxByCountry(String countryCode) {
        return switch (countryCode.toUpperCase()) {
            case "ES" -> 1.21;
            case "UK" -> 1.20;
            case "US" -> 1.07;
            case "CN" -> 1.05;
            default   -> 1.0;
        };
    }

    private Double getExchangeRate(Product product) {
        return switch (product.getCurrency()) {
            case "GBP" -> 1.20;
            case "USD" -> 0.92;
            case "CNY" -> 0.13;
            default    -> 1.0;
        };
    }

    private String getMessageByCountry(String countryCode) {
        return switch (countryCode.toUpperCase()) {
            case "ES" -> "Welcome from Spain!";
            case "US" -> "Welcome from the United States!";
            case "UK" -> "Welcome from the United Kingdom.";
            default   -> "Welcome!";
        };
    }
}
