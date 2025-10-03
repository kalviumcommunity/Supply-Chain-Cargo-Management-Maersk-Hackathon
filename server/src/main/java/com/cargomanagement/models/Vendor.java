package com.cargomanagement.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "vendors")
public class Vendor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long vendorId;

    private String name;
    private String contactInfo;
    private String serviceType; // e.g., "Logistics", "Shipping Line"
}