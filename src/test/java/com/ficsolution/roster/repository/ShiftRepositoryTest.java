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
import com.ficsolution.roster.TestcontainersConfiguration;

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

    @Test
    void testUpdateShiftStatusBeforeShiftDate() {
        LocalDate today = LocalDate.of(2000, 1, 10);
        Shift pastScheduled = shiftRepository.save(buildShift(today.minusDays(1), ShiftStatus.SCHEDULED));
        Shift pastCanceled = shiftRepository.save(buildShift(today.minusDays(1), ShiftStatus.CANCELED));
        Shift todayScheduled = shiftRepository.save(buildShift(today, ShiftStatus.SCHEDULED));
        shiftRepository.flush();

        int updated = shiftRepository.updateShiftStatusBeforeShiftDate(ShiftStatus.SCHEDULED, ShiftStatus.COMPLETED, LocalDateTime.now(), today);

        assertEquals(1, updated);
        assertEquals(ShiftStatus.COMPLETED, shiftRepository.findById(pastScheduled.getId()).get().getStatus());
        assertEquals(ShiftStatus.CANCELED, shiftRepository.findById(pastCanceled.getId()).get().getStatus());
        assertEquals(ShiftStatus.SCHEDULED, shiftRepository.findById(todayScheduled.getId()).get().getStatus());
    }

    @Test
    void testUpdateShiftStatusBeforeShiftDateWithNothingToUpdate() {
        int updated = shiftRepository.updateShiftStatusBeforeShiftDate(ShiftStatus.SCHEDULED, ShiftStatus.COMPLETED, LocalDateTime.now(), LocalDate.of(2000, 1, 10));

        assertEquals(0, updated);
    }

    private Shift buildShift(LocalDate shiftDate, ShiftStatus status) {
        return Shift.builder()
                .employee(employeeRepository.findById(employeeId).get())
                .store(storeRepository.findById(storeId).get())
                .shiftDate(shiftDate)
                .startTime(LocalTime.of(8, 0))
                .endTime(LocalTime.of(16, 0))
                .status(status)
                .build();
    }
}
