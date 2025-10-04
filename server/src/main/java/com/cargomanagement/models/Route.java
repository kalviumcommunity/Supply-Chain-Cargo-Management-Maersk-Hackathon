package com.cargomanagement.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "route",
       uniqueConstraints = @UniqueConstraint(columnNames = {"origin_port", "destination_port"}))  // UNIQUE constraint via annotation
public class Route {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long routeId;

    @NotBlank(message = "Origin port is required")
    @Size(max = 100, message = "Origin port cannot exceed 100 characters")
    @Column(name = "origin_port", nullable = false, length = 100)
    private String originPort;

    @NotBlank(message = "Destination port is required")
    @Size(max = 100, message = "Destination port cannot exceed 100 characters")
    @Column(name = "destination_port", nullable = false, length = 100)
    private String destinationPort;

    @NotNull(message = "Duration is required")
    @Min(value = 1, message = "Duration must be positive (greater than 0)")
    @Column(name = "duration", nullable = false)
    private Integer duration;  // CHECK duration >0 via @Min

    @NotBlank(message = "Status is required")
    @Pattern(regexp = "Active|Delayed|Closed", message = "Status must be Active, Delayed, or Closed")
    @Column(name = "status", nullable = false, length = 20)  // Default 'Active' via field init
    private String status = "Active";  // Java default for insert

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}