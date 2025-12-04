package com.MicroserviciosSpringBoot2025.Item.service;

import com.MicroserviciosSpringBoot2025.Item.client.WebClientService;
import com.MicroserviciosSpringBoot2025.Item.entity.Item;
import com.MicroserviciosSpringBoot2025.Item.entity.Product;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class ItemService {

    private final Random random = new Random();
    private final WebClientService webClientService;

    public ItemService(WebClientService webClientService) {
        this.webClientService = webClientService;
    }

    public List<Item> findAll() {
        return webClientService.findAll().stream()
                .map(product -> {
                    return new Item(product, random.nextInt(10 + 2));
                }).toList();
    }

    public Optional<Item> getItem(Long id, Integer quantity) {
        Optional<Product> productOptional = webClientService.getProduct(id);
        return productOptional.map(product -> new Item(product, quantity));
    }

}
