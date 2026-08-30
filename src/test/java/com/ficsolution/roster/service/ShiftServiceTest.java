package com.ficsolution.roster.service;

import com.ficsolution.roster.exception.ConflictShiftException;
import com.ficsolution.roster.exception.InvalidDateTimeException;
import com.ficsolution.roster.model.Employee;
import com.ficsolution.roster.model.Role;
import com.ficsolution.roster.model.Shift;
import com.ficsolution.roster.model.Store;
import com.ficsolution.roster.model.enumModel.EmployeeStatus;
import com.ficsolution.roster.model.enumModel.ShiftStatus;
import com.ficsolution.roster.repository.ShiftRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ShiftServiceTest {

    @InjectMocks
    private ShiftService shiftService;

    @Mock
    private ShiftRepository shiftRepository;

    @Mock
    private EmployeeService employeeService;

    @Mock
    private StoreService storeService;

    private static UUID id = UUID.randomUUID();
    private static Shift shift;
    private static Store store;
    private static Employee employee;

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
        when(shiftRepository.save(any(Shift.class))).thenReturn(shift);
        when(employeeService.retrieveEmployeeById(employee.getId())).thenReturn(employee);
        when(storeService.retrieveStoreById(store.getId())).thenReturn(store);

        Shift createdShift = shiftService.createShift(shift);

        assertNotNull(createdShift);
        assertEquals(id, createdShift.getId());
        verify(employeeService, times(1)).retrieveEmployeeById(employee.getId());
        verify(storeService, times(1)).retrieveStoreById(store.getId());
        verify(shiftRepository, times(1)).save(shift);
    }

    @Test
    void testCreateShift_invalidDateTime() {
        Shift wrongShift = new Shift(
                id,
                employee,
                store,
                LocalDate.now(),
                LocalTime.of(16, 0),
                LocalTime.of(8, 0),
                ShiftStatus.SCHEDULED,
                LocalDateTime.now(),
                LocalDateTime.now());

        assertThrows(InvalidDateTimeException.class, () -> shiftService.createShift(wrongShift));
        verify(employeeService, times(0)).retrieveEmployeeById(employee.getId());
        verify(storeService, times(0)).retrieveStoreById(store.getId());
        verify(shiftRepository, times(0)).save(wrongShift);
    }

    @Test
    void testRetrieveAllShifts() {
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
        when(shiftRepository.findAll()).thenReturn(shifts);

        List<Shift> retrievedShifts = shiftService.retrieveAllShifts();

        assertNotNull(retrievedShifts);
        assertEquals(2, retrievedShifts.size());
    }

    @Test
    void testRetrieveShiftById() {
        when(shiftRepository.findById(any(UUID.class))).thenReturn(Optional.of(shift));

        Shift retrievedShift = shiftService.retrieveShiftById(shift.getId());

        assertNotNull(retrievedShift);
        assertEquals(id, retrievedShift.getId());
    }

    @Test
    void testRetrieveShiftBetweenDates() {
        List<Shift> shifts = Arrays.asList(
                shift,
                new Shift(
                        UUID.randomUUID(),
                        employee,
                        store,
                        LocalDate.now().plusDays(7),
                        LocalTime.of(10, 0),
                        LocalTime.of(18, 0),
                        ShiftStatus.SCHEDULED,
                        LocalDateTime.now(),
                        LocalDateTime.now())
        );
        when(shiftRepository.findByShiftDateBetween(any(LocalDate.class), any(LocalDate.class))).thenReturn(shifts);

        List<Shift> retrievedShifts = shiftService.retrieveAllShiftsBetweenDates(LocalDate.now(), LocalDate.now().plusDays(7));

        assertNotNull(retrievedShifts);
        assertEquals(2, retrievedShifts.size());
    }

    @Test
    void testRetrieveShiftByEmployeeIdAndShiftDateBetween() {
        List<Shift> shifts = Arrays.asList(
                shift,
                new Shift(
                        UUID.randomUUID(),
                        employee,
                        store,
                        LocalDate.now().plusDays(7),
                        LocalTime.of(10, 0),
                        LocalTime.of(18, 0),
                        ShiftStatus.SCHEDULED,
                        LocalDateTime.now(),
                        LocalDateTime.now())
        );
        when(shiftRepository.findByEmployeeIdAndShiftDateBetween(any(UUID.class), any(LocalDate.class), any(LocalDate.class))).thenReturn(shifts);

        List<Shift> retrievedShifts = shiftService.retrieveShiftByEmployeeIdAndShiftDateBetween(employee.getId(), LocalDate.now(), LocalDate.now().plusDays(7));

        assertNotNull(retrievedShifts);
        assertEquals(2, retrievedShifts.size());
    }

    @Test
    void testUpdateShiftInfo() {
        Store newStore = new Store();
        newStore.setId(UUID.randomUUID());
        Shift newShift = new Shift(
                id,
                employee,
                newStore,
                LocalDate.now(),
                LocalTime.of(10, 0),
                LocalTime.of(18, 0),
                ShiftStatus.SCHEDULED,
                LocalDateTime.now(),
                LocalDateTime.now());
        when(shiftRepository.findById(any(UUID.class))).thenReturn(Optional.of(shift));
        when(shiftRepository.save(any(Shift.class))).thenReturn(newShift);
        when(storeService.retrieveStoreById(any(UUID.class))).thenReturn(newStore);

        Shift updatedShift = shiftService.updateShiftInfo(id, newShift);

        assertNotNull(updatedShift);
        assertEquals(ShiftStatus.SCHEDULED, updatedShift.getStatus());
        assertEquals(employee.getId(), updatedShift.getEmployee().getId());
        assertNotEquals(LocalTime.of(8, 0), updatedShift.getStartTime());
        assertNotEquals(LocalTime.of(16, 0), updatedShift.getEndTime());
        verify(shiftRepository, times(1)).findById(any());
        verify(shiftRepository, times(1)).save(any());
        verify(storeService, times(1)).retrieveStoreById(any());
    }

    @Test
    void testUpdateShiftTime_NotCorrect() {
        Store newStore = new Store();
        newStore.setId(UUID.randomUUID());
        Shift newShift = new Shift(
                id,
                employee,
                newStore,
                LocalDate.now(),
                LocalTime.of(18, 0),
                LocalTime.of(8, 0),
                ShiftStatus.SCHEDULED,
                LocalDateTime.now(),
                LocalDateTime.now());

        assertThrows(InvalidDateTimeException.class, () -> shiftService.updateShiftInfo(id, newShift));
    }

    @Test
    void testSwapShift() {
        Employee newEmployee = new Employee(UUID.randomUUID(), "Fabiano Campos",
                "fabiano.fic@gmail.com",
                "RANDOMHASHPASSWORD",
                new Role(UUID.fromString("df501f58-dc8a-470c-a26d-5786633b6009"), "STAFF"),
                EmployeeStatus.ACTIVE,
                LocalDateTime.now(),
                LocalDateTime.now());
        when(shiftRepository.findById(id)).thenReturn(Optional.of(shift));
        when(employeeService.retrieveEmployeeById(newEmployee.getId())).thenReturn(newEmployee);
        when(shiftRepository.save(any(Shift.class))).thenReturn(shift);

        Shift swappedShift = shiftService.swapShiftEmployee(id, newEmployee.getId());

        assertNotNull(swappedShift);
        assertEquals(newEmployee.getId(), swappedShift.getEmployee().getId());
        verify(shiftRepository, times(1)).findById(id);
        verify(employeeService, times(1)).retrieveEmployeeById(newEmployee.getId());
    }

    @Test
    void testSwapShift_conflictShift() {
        Employee newEmployee = new Employee(UUID.randomUUID(), "Fabiano Campos",
                "fabiano.fic@gmail.com",
                "RANDOMHASHPASSWORD",
                new Role(UUID.fromString("df501f58-dc8a-470c-a26d-5786633b6009"), "STAFF"),
                EmployeeStatus.ACTIVE,
                LocalDateTime.now(),
                LocalDateTime.now());

        Shift otherShift = new Shift(
                id,
                employee,
                store,
                LocalDate.now(),
                LocalTime.of(10, 0),
                LocalTime.of(18, 0),
                ShiftStatus.SCHEDULED,
                LocalDateTime.now(),
                LocalDateTime.now());
        when(shiftRepository.findById(id)).thenReturn(Optional.of(shift));
        when(shiftRepository.findByEmployeeIdAndShiftDate(any(), any())).thenReturn(List.of(otherShift));

        assertThrows(ConflictShiftException.class, () -> shiftService.swapShiftEmployee(id, newEmployee.getId()));
        verify(shiftRepository, times(1)).findByEmployeeIdAndShiftDate(any(), any());
    }
}
