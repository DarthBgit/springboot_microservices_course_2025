package com.MicroserviciosSpringBoot2025.Item.service;

import com.MicroserviciosSpringBoot2025.Item.entity.ItemDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ItemService {
    Flux<ItemDTO> findAll();
    Mono<ItemDTO> getItem(Long id, Integer quantity);
    Flux<ItemDTO> findByCountry(String countryCode);
}
