package com.warehouse.service;

import com.warehouse.exception.ResourceNotFoundException;
import com.warehouse.model.Item;
import com.warehouse.payload.request.ItemRequest;
import com.warehouse.payload.response.ItemResponse;
import com.warehouse.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemService {

    @Autowired
    private ItemRepository itemRepository;

    public void updateStockAfterOrder(Long itemId, int quantityOrdered) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + itemId));

        int updatedStock = item.getStockQuantity() - quantityOrdered;

        if (updatedStock < 0) {
            throw new IllegalArgumentException("Not enough stock available for this item.");
        }

        item.setStockQuantity(updatedStock);
        itemRepository.save(item); // Save the updated item
    }

    public List<ItemResponse> getAllItems() {
        return itemRepository.findAll().stream()
                .map(this::convertToItemResponse)
                .collect(Collectors.toList());
    }

    public ItemResponse getItemById(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + id));
        return convertToItemResponse(item);
    }

    public ItemResponse createItem(ItemRequest itemRequest) {
        Item item = new Item();
        updateItemFromRequest(item, itemRequest);
        Item savedItem = itemRepository.save(item);
        return convertToItemResponse(savedItem);
    }

    public ItemResponse updateItem(Long id, ItemRequest itemRequest) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + id));
        updateItemFromRequest(item, itemRequest);
        Item updatedItem = itemRepository.save(item);
        return convertToItemResponse(updatedItem);
    }

    public void deleteItem(Long id) {
        if (!itemRepository.existsById(id)) {
            throw new ResourceNotFoundException("Item not found with id: " + id);
        }
        itemRepository.deleteById(id);
    }

    private void updateItemFromRequest(Item item, ItemRequest itemRequest) {
        item.setName(itemRequest.getName());
        item.setDescription(itemRequest.getDescription());
        item.setPrice(itemRequest.getPrice());

        Integer quantity = itemRequest.getStockQuantity();
        item.setStockQuantity(quantity != null ? quantity : 0);
    }

    private ItemResponse convertToItemResponse(Item item) {
        ItemResponse response = new ItemResponse();
        response.setId(item.getId());
        response.setName(item.getName());
        response.setDescription(item.getDescription());
        response.setPrice(item.getPrice());

        response.setStockQuantity(item.getStockQuantity() != null ? item.getStockQuantity() : 0);

        return response;
    }

}
