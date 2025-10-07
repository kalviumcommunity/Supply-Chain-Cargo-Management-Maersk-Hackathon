package com.cargomanagement.controller;

import com.cargomanagement.models.Delivery;
import com.cargomanagement.repository.DeliveryRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/deliveries")
@CrossOrigin(origins = "*")
public class DeliveryController {

    @Autowired
    private DeliveryRepository deliveryRepository;

    // GET all deliveries
    @GetMapping
    public ResponseEntity<List<Delivery>> getAllDeliveries() {
        try {
            List<Delivery> deliveries = deliveryRepository.findAll();
            if (deliveries.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(deliveries);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET delivery by ID
    @GetMapping("/{id}")
    public ResponseEntity<Delivery> getDeliveryById(@PathVariable Long id) {
        try {
            Optional<Delivery> delivery = deliveryRepository.findById(id);
            if (delivery.isPresent()) {
                return ResponseEntity.ok(delivery.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // POST create new delivery
    @PostMapping
    public ResponseEntity<Delivery> createDelivery(@Valid @RequestBody Delivery delivery) {
        try {
            Delivery savedDelivery = deliveryRepository.save(delivery);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedDelivery);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // PUT update delivery
    @PutMapping("/{id}")
    public ResponseEntity<Delivery> updateDelivery(@PathVariable Long id, @Valid @RequestBody Delivery deliveryDetails) {
        try {
            Optional<Delivery> optionalDelivery = deliveryRepository.findById(id);
            if (optionalDelivery.isPresent()) {
                Delivery delivery = optionalDelivery.get();
                
                // Update fields
                delivery.setShipment(deliveryDetails.getShipment());
                delivery.setActualDeliveryDate(deliveryDetails.getActualDeliveryDate());
                delivery.setRecipient(deliveryDetails.getRecipient());
                delivery.setStatus(deliveryDetails.getStatus());
                
                Delivery updatedDelivery = deliveryRepository.save(delivery);
                return ResponseEntity.ok(updatedDelivery);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // DELETE delivery
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDelivery(@PathVariable Long id) {
        try {
            if (deliveryRepository.existsById(id)) {
                deliveryRepository.deleteById(id);
                return ResponseEntity.ok("Delivery deleted successfully");
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting delivery: " + e.getMessage());
        }
    }

    // GET delivery by shipment ID
    @GetMapping("/shipment/{shipmentId}")
    public ResponseEntity<Delivery> getDeliveryByShipmentId(@PathVariable Long shipmentId) {
        try {
            Optional<Delivery> delivery = deliveryRepository.findByShipmentShipmentId(shipmentId);
            if (delivery.isPresent()) {
                return ResponseEntity.ok(delivery.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET deliveries by status
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Delivery>> getDeliveriesByStatus(@PathVariable String status) {
        try {
            List<Delivery> deliveries = deliveryRepository.findByStatus(status);
            if (deliveries.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(deliveries);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}