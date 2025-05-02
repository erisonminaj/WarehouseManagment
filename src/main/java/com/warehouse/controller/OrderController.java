package com.warehouse.controller;

import com.warehouse.model.OrderStatus;
import com.warehouse.payload.request.DeliveryRequest;
import com.warehouse.payload.request.OrderApprovalRequest;
import com.warehouse.payload.request.OrderRequest;
import com.warehouse.payload.request.OrderUpdateRequest;
import com.warehouse.payload.response.MessageResponse;
import com.warehouse.payload.response.OrderResponse;
import com.warehouse.payload.response.OrderSummaryResponse;
import com.warehouse.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @GetMapping
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<List<OrderResponse>> getMyOrders() {
        List<OrderResponse> orders = orderService.getMyOrders();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<List<OrderResponse>> getMyOrdersByStatus(@PathVariable OrderStatus status) {
        List<OrderResponse> orders = orderService.getMyOrdersByStatus(status);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/status")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<Map<OrderStatus, List<OrderResponse>>> getOrdersGroupedByStatus() {
        List<OrderResponse> orders = orderService.getMyOrders();
        Map<OrderStatus, List<OrderResponse>> grouped = orders.stream()
                .collect(Collectors.groupingBy(OrderResponse::getStatus));
        return ResponseEntity.ok(grouped);
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('WAREHOUSE_MANAGER')")
    public ResponseEntity<Page<OrderSummaryResponse>> getAllOrders(Pageable pageable) {
        Page<OrderSummaryResponse> orders = orderService.getAllOrders(pageable);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/all/status/{status}")
    @PreAuthorize("hasRole('WAREHOUSE_MANAGER') or hasRole('SYSTEM_ADMIN')")
    public ResponseEntity<Page<OrderSummaryResponse>> getAllOrdersByStatus(@PathVariable OrderStatus status, Pageable pageable) {
        Page<OrderSummaryResponse> orders = orderService.getAllOrdersByStatus(status, pageable);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id) {
        OrderResponse order = orderService.getOrderById(id);
        return ResponseEntity.ok(order);
    }

    @PostMapping
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderRequest orderRequest) {
        OrderResponse createdOrder = orderService.createOrder(orderRequest);
        return ResponseEntity.ok(createdOrder);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<OrderResponse> updateOrder(@PathVariable Long id, @Valid @RequestBody OrderUpdateRequest orderUpdateRequest) {
        OrderResponse updatedOrder = orderService.updateOrder(id, orderUpdateRequest);
        return ResponseEntity.ok(updatedOrder);
    }

    @PostMapping("/{id}/submit")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<OrderResponse> submitOrder(@PathVariable Long id) {
        OrderResponse submittedOrder = orderService.submitOrder(id);
        return ResponseEntity.ok(submittedOrder);
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<OrderResponse> cancelOrder(@PathVariable Long id) {
        OrderResponse canceledOrder = orderService.cancelOrder(id);
        return ResponseEntity.ok(canceledOrder);
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasRole('WAREHOUSE_MANAGER') or hasRole('SYSTEM_ADMIN')")
    public ResponseEntity<OrderResponse> approveOrDeclineOrder(@PathVariable Long id, @Valid @RequestBody OrderApprovalRequest approvalRequest) {
        OrderResponse processedOrder = orderService.approveOrDeclineOrder(id, approvalRequest);
        return ResponseEntity.ok(processedOrder);
    }

    @PostMapping("/{id}/decline")
    @PreAuthorize("hasRole('ROLE_WAREHOUSE_MANAGER')")
    public ResponseEntity<OrderResponse> declineOrder(@PathVariable Long id, @Valid @RequestBody OrderApprovalRequest approvalRequest) {
        OrderResponse declinedOrder = orderService.declineOrder(id, approvalRequest);
        return ResponseEntity.ok(declinedOrder);
    }


    @PostMapping("/{id}/delivery")
    @PreAuthorize("hasRole('WAREHOUSE_MANAGER') or hasRole('SYSTEM_ADMIN')")
    public ResponseEntity<OrderResponse> scheduleDelivery(@PathVariable Long id, @Valid @RequestBody DeliveryRequest deliveryRequest) {
        OrderResponse orderWithDelivery = orderService.scheduleDelivery(id, deliveryRequest);
        return ResponseEntity.ok(orderWithDelivery);
    }
}
