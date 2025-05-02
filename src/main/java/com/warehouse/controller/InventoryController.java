package com.warehouse.controller;

import com.warehouse.payload.request.InventoryRequest;
import com.warehouse.payload.response.InventoryResponse;
import com.warehouse.service.InventoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@CrossOrigin(origins = "*", maxAge = 3600)
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    @GetMapping("/items")
    public ResponseEntity<List<InventoryResponse>> getItems() {
        List<InventoryResponse> items = inventoryService.getItems();
        return ResponseEntity.ok(items);
    }

    @PostMapping("/items")
    public ResponseEntity<InventoryResponse> createItem(@Valid @RequestBody InventoryRequest inventoryRequest) {
        InventoryResponse newItem = inventoryService.createItem(inventoryRequest);
        return ResponseEntity.ok(newItem);
    }
}
