package com.warehouse.service;

import com.warehouse.exception.BadRequestException;
import com.warehouse.exception.ResourceNotFoundException;
import com.warehouse.model.*;
import com.warehouse.payload.request.DeliveryRequest;
import com.warehouse.payload.request.OrderApprovalRequest;
import com.warehouse.payload.request.OrderRequest;
import com.warehouse.payload.request.OrderUpdateRequest;
import com.warehouse.payload.response.DeliveryResponse;
import com.warehouse.payload.response.OrderItemResponse;
import com.warehouse.payload.response.OrderResponse;
import com.warehouse.payload.response.OrderSummaryResponse;
import com.warehouse.repository.DeliveryRepository;
import com.warehouse.repository.ItemRepository;
import com.warehouse.repository.OrderRepository;
import com.warehouse.repository.UserRepository;
import com.warehouse.security.services.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DeliveryRepository deliveryRepository;

    @Autowired
    private ItemService itemService;  

    public List<OrderResponse> getMyOrders() {
        User currentUser = getCurrentUser();
        return orderRepository.findByClient(currentUser).stream()
                .map(this::convertToOrderResponse)
                .collect(Collectors.toList());
    }

    public List<OrderResponse> getMyOrdersByStatus(OrderStatus status) {
        User currentUser = getCurrentUser();
        return orderRepository.findByClientAndStatus(currentUser, status).stream()
                .map(this::convertToOrderResponse)
                .collect(Collectors.toList());
    }

    public Page<OrderSummaryResponse> getAllOrders(Pageable pageable) {
        return orderRepository.findAllByOrderBySubmittedAtDesc(pageable)
                .map(this::convertToOrderSummaryResponse);
    }

    public Page<OrderSummaryResponse> getAllOrdersByStatus(OrderStatus status, Pageable pageable) {
        return orderRepository.findByStatus(status, pageable)
                .map(this::convertToOrderSummaryResponse);
    }

    public OrderResponse getOrderById(Long id) {
        Order order = getOrderEntityById(id);
        checkOrderAccess(order);
        return convertToOrderResponse(order);
    }

    @Transactional
    public OrderResponse createOrder(OrderRequest orderRequest) {
        User currentUser = getCurrentUser();

        Order order = new Order();
        order.setClient(currentUser);
        order.setStatus(OrderStatus.CREATED);
        order.setCreatedAt(LocalDateTime.now());
        order.setLastUpdatedAt(LocalDateTime.now());

        Order savedOrder = orderRepository.save(order);

        if (orderRequest.getItems() != null && !orderRequest.getItems().isEmpty()) {
            orderRequest.getItems().forEach(itemRequest -> {
                Item item = itemRepository.findById(itemRequest.getItemId())
                        .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + itemRequest.getItemId()));

                if (item.getStockQuantity() < itemRequest.getQuantity()) {
                    throw new BadRequestException("Not enough stock for item: " + item.getName());
                }

                OrderItem orderItem = new OrderItem();
                orderItem.setOrder(savedOrder);
                orderItem.setItem(item);
                orderItem.setQuantity(itemRequest.getQuantity());
                orderItem.setPriceAtOrder(item.getPrice());

                savedOrder.getItems().add(orderItem);
            });
        }

        return convertToOrderResponse(orderRepository.save(savedOrder));
    }

    @Transactional
    public OrderResponse updateOrder(Long id, OrderUpdateRequest orderUpdateRequest) {
        Order order = getOrderEntityById(id);
        checkOrderAccess(order);

        if (order.getStatus() != OrderStatus.CREATED && order.getStatus() != OrderStatus.DECLINED) {
            throw new BadRequestException("Cannot update order with status: " + order.getStatus());
        }

        order.getItems().clear();

        if (orderUpdateRequest.getItems() != null && !orderUpdateRequest.getItems().isEmpty()) {
            orderUpdateRequest.getItems().forEach(itemRequest -> {
                Item item = itemRepository.findById(itemRequest.getItemId())
                        .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + itemRequest.getItemId()));

                if (item.getStockQuantity() < itemRequest.getQuantity()) {
                    throw new BadRequestException("Not enough stock for item: " + item.getName());
                }

                OrderItem orderItem = new OrderItem();
                orderItem.setOrder(order);
                orderItem.setItem(item);
                orderItem.setQuantity(itemRequest.getQuantity());
                orderItem.setPriceAtOrder(item.getPrice());

                order.getItems().add(orderItem);
            });
        }

        order.setLastUpdatedAt(LocalDateTime.now());
        return convertToOrderResponse(orderRepository.save(order));
    }

    @Transactional
    public OrderResponse submitOrder(Long id) {
        Order order = getOrderEntityById(id);
        checkOrderAccess(order);

        if (order.getStatus() != OrderStatus.CREATED && order.getStatus() != OrderStatus.DECLINED) {
            throw new BadRequestException("Cannot submit order with status: " + order.getStatus());
        }

        if (order.getItems().isEmpty()) {
            throw new BadRequestException("Cannot submit an empty order");
        }

        order.setStatus(OrderStatus.AWAITING_APPROVAL);
        order.setSubmittedAt(LocalDateTime.now());
        order.setLastUpdatedAt(LocalDateTime.now());

        return convertToOrderResponse(orderRepository.save(order));
    }

    @Transactional
    public OrderResponse cancelOrder(Long id) {
        Order order = getOrderEntityById(id);
        checkOrderAccess(order);

        if (order.getStatus() == OrderStatus.FULFILLED ||
                order.getStatus() == OrderStatus.UNDER_DELIVERY ||
                order.getStatus() == OrderStatus.CANCELED) {
            throw new BadRequestException("Cannot cancel order with status: " + order.getStatus());
        }

        order.setStatus(OrderStatus.CANCELED);
        order.setLastUpdatedAt(LocalDateTime.now());

        return convertToOrderResponse(orderRepository.save(order));
    }

    @Transactional
    public OrderResponse approveOrDeclineOrder(Long id, OrderApprovalRequest approvalRequest) {
        Order order = getOrderEntityById(id);

        if (order.getStatus() != OrderStatus.AWAITING_APPROVAL) {
            throw new BadRequestException("Can only approve/decline orders with status AWAITING_APPROVAL");
        }

        if (approvalRequest.getApproved()) {
            order.setStatus(OrderStatus.APPROVED);

            // Reduce stock for each item in the order
            for (OrderItem orderItem : order.getItems()) {
                itemService.updateStockAfterOrder(orderItem.getItem().getId(), orderItem.getQuantity());
            }

        } else {
            order.setStatus(OrderStatus.DECLINED);
            order.setDeclineReason(approvalRequest.getDeclineReason());
        }

        order.setLastUpdatedAt(LocalDateTime.now());

        return convertToOrderResponse(orderRepository.save(order));
    }

    @Transactional
    public OrderResponse declineOrder(Long id, @Valid OrderApprovalRequest approvalRequest) {
        Order order = getOrderEntityById(id);

        if (order.getStatus() != OrderStatus.AWAITING_APPROVAL) {
            throw new BadRequestException("Can only decline orders with status AWAITING_APPROVAL");
        }

        order.setStatus(OrderStatus.DECLINED);

        if (approvalRequest.getDeclineReason() != null && !approvalRequest.getDeclineReason().isEmpty()) {
            order.setDeclineReason(approvalRequest.getDeclineReason());
        }

        order.setLastUpdatedAt(LocalDateTime.now());

        return convertToOrderResponse(orderRepository.save(order));
    }

    @Transactional
    public OrderResponse scheduleDelivery(Long id, DeliveryRequest deliveryRequest) {
        Order order = getOrderEntityById(id);

        if (order.getStatus() != OrderStatus.APPROVED) {
            throw new BadRequestException("Can only schedule delivery for APPROVED orders");
        }

        Delivery delivery = new Delivery();
        delivery.setOrder(order);
        delivery.setScheduledDate(deliveryRequest.getDeliveryDate());
        delivery.setDeliveryAddress(deliveryRequest.getDeliveryAddress());
        delivery.setDeliveryNotes(deliveryRequest.getDeliveryNotes());

        order.setDelivery(delivery);
        order.setStatus(OrderStatus.UNDER_DELIVERY);
        order.setLastUpdatedAt(LocalDateTime.now());

        deliveryRepository.save(delivery);
        return convertToOrderResponse(orderRepository.save(order));
    }

    private Order getOrderEntityById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
    }

    private void checkOrderAccess(Order order) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_SYSTEM_ADMIN") || a.getAuthority().equals("ROLE_WAREHOUSE_MANAGER"));

        if (!isAdmin && !order.getClient().getId().equals(userDetails.getId())) {
            throw new BadRequestException("You don't have access to this order");
        }
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private OrderResponse convertToOrderResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setClientId(order.getClient().getId());
        response.setClientName(order.getClient().getFirstName() + " " + order.getClient().getLastName());
        response.setStatus(order.getStatus());
        response.setCreatedAt(order.getCreatedAt());
        response.setSubmittedAt(order.getSubmittedAt());
        response.setLastUpdatedAt(order.getLastUpdatedAt());
        response.setDeclineReason(order.getDeclineReason());

        List<OrderItemResponse> itemResponses = new ArrayList<>();
        double totalAmount = 0.0;

        for (OrderItem item : order.getItems()) {
            OrderItemResponse itemResponse = new OrderItemResponse();
            itemResponse.setId(item.getId());
            itemResponse.setItemId(item.getItem().getId());
            itemResponse.setItemName(item.getItem().getName());
            itemResponse.setQuantity(item.getQuantity());
            itemResponse.setPriceAtOrder(item.getPriceAtOrder());
            double totalPrice = item.getQuantity() * item.getPriceAtOrder();
            itemResponse.setTotalPrice(totalPrice);
            totalAmount += totalPrice;
            itemResponses.add(itemResponse);
        }

        response.setItems(itemResponses);
        response.setTotalAmount(totalAmount);

        if (order.getDelivery() != null) {
            DeliveryResponse deliveryResponse = new DeliveryResponse();
            deliveryResponse.setId(order.getDelivery().getId());
            deliveryResponse.setDeliveryDate(order.getDelivery().getScheduledDate());
            deliveryResponse.setDeliveryAddress(order.getDelivery().getDeliveryAddress());
            deliveryResponse.setDeliveryNotes(order.getDelivery().getDeliveryNotes());
            response.setDelivery(deliveryResponse);
        }

        return response;
    }

    private OrderSummaryResponse convertToOrderSummaryResponse(Order order) {
        OrderSummaryResponse response = new OrderSummaryResponse();
        response.setId(order.getId());
        response.setClientName(order.getClient().getFirstName() + " " + order.getClient().getLastName());
        response.setStatus(order.getStatus());
        response.setSubmittedAt(order.getSubmittedAt());
        response.setItemCount(order.getItems().size());

        double totalAmount = order.getItems().stream()
                .mapToDouble(item -> item.getQuantity() * item.getPriceAtOrder())
                .sum();

        response.setTotalAmount(totalAmount);
        return response;
    }
}
