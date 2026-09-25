package com.ficsolution.roster.repository;

import com.ficsolution.roster.model.Shift;
import com.ficsolution.roster.model.enums.ShiftStatus;
import com.ficsolution.roster.repository.specification.ShiftSpecification;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.testcontainers.utility.TestcontainersConfiguration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static com.ficsolution.roster.util.Util.*;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(TestcontainersConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ShiftRepositoryTest {

    @Autowired
    private ShiftRepository shiftRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Test
    void testCreateShift() {
        Shift shift = new Shift(
                null,
                employeeRepository.findById(employeeId).get(),
                storeRepository.findById(storeId).get(),
                LocalDate.now(),
                LocalTime.of(8, 0),
                LocalTime.of(16, 0),
                ShiftStatus.SCHEDULED,
                LocalDateTime.now(),
                LocalDateTime.now());
        Shift createdShift = shiftRepository.save(shift);

        assertNotNull(createdShift);
        assertEquals(ShiftStatus.SCHEDULED, createdShift.getStatus());
    }

    @Test
    void testRetrieveAllShifts() {
        Specification<Shift> specification = Specification.allOf(
                ShiftSpecification.hasEmployeeId(employeeId)
        );
        Pageable pageable = PageRequest.of(0, 10);
        Page<Shift> retrievedShifts = shiftRepository.findAll(specification, pageable);

        assertNotNull(retrievedShifts);
        assertEquals(2, retrievedShifts.getTotalElements());
    }

    @Test
    void testRetrieveShiftById() {
        Optional<Shift> retrievedShift = shiftRepository.findById(shiftId);

        assertFalse(retrievedShift.isEmpty());
        assertEquals(shiftId, retrievedShift.get().getId());
    }
}
