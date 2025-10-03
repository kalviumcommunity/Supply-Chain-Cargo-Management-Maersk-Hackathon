package com.cargomanagement.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Entity
@Data
@Table(name = "cargo")
public class Cargo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cargoId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "shipment_id", nullable = false)
    @JsonBackReference
    private Shipment shipment;

    private String type;
    private String description;
    private BigDecimal value;
    private BigDecimal weight; // Weight in kg or other unit
    private String weightUnit; // Unit of weight (kg, lbs, tons, etc.)
}
