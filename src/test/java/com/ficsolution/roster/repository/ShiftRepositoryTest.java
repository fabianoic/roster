package com.ficsolution.roster.repository;

import com.ficsolution.roster.model.Shift;
import com.ficsolution.roster.model.enumModel.ShiftStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.testcontainers.utility.TestcontainersConfiguration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
        UUID id = UUID.randomUUID();
        Shift shift = new Shift(
                id,
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
        assertEquals(id, createdShift.getId());
        assertEquals(ShiftStatus.SCHEDULED, createdShift.getStatus());
    }

    @Test
    void testRetrieveAllShifts() {
        List<Shift> retrievedShifts = shiftRepository.findAll();

        assertNotNull(retrievedShifts);
        assertEquals(4, retrievedShifts.size());
    }

    @Test
    void testRetrieveShiftById() {
        Optional<Shift> retrievedShift = shiftRepository.findById(shiftId);

        assertFalse(retrievedShift.isEmpty());
        assertEquals(shiftId, retrievedShift.get().getId());
    }

    @Test
    void testRetrieveShiftBetweenTwoDates() {
        List<Shift> retrievedShifts = shiftRepository.findByShiftDateBetween(LocalDate.now(), LocalDate.now().plusDays(7));

        assertNotNull(retrievedShifts);
        assertEquals(4, retrievedShifts.size());
        assertEquals(shiftId, retrievedShifts.getFirst().getId());
    }

    @Test
    void testRetrieveShiftEmployeeIdandShiftDateBetween() {
        System.out.println(employeeId);
        System.out.println(LocalDate.now());
        System.out.println(LocalDate.now().plusDays(7));
        List<Shift> retrievedShifts = shiftRepository.findByEmployeeIdAndShiftDateBetween(employeeId, LocalDate.now(), LocalDate.now().plusDays(7));

        assertNotNull(retrievedShifts);
        assertEquals(2, retrievedShifts.size());
        assertNotEquals(employeeId, retrievedShifts.getFirst().getId());
    }
}
