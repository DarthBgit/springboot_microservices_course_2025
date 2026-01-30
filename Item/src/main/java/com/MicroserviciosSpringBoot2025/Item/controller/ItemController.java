package com.MicroserviciosSpringBoot2025.Item.controller;

import com.MicroserviciosSpringBoot2025.Item.entity.ItemDTO;
import com.MicroserviciosSpringBoot2025.Item.service.ItemServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/items")
public class ItemController {

    private final ItemServiceImpl itemServiceImpl;

    public ItemController(ItemServiceImpl itemServiceImpl) {
        this.itemServiceImpl = itemServiceImpl;
    }

    /**
     * Endpoint to retrieve all Item objects.
     * It returns a Flux<Item>, allowing Spring WebFlux to stream the results
     * non-blockingly as they arrive from the ItemService.
     * * @return Flux<Item> - The reactive stream of Item objects.
     */
    @GetMapping
    public Flux<ItemDTO> getAllItems() {
        // We now return the Flux directly from the service layer.
        // WebFlux handles the subscription and streaming of the response.
        return itemServiceImpl.findAll();
    }

    /**
     * Endpoint to retrieve a single Item by ID and quantity.
     * It returns a Mono<Item>, allowing Spring WebFlux to handle the asynchronous
     * result when it becomes available.
     * * @param id The ID of the product.
     * @param quantity The quantity for the item.
     * @return Mono<Item> - The reactive publisher for a single Item object.
     */
    @GetMapping("/{id}/{quantity}")
    public Mono<ResponseEntity<ItemDTO>> getItem(@PathVariable Long id, @PathVariable Integer quantity) {
        return itemServiceImpl.getItem(id, quantity)
                .map(ResponseEntity::ok) // If an item is found, wrap it in a 200 OK response
                .defaultIfEmpty(ResponseEntity.notFound().build()); // If the Mono is empty (not found), create a 404 response
    }
}
