package com.cargomanagement.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "vendor")
public class Vendor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long vendorId;

    @NotBlank(message = "Name is required and cannot be empty")
    @Size(min = 1, max = 100, message = "Name must be between 1-100 characters")
    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;

    @NotBlank(message = "Contact info is required")
    @Size(max = 200, message = "Contact info cannot exceed 200 characters")
    @Pattern(regexp = "^[\\w\\-\\s@.+]+$", message = "Contact info contains invalid characters")  // Basic validation
    @Column(name = "contact_info", nullable = false, length = 200)
    private String contactInfo;

    @NotBlank(message = "Service type is required") 
    @Pattern(regexp = "Logistics|Shipping Line", message = "Service type must be Logistics or Shipping Line")  // Enforces valid options
    @Column(name = "service_type", nullable = false, length = 50)
    private String serviceType;  // e.g., "Logistics", "Shipping Line"

    @NotNull(message = "Is active is required")
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;  // Java default for BOOLEAN TRUE

    @CreationTimestamp  // Auto-sets on persist
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}