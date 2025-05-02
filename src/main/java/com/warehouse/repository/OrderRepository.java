package com.warehouse.repository;

import com.warehouse.model.Order;
import com.warehouse.model.OrderStatus;
import com.warehouse.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByClientAndStatus(User client, OrderStatus status);
    List<Order> findByClient(User client);
    Page<Order> findByStatus(OrderStatus status, Pageable pageable);
    Page<Order> findAllByOrderBySubmittedAtDesc(Pageable pageable);
}