package com.warehouse.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Truck {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String chassisNumber;
    private String licensePlate;
    private boolean available;
    private LocalDate availableFrom;
    private Double containerVolume;

    @ManyToOne
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getChassisNumber() { return chassisNumber; }
    public void setChassisNumber(String chassisNumber) { this.chassisNumber = chassisNumber; }

    public String getLicensePlate() { return licensePlate; }
    public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    public LocalDate getAvailableFrom() { return availableFrom; }
    public void setAvailableFrom(LocalDate availableFrom) { this.availableFrom = availableFrom; }

    public Double getContainerVolume() { return containerVolume; }
    public void setContainerVolume(Double containerVolume) { this.containerVolume = containerVolume; }

    public Delivery getDelivery() { return delivery; }
    public void setDelivery(Delivery delivery) { this.delivery = delivery; }
}
