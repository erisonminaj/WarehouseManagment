package com.warehouse.repository;

import com.warehouse.model.Truck;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TruckRepository extends JpaRepository<Truck, Long> {

    boolean existsByChassisNumber(String chassisNumber);

    List<Truck> findAllByIdIn(List<Long> truckIds);
}
