package com.cargomanagement.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name = "shipment")
public class Shipment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long shipmentId;

    @NotBlank(message = "Origin is required")
    @Size(max = 100, message = "Origin cannot exceed 100 characters")
    @Column(name = "origin", nullable = false, length = 100)
    private String origin;

    @NotBlank(message = "Destination is required")
    @Size(max = 100, message = "Destination cannot exceed 100 characters")
    @Column(name = "destination", nullable = false, length = 100)
    private String destination;

    @NotBlank(message = "Status is required")
    @Pattern(regexp = "Created|In Transit|Delivered|Delayed", message = "Status must be Created, In Transit, Delivered, or Delayed")
    @Column(name = "status", nullable = false, length = 20)  // Default 'Created' via field init
    private String status = "Created";  // Java default for insert

    @Column(name = "estimated_delivery", nullable = true)  // TIMESTAMP, nullable allowed
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime estimatedDelivery;  // Use LocalDateTime for TIMESTAMP

    // FK to Route (NOT NULL)
    @NotNull(message = "Route is required")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "route_id", nullable = false)
    private Route assignedRoute;

    // FK to Vendor (NOT NULL)
    @NotNull(message = "Vendor is required")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "vendor_id", nullable = false)
    private Vendor assignedVendor;

    @NotBlank(message = "Shipment code is required and unique")
    @Size(min = 1, max = 20, message = "Shipment code must be 1-20 characters")
    @Column(name = "shipment_code", unique = true, length = 20, nullable = false)  // UNIQUE and NOT NULL
    private String shipmentCode;  // Added from specs

    @OneToMany(mappedBy = "shipment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Cargo> cargoItems;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}