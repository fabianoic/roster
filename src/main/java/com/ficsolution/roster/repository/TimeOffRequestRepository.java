package com.ficsolution.roster.repository;

import com.ficsolution.roster.model.TimeOffRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TimeOffRequestRepository extends JpaRepository<TimeOffRequest, UUID>, JpaSpecificationExecutor<TimeOffRequest> {
    List<TimeOffRequest> findByEmployeeId(UUID employeeId);
}
