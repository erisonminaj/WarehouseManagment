package com.warehouse.repository;

import com.warehouse.model.Delivery;
import com.warehouse.model.Truck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
    List<Delivery> findByScheduledDate(LocalDate date);

    boolean existsByTrucksAndScheduledDate(Truck truck, LocalDate date);
}
