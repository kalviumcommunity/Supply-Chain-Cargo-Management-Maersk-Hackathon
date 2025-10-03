package com.cargomanagement.models;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Data
@Table(name = "deliveries")
public class Delivery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long deliveryId;

    @OneToOne
    @JoinColumn(name = "shipment_id", referencedColumnName = "shipmentId")
    private Shipment shipment;

    private LocalDate actualDeliveryDate;
    private String recipient;
}
