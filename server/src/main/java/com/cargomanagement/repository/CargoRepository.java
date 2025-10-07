package com.cargomanagement.repository;

import com.cargomanagement.models.Cargo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CargoRepository extends JpaRepository<Cargo, Long> {
    List<Cargo> findByShipmentShipmentId(Long shipmentId);
    List<Cargo> findByType(String type);
}
