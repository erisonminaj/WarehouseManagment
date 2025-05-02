package com.warehouse.controller;

import com.warehouse.model.Delivery;
import com.warehouse.model.Order;
import com.warehouse.model.Truck;
import com.warehouse.payload.request.DeliveryRequest;
import com.warehouse.service.DeliveryService;
import com.warehouse.repository.TruckRepository;
import com.warehouse.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/deliveries")
public class DeliveryController {

    @Autowired
    private DeliveryService deliveryService;

    @Autowired
    private TruckRepository truckRepository;

    @Autowired
    private OrderRepository orderRepository;

    @PostMapping("/schedule")
    public ResponseEntity<String> scheduleDelivery(@RequestBody DeliveryRequest deliveryRequest) {
        List<Truck> trucks = truckRepository.findAllByIdIn(deliveryRequest.getTruckIds());

        Order order = orderRepository.findById(deliveryRequest.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        try {
            Delivery scheduledDelivery = deliveryService.scheduleDelivery(order, deliveryRequest.getDeliveryDate(), trucks);
            return ResponseEntity.ok("✅ Delivery scheduled successfully for order ID: " + scheduledDelivery.getOrder().getId());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body("❌ Error: " + ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(500).body("❌ An unexpected error occurred.");
        }
    }
}
