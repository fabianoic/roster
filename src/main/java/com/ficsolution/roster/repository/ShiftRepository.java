package com.ficsolution.roster.repository;

import com.ficsolution.roster.model.Shift;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface ShiftRepository extends JpaRepository<Shift, UUID>, JpaSpecificationExecutor<Shift> {

    List<Shift> findByEmployeeIdAndShiftDate(UUID id, LocalDate day);

    @EntityGraph(attributePaths = {"employee", "store"})
    Page<Shift> findAll(Specification<Shift> spec, Pageable pageable);
}
