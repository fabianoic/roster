package com.ficsolution.roster.repository;

import com.ficsolution.roster.model.ShiftSwapRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ShiftSwapRequestRepository extends JpaRepository<ShiftSwapRequest, UUID> {
    List<ShiftSwapRequest> findByRequesterId(UUID employeeId);
}
