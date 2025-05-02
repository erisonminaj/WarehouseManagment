package com.warehouse.service;

import com.warehouse.model.Delivery;
import com.warehouse.model.Order;
import com.warehouse.model.Truck;
import com.warehouse.repository.DeliveryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class DeliveryService {

    @Autowired
    private DeliveryRepository deliveryRepository;

    public Delivery scheduleDelivery(Order order, LocalDate date, List<Truck> trucks) {
        if (date.getDayOfWeek().getValue() >= 6) {
            throw new IllegalArgumentException("Delivery cannot be scheduled on weekends.");
        }

        if (trucks.isEmpty()) {
            throw new IllegalArgumentException("At least one truck must be selected.");
        }

        for (Truck truck : trucks) {
            boolean isBusy = deliveryRepository.existsByTrucksAndScheduledDate(truck, date);
            if (isBusy) {
                throw new IllegalArgumentException("Truck with ID " + truck.getId() + " is already scheduled on " + date);
            }
        }

        Delivery delivery = new Delivery();
        delivery.setOrder(order);
        delivery.setScheduledDate(date);
        delivery.setStatus("UNDER_DELIVERY");
        delivery.setTrucks(trucks);

        return deliveryRepository.save(delivery);
    }
}
