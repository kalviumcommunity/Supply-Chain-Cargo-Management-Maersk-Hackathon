package com.cargomanagement.controller;

import com.cargomanagement.models.Vendor;
import com.cargomanagement.repository.VendorRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/vendors")
@CrossOrigin(origins = "*")
public class VendorController {

    @Autowired
    private VendorRepository vendorRepository;

    // GET all vendors
    @GetMapping
    public ResponseEntity<List<Vendor>> getAllVendors() {
        try {
            List<Vendor> vendors = vendorRepository.findAll();
            if (vendors.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(vendors);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET vendor by ID
    @GetMapping("/{id}")
    public ResponseEntity<Vendor> getVendorById(@PathVariable Long id) {
        try {
            Optional<Vendor> vendor = vendorRepository.findById(id);
            if (vendor.isPresent()) {
                return ResponseEntity.ok(vendor.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // POST create new vendor
    @PostMapping
    public ResponseEntity<Vendor> createVendor(@Valid @RequestBody Vendor vendor) {
        try {
            Vendor savedVendor = vendorRepository.save(vendor);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedVendor);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // PUT update vendor
    @PutMapping("/{id}")
    public ResponseEntity<Vendor> updateVendor(@PathVariable Long id, @Valid @RequestBody Vendor vendorDetails) {
        try {
            Optional<Vendor> optionalVendor = vendorRepository.findById(id);
            if (optionalVendor.isPresent()) {
                Vendor vendor = optionalVendor.get();
                
                // Update fields
                vendor.setName(vendorDetails.getName());
                vendor.setContactInfo(vendorDetails.getContactInfo());
                vendor.setServiceType(vendorDetails.getServiceType());
                vendor.setIsActive(vendorDetails.getIsActive());
                
                Vendor updatedVendor = vendorRepository.save(vendor);
                return ResponseEntity.ok(updatedVendor);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // DELETE vendor
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteVendor(@PathVariable Long id) {
        try {
            if (vendorRepository.existsById(id)) {
                vendorRepository.deleteById(id);
                return ResponseEntity.ok("Vendor deleted successfully");
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting vendor: " + e.getMessage());
        }
    }

    // GET vendors by service type
    @GetMapping("/service-type/{serviceType}")
    public ResponseEntity<List<Vendor>> getVendorsByServiceType(@PathVariable String serviceType) {
        try {
            List<Vendor> vendors = vendorRepository.findByServiceType(serviceType);
            if (vendors.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(vendors);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET active vendors
    @GetMapping("/active")
    public ResponseEntity<List<Vendor>> getActiveVendors() {
        try {
            List<Vendor> vendors = vendorRepository.findByIsActive(true);
            if (vendors.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(vendors);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // PUT activate/deactivate vendor
    @PutMapping("/{id}/status")
    public ResponseEntity<Vendor> updateVendorStatus(@PathVariable Long id, @RequestParam Boolean isActive) {
        try {
            Optional<Vendor> optionalVendor = vendorRepository.findById(id);
            if (optionalVendor.isPresent()) {
                Vendor vendor = optionalVendor.get();
                vendor.setIsActive(isActive);
                Vendor updatedVendor = vendorRepository.save(vendor);
                return ResponseEntity.ok(updatedVendor);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}