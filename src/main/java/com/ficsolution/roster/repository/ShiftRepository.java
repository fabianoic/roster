package com.ficsolution.roster.repository;

import com.ficsolution.roster.model.Shift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface ShiftRepository extends JpaRepository<Shift, UUID> {

    List<Shift> findByShiftDateBetween(LocalDate startDate, LocalDate endDate);

    List<Shift> findByEmployeeIdAndShiftDateBetween(UUID id, LocalDate startDate, LocalDate endDate);

    List<Shift> findByEmployeeIdAndShiftDate(UUID id, LocalDate day);
}
