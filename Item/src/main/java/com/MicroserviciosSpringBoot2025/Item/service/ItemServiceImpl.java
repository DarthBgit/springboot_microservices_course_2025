package com.MicroserviciosSpringBoot2025.Item.service;

import com.MicroserviciosSpringBoot2025.Item.client.WebClientService;
import com.MicroserviciosSpringBoot2025.Item.entity.ItemDTO;
import com.MicroserviciosSpringBoot2025.Item.entity.ProductDTO;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.Random;

@Service
public class ItemServiceImpl implements ItemService {

    private final Random random = new Random();
    private final WebClientService webClientService;

    public ItemServiceImpl(WebClientService webClientService) {
        this.webClientService = webClientService;
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

    private ItemDTO createItemWithLogic(ProductDTO product, Integer quantity) {
        ItemDTO item = new ItemDTO(product, quantity);

        // Apply VAT based on the country provided in the Product DTO
        double tax = getTaxByCountry(product.getCountryCode());
        item.setTotalPrice(product.getPrice() * quantity * tax);

        // A personalized message so the customer knows where it comes from
        String lang = getMessageByCountry(product.getCountryCode());
        item.setLocationSummary(lang + " " + product.getDescription());

        return item;
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
