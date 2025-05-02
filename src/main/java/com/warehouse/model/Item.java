package com.warehouse.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private Double price;

    private Double length;
    private Double width;
    private Double height;

    private Double volume;

    private Integer stockQuantity;

    @PrePersist
    @PreUpdate
    public void calculateVolume() {
        if (length != null && width != null && height != null) {
            this.volume = length * width * height;
        } else {
            this.volume = null; // Or set to 0.0 if you prefer
        }
    }
}
