package com.cargomanagement.controller;

import com.cargomanagement.models.Route;
import com.cargomanagement.repository.RouteRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/routes")
@CrossOrigin(origins = "*")
public class RouteController {

    @Autowired
    private RouteRepository routeRepository;

    // GET all routes
    @GetMapping
    public ResponseEntity<List<Route>> getAllRoutes() {
        try {
            List<Route> routes = routeRepository.findAll();
            if (routes.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(routes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET route by ID
    @GetMapping("/{id}")
    public ResponseEntity<Route> getRouteById(@PathVariable Long id) {
        try {
            Optional<Route> route = routeRepository.findById(id);
            if (route.isPresent()) {
                return ResponseEntity.ok(route.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // POST create new route
    @PostMapping
    public ResponseEntity<Route> createRoute(@Valid @RequestBody Route route) {
        try {
            Route savedRoute = routeRepository.save(route);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedRoute);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // PUT update route
    @PutMapping("/{id}")
    public ResponseEntity<Route> updateRoute(@PathVariable Long id, @Valid @RequestBody Route routeDetails) {
        try {
            Optional<Route> optionalRoute = routeRepository.findById(id);
            if (optionalRoute.isPresent()) {
                Route route = optionalRoute.get();
                
                // Update fields
                route.setOriginPort(routeDetails.getOriginPort());
                route.setDestinationPort(routeDetails.getDestinationPort());
                route.setDuration(routeDetails.getDuration());
                route.setStatus(routeDetails.getStatus());
                route.setDistance(routeDetails.getDistance());
                route.setTransportationMode(routeDetails.getTransportationMode());
                route.setCost(routeDetails.getCost());
                
                Route updatedRoute = routeRepository.save(route);
                return ResponseEntity.ok(updatedRoute);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // DELETE route
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRoute(@PathVariable Long id) {
        try {
            if (routeRepository.existsById(id)) {
                routeRepository.deleteById(id);
                return ResponseEntity.ok("Route deleted successfully");
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting route: " + e.getMessage());
        }
    }

    // GET routes by status
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Route>> getRoutesByStatus(@PathVariable String status) {
        try {
            List<Route> routes = routeRepository.findByStatus(status);
            if (routes.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(routes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET routes by origin and destination
    @GetMapping("/search")
    public ResponseEntity<List<Route>> getRoutesByOriginAndDestination(
            @RequestParam String origin, 
            @RequestParam String destination) {
        try {
            List<Route> routes = routeRepository.findByOriginPortAndDestinationPort(origin, destination);
            if (routes.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(routes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET routes by transportation mode
    @GetMapping("/transport-mode/{mode}")
    public ResponseEntity<List<Route>> getRoutesByTransportationMode(@PathVariable String mode) {
        try {
            List<Route> routes = routeRepository.findByTransportationMode(mode);
            if (routes.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(routes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // PUT update route status
    @PutMapping("/{id}/status")
    public ResponseEntity<Route> updateRouteStatus(@PathVariable Long id, @RequestParam String status) {
        try {
            Optional<Route> optionalRoute = routeRepository.findById(id);
            if (optionalRoute.isPresent()) {
                Route route = optionalRoute.get();
                route.setStatus(status);
                Route updatedRoute = routeRepository.save(route);
                return ResponseEntity.ok(updatedRoute);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}