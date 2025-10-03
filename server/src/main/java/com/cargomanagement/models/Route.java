package com.cargomanagement.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "routes")
public class Route {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long routeId;

    private String originPort;
    private String destinationPort;
    private Integer duration; // in days
    private String status; // "Active", "Delayed", "Closed"
}