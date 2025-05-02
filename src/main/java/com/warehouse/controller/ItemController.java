package com.warehouse.controller;

import com.warehouse.payload.request.ItemRequest;
import com.warehouse.payload.response.ItemResponse;
import com.warehouse.payload.response.MessageResponse;
import com.warehouse.service.ItemService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/items")
public class ItemController {
    @Autowired
    private ItemService itemService;

    @GetMapping
    public ResponseEntity<List<ItemResponse>> getAllItems() {
        List<ItemResponse> items = itemService.getAllItems();
        return ResponseEntity.ok(items);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemResponse> getItemById(@PathVariable Long id) {
        ItemResponse item = itemService.getItemById(id);
        return ResponseEntity.ok(item);
    }

    @PostMapping
    @PreAuthorize("hasRole('WAREHOUSE_MANAGER') or hasRole('SYSTEM_ADMIN')")
    public ResponseEntity<ItemResponse> createItem(@Valid @RequestBody ItemRequest itemRequest) {
        ItemResponse createdItem = itemService.createItem(itemRequest);
        return ResponseEntity.ok(createdItem);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('WAREHOUSE_MANAGER') or hasRole('SYSTEM_ADMIN')")
    public ResponseEntity<ItemResponse> updateItem(@PathVariable Long id, @Valid @RequestBody ItemRequest itemRequest) {
        ItemResponse updatedItem = itemService.updateItem(id, itemRequest);
        return ResponseEntity.ok(updatedItem);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('WAREHOUSE_MANAGER')")
    public ResponseEntity<MessageResponse> deleteItem(@PathVariable Long id) {
        itemService.deleteItem(id);
        return ResponseEntity.ok(new MessageResponse("Item deleted successfully"));
    }
}