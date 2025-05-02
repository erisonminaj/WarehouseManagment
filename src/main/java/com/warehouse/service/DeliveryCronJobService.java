package com.warehouse.service;

import com.warehouse.model.Delivery;
import com.warehouse.repository.DeliveryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class DeliveryCronJobService {

    @Autowired
    private DeliveryRepository deliveryRepository;

    @Scheduled(cron = "0 0 0 * * ?")
    public void checkAndFulfillDeliveries() {
        LocalDate today = LocalDate.now();

        List<Delivery> deliveries = deliveryRepository.findByScheduledDate(today);

        for (Delivery delivery : deliveries) {
            if (delivery.getScheduledDate().equals(today)) {
                delivery.setStatus("FULFILLED");
                deliveryRepository.save(delivery);
            }
        }
    }
}
