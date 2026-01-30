package com.MicroserviciosSpringBoot2025.Item.service;

import com.MicroserviciosSpringBoot2025.Item.client.WebClientService;
import com.MicroserviciosSpringBoot2025.Item.entity.InstanceStatusDTO;
import com.MicroserviciosSpringBoot2025.Item.entity.ItemDTO;
import com.MicroserviciosSpringBoot2025.Item.entity.Product;
import com.MicroserviciosSpringBoot2025.Item.mapper.ProductToItemDTO;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.List;
import java.util.Random;

@Service
public class ItemServiceImpl implements ItemService {

    private final Random random = new Random();

    // Dependency injection of WebClientService (HTTP client to make requests to product service)
    private final WebClientService webClientService;
    // Dependency injection of DiscoveryClient to get info about red (Eureka client)
    private final DiscoveryClient discoveryClient;

    public ItemServiceImpl(WebClientService webClientService, DiscoveryClient discoveryClient) {
        this.webClientService = webClientService;
        this.discoveryClient = discoveryClient;
    }

    @Override
    public Flux<ItemDTO> findAll() {
        return webClientService.findAll()
                .map(product -> createItemWithLogic(product, random.nextInt(10) + 1));
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
    public Flux<InstanceStatusDTO> getGlobalInstancesStatus() {
        List<ServiceInstance> instances = discoveryClient.getInstances("product-service");
        return webClientService.getStatusInstances(instances);
    }

    private ItemDTO createItemWithLogic(Product product, Integer quantity) {
        String lang = getMessageByCountry(product.getCountryCode());
        Double tax = getTaxByCountry(product.getCountryCode());
        return ProductToItemDTO.map(product, quantity, tax, lang);
    }

    private Double getTaxByCountry(String countryCode) {
        return switch (countryCode.toUpperCase()) {
            case "ES" -> 1.21;
            case "UK" -> 1.20;
            case "US" -> 1.07;
            default   -> 1.0;
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
