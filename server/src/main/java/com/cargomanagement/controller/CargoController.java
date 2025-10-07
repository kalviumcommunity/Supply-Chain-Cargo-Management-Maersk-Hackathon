package com.cargomanagement.controller;

import com.cargomanagement.models.Cargo;
import com.cargomanagement.repository.CargoRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/cargo")
@CrossOrigin(origins = "*")
public class CargoController {

    @Autowired
    private CargoRepository cargoRepository;

    // GET all cargo
    @GetMapping
    public ResponseEntity<List<Cargo>> getAllCargo() {
        try {
            List<Cargo> cargoList = cargoRepository.findAll();
            if (cargoList.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(cargoList);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET cargo by ID
    @GetMapping("/{id}")
    public ResponseEntity<Cargo> getCargoById(@PathVariable Long id) {
        try {
            Optional<Cargo> cargo = cargoRepository.findById(id);
            if (cargo.isPresent()) {
                return ResponseEntity.ok(cargo.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // POST create new cargo
    @PostMapping
    public ResponseEntity<Cargo> createCargo(@Valid @RequestBody Cargo cargo) {
        try {
            Cargo savedCargo = cargoRepository.save(cargo);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedCargo);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // PUT update cargo
    @PutMapping("/{id}")
    public ResponseEntity<Cargo> updateCargo(@PathVariable Long id, @Valid @RequestBody Cargo cargoDetails) {
        try {
            Optional<Cargo> optionalCargo = cargoRepository.findById(id);
            if (optionalCargo.isPresent()) {
                Cargo cargo = optionalCargo.get();
                
                // Update fields
                cargo.setShipment(cargoDetails.getShipment());
                cargo.setType(cargoDetails.getType());
                cargo.setValue(cargoDetails.getValue());
                cargo.setDescription(cargoDetails.getDescription());
                cargo.setWeight(cargoDetails.getWeight());
                cargo.setVolume(cargoDetails.getVolume());
                cargo.setWeightUnit(cargoDetails.getWeightUnit());
                
                Cargo updatedCargo = cargoRepository.save(cargo);
                return ResponseEntity.ok(updatedCargo);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // DELETE cargo
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCargo(@PathVariable Long id) {
        try {
            if (cargoRepository.existsById(id)) {
                cargoRepository.deleteById(id);
                return ResponseEntity.ok("Cargo deleted successfully");
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting cargo: " + e.getMessage());
        }
    }

    // GET cargo by shipment ID
    @GetMapping("/shipment/{shipmentId}")
    public ResponseEntity<List<Cargo>> getCargoByShipmentId(@PathVariable Long shipmentId) {
        try {
            List<Cargo> cargoList = cargoRepository.findByShipmentShipmentId(shipmentId);
            if (cargoList.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(cargoList);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET cargo by type
    @GetMapping("/type/{type}")
    public ResponseEntity<List<Cargo>> getCargoByType(@PathVariable String type) {
        try {
            List<Cargo> cargoList = cargoRepository.findByType(type);
            if (cargoList.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(cargoList);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}