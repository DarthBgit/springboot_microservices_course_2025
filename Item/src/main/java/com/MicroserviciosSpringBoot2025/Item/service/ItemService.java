package com.MicroserviciosSpringBoot2025.Item.service;

import com.MicroserviciosSpringBoot2025.Item.client.WebClientService;
import com.MicroserviciosSpringBoot2025.Item.entity.Item;
import com.MicroserviciosSpringBoot2025.Item.entity.Product;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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

    public Flux<Item> findAll() {
        return webClientService.findAll()
                .map(product -> new Item(product, random.nextInt(10 + 2)));

    }

    public Mono<Item> getItem(Long id, Integer quantity) {
        return webClientService.getProduct(id).map(product -> new Item(product, quantity));
    }

}
