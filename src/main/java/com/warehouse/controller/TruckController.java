package com.warehouse.controller;

import com.warehouse.model.Truck;
import com.warehouse.repository.TruckRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trucks")
public class TruckController {

    @Autowired
    private TruckRepository truckRepository;

    @PostMapping
    @PreAuthorize("hasRole('WAREHOUSE_MANAGER')")
    public ResponseEntity<Truck> createTruck(@RequestBody Truck truck) {
        if (truckRepository.existsByChassisNumber(truck.getChassisNumber())) {
            return ResponseEntity.badRequest().build(); // Or throw BadRequestException
        }

        Truck savedTruck = truckRepository.save(truck);
        return ResponseEntity.ok(savedTruck);
    }

    @GetMapping
    @PreAuthorize("hasRole('WAREHOUSE_MANAGER')")
    public List<Truck> getAllTrucks() {
        return truckRepository.findAll();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('WAREHOUSE_MANAGER')")
    public ResponseEntity<String> deleteTruck(@PathVariable Long id) {
        if (!truckRepository.existsById(id)) {
            return ResponseEntity.status(404).body("Truck with ID " + id + " not found.");
        }

        truckRepository.deleteById(id);
        return ResponseEntity.ok("Truck with ID " + id + " was successfully deleted.");
    }
}
