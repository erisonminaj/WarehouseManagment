package com.warehouse.service;

import com.warehouse.exception.ResourceNotFoundException;
import com.warehouse.model.InventoryItem;
import com.warehouse.model.Item;
import com.warehouse.payload.request.InventoryRequest;
import com.warehouse.payload.response.InventoryResponse;
import com.warehouse.repository.InventoryItemRepository;
import com.warehouse.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class InventoryService {

    @Autowired
    private InventoryItemRepository inventoryItemRepository;

    @Autowired
    private ItemRepository itemRepository;

    public List<InventoryResponse> getItems() {
        List<InventoryItem> items = inventoryItemRepository.findAll();
        return items.stream()
                .map(this::convertToInventoryResponse)
                .collect(Collectors.toList());
    }

    public InventoryResponse createItem(InventoryRequest inventoryRequest) {
        Item item = itemRepository.findById(inventoryRequest.getItemId())
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with ID: " + inventoryRequest.getItemId()));

        InventoryItem inventoryItem = new InventoryItem();
        inventoryItem.setItem(item);
        inventoryItem.setQuantity(inventoryRequest.getStockQuantity());

        InventoryItem savedItem = inventoryItemRepository.save(inventoryItem);
        return convertToInventoryResponse(savedItem);
    }

    private InventoryResponse convertToInventoryResponse(InventoryItem inventoryItem) {
        Item item = inventoryItem.getItem();

        InventoryResponse response = new InventoryResponse();
        response.setInventoryId(inventoryItem.getId());
        response.setItemId(item.getId());
        response.setItemName(item.getName());
        response.setItemDescription(item.getDescription());
        response.setItemPrice(item.getPrice());
        response.setStockQuantity(inventoryItem.getQuantity());

        return response;
    }
}
