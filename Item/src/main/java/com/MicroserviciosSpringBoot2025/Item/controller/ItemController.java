package com.MicroserviciosSpringBoot2025.Item.controller;

import com.MicroserviciosSpringBoot2025.Item.entity.Item;
import com.MicroserviciosSpringBoot2025.Item.service.ItemService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/items")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public List<Item> getAllItems() {
        return itemService.findAll();
    }

    @RequestMapping("/{id}/{quantity}")
    public Item getItem(@PathVariable Long id, @PathVariable Integer quantity) {
        return itemService.getItem(id, quantity).orElseThrow();
    }
}
