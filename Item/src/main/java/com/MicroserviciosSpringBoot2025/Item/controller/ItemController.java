package com.MicroserviciosSpringBoot2025.Item.controller;

import com.MicroserviciosSpringBoot2025.Item.entity.Item;
import com.MicroserviciosSpringBoot2025.Item.service.ItemService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/items")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    /**
     * Endpoint to retrieve all Item objects.
     * It returns a Flux<Item>, allowing Spring WebFlux to stream the results
     * non-blockingly as they arrive from the ItemService.
     * * @return Flux<Item> - The reactive stream of Item objects.
     */
    @GetMapping
    public Flux<Item> getAllItems() {
        // We now return the Flux directly from the service layer.
        // WebFlux handles the subscription and streaming of the response.
        return itemService.findAll();
    }

    /**
     * Endpoint to retrieve a single Item by ID and quantity.
     * It returns a Mono<Item>, allowing Spring WebFlux to handle the asynchronous
     * result when it becomes available.
     * * @param id The ID of the product.
     * @param quantity The quantity for the item.
     * @return Mono<Item> - The reactive publisher for a single Item object.
     */
    @GetMapping("/{id}/{quantity}") // Changed @RequestMapping to @GetMapping for clarity
    public Mono<Item> getItem(@PathVariable Long id, @PathVariable Integer quantity) {
        // We return the Mono directly from the service layer.
        // WebFlux will handle the final result when the Mono completes.
        return itemService.getItem(id, quantity);

        // NOTE: In a real application, you might add error handling here,
        // e.g., .switchIfEmpty(Mono.error(new ItemNotFoundException(id)))
    }
}
