package com.cargomanagement.controller;

import com.cargomanagement.models.Shipment;
import com.cargomanagement.repository.ShipmentRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/shipments")
@CrossOrigin(origins = "*")
public class ShipmentController {

    @Autowired
    private ShipmentRepository shipmentRepository;

    // GET all shipments
    @GetMapping
    public ResponseEntity<List<Shipment>> getAllShipments() {
        try {
            List<Shipment> shipments = shipmentRepository.findAll();
            if (shipments.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(shipments);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET shipment by ID
    @GetMapping("/{id}")
    public ResponseEntity<Shipment> getShipmentById(@PathVariable Long id) {
        try {
            Optional<Shipment> shipment = shipmentRepository.findById(id);
            if (shipment.isPresent()) {
                return ResponseEntity.ok(shipment.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // POST create new shipment
    @PostMapping
    public ResponseEntity<Shipment> createShipment(@Valid @RequestBody Shipment shipment) {
        try {
            Shipment savedShipment = shipmentRepository.save(shipment);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedShipment);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // PUT update shipment
    @PutMapping("/{id}")
    public ResponseEntity<Shipment> updateShipment(@PathVariable Long id, @Valid @RequestBody Shipment shipmentDetails) {
        try {
            Optional<Shipment> optionalShipment = shipmentRepository.findById(id);
            if (optionalShipment.isPresent()) {
                Shipment shipment = optionalShipment.get();
                
                // Update fields
                shipment.setOrigin(shipmentDetails.getOrigin());
                shipment.setDestination(shipmentDetails.getDestination());
                shipment.setStatus(shipmentDetails.getStatus());
                shipment.setEstimatedDelivery(shipmentDetails.getEstimatedDelivery());
                shipment.setAssignedRoute(shipmentDetails.getAssignedRoute());
                shipment.setAssignedVendor(shipmentDetails.getAssignedVendor());
                shipment.setShipmentCode(shipmentDetails.getShipmentCode());
                
                Shipment updatedShipment = shipmentRepository.save(shipment);
                return ResponseEntity.ok(updatedShipment);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // DELETE shipment
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteShipment(@PathVariable Long id) {
        try {
            if (shipmentRepository.existsById(id)) {
                shipmentRepository.deleteById(id);
                return ResponseEntity.ok("Shipment deleted successfully");
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting shipment: " + e.getMessage());
        }
    }

    // GET shipments by status
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Shipment>> getShipmentsByStatus(@PathVariable String status) {
        try {
            List<Shipment> shipments = shipmentRepository.findByStatus(status);
            if (shipments.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(shipments);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}