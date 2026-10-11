package com.ficsolution.roster.repository;

import com.ficsolution.roster.model.Availability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AvailabilityRepository extends JpaRepository<Availability, UUID>, JpaSpecificationExecutor<Availability> {

    List<Availability> findByEmployeeId(UUID id);
}
