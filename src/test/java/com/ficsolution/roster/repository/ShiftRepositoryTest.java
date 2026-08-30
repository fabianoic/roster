package com.ficsolution.roster.repository;

import com.ficsolution.roster.model.Employee;
import com.ficsolution.roster.model.Role;
import com.ficsolution.roster.model.Shift;
import com.ficsolution.roster.model.Store;
import com.ficsolution.roster.model.enumModel.EmployeeStatus;
import com.ficsolution.roster.model.enumModel.ShiftStatus;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.testcontainers.utility.TestcontainersConfiguration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

    private static final UUID id = UUID.randomUUID();

    private static Employee employee;
    private static Store store;
    private static Shift shift;

    @BeforeAll
    static void setUp() {
        employee = new Employee(UUID.randomUUID(), "Fabiano Campos",
                "fabiano.fic@gmail.com",
                "RANDOMHASHPASSWORD",
                new Role(UUID.fromString("df501f58-dc8a-470c-a26d-5786633b6009"), "STAFF"),
                EmployeeStatus.ACTIVE,
                LocalDateTime.now(),
                LocalDateTime.now());

        store = new Store(UUID.randomUUID(), "Blackrock", "Blackrock Shopping Center", LocalDateTime.now(), LocalDateTime.now());

        shift = new Shift(
                id,
                employee,
                store,
                LocalDate.now(),
                LocalTime.of(8, 0),
                LocalTime.of(16, 0),
                ShiftStatus.SCHEDULED,
                LocalDateTime.now(),
                LocalDateTime.now());
    }

    @Test
    void testCreateShift() {
        employeeRepository.save(employee);
        storeRepository.save(store);

        Shift createdShift = shiftRepository.save(shift);

        assertNotNull(createdShift);
        assertEquals(id, createdShift.getId());
        assertEquals(ShiftStatus.SCHEDULED, createdShift.getStatus());
    }

    @Test
    void testRetrieveAllShifts() {
        employeeRepository.save(employee);
        storeRepository.save(store);

        List<Shift> shifts = Arrays.asList(
                shift,
                new Shift(
                        UUID.randomUUID(),
                        employee,
                        store,
                        LocalDate.now().plusDays(1),
                        LocalTime.of(10, 0),
                        LocalTime.of(18, 0),
                        ShiftStatus.SCHEDULED,
                        LocalDateTime.now(),
                        LocalDateTime.now())
        );
        shiftRepository.saveAll(shifts);

        List<Shift> retrievedShifts = shiftRepository.findAll();

        assertNotNull(retrievedShifts);
        assertEquals(2, retrievedShifts.size());
    }

    @Test
    void testRetrieveShiftById() {
        employeeRepository.save(employee);
        storeRepository.save(store);

        shiftRepository.save(shift);

        Optional<Shift> retrievedShift = shiftRepository.findById(id);

        assertFalse(retrievedShift.isEmpty());
        assertEquals(id, retrievedShift.get().getId());
    }

    @Test
    void testRetrieveShiftBetweenTwoDates() {
        employeeRepository.save(employee);
        storeRepository.save(store);

        List<Shift> shifts = Arrays.asList(
                shift,
                new Shift(
                        UUID.randomUUID(),
                        employee,
                        store,
                        LocalDate.now().plusDays(8),
                        LocalTime.of(10, 0),
                        LocalTime.of(18, 0),
                        ShiftStatus.SCHEDULED,
                        LocalDateTime.now(),
                        LocalDateTime.now())
        );
        shiftRepository.saveAll(shifts);

        List<Shift> retrievedShifts = shiftRepository.findByShiftDateBetween(LocalDate.now(), LocalDate.now().plusDays(7));

        assertNotNull(retrievedShifts);
        assertEquals(1, retrievedShifts.size());
        assertEquals(id, retrievedShifts.getFirst().getId());
    }

    @Test
    void testRetrieveShiftEmployeeIdandShiftDateBetween() {
        Employee newEmployee = new Employee(UUID.randomUUID(), "Oscar",
                "oscar@gmail.com",
                "RANDOMHASHPASSWORD",
                new Role(UUID.fromString("df501f58-dc8a-470c-a26d-5786633b6009"), "STAFF"),
                EmployeeStatus.ACTIVE,
                LocalDateTime.now(),
                LocalDateTime.now());
        List<Shift> shifts = Arrays.asList(
                shift,
                new Shift(
                        UUID.randomUUID(),
                        newEmployee,
                        store,
                        LocalDate.now().plusDays(8),
                        LocalTime.of(10, 0),
                        LocalTime.of(18, 0),
                        ShiftStatus.SCHEDULED,
                        LocalDateTime.now(),
                        LocalDateTime.now())
        );
        employeeRepository.save(employee);
        employeeRepository.save(newEmployee);
        storeRepository.save(store);
        shiftRepository.saveAll(shifts);

        List<Shift> retrievedShifts = shiftRepository.findByEmployeeIdAndShiftDateBetween(employee.getId(), LocalDate.now(), LocalDate.now().plusDays(7));

        assertNotNull(retrievedShifts);
        assertEquals(1, retrievedShifts.size());
        assertNotEquals(newEmployee.getId(), retrievedShifts.getFirst().getId());
    }
}
