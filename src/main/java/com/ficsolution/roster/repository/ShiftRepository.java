package com.ficsolution.roster.repository;

import com.ficsolution.roster.model.Shift;
import com.ficsolution.roster.model.enums.ShiftStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ShiftRepository extends JpaRepository<Shift, UUID>, JpaSpecificationExecutor<Shift> {

    List<Shift> findByEmployeeIdAndShiftDateAndStatusNot(UUID id, LocalDate day, ShiftStatus status);

    @EntityGraph(attributePaths = {"employee", "store"})
    Page<Shift> findAll(Specification<Shift> spec, Pageable pageable);

    @EntityGraph(attributePaths = {"employee", "store"})
    Optional<Shift> findById(UUID id);
}
