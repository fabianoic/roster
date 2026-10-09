package com.ficsolution.roster.repository;

import com.ficsolution.roster.model.Employee;
import com.ficsolution.roster.model.Role;
import com.ficsolution.roster.model.Shift;
import com.ficsolution.roster.model.TimeOffRequest;
import com.ficsolution.roster.model.enums.EmployeeStatus;
import com.ficsolution.roster.model.enums.RequestStatus;
import com.ficsolution.roster.model.enums.ShiftStatus;
import com.ficsolution.roster.model.enums.TimeOffRequestType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import com.ficsolution.roster.TestcontainersConfiguration;

import java.time.DayOfWeek;
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
public class EmployeeRepositoryTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ShiftRepository shiftRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private TimeOffRequestRepository timeOffRequestRepository;

    @Test
    void testCreateEmployee() {
        // arrange
        Employee employee = new Employee(null, "Fabiano Campos",
                "fabiano.fic@gmail.com",
                "RANDOMHASHPASSWORD",
                new Role(UUID.randomUUID(), "STAFF"),
                EmployeeStatus.ACTIVE,
                LocalDateTime.now(),
                LocalDateTime.now(),
                false,
                0,
                null);

        // act
        Employee savedEmployee = employeeRepository.save(employee);

        // assert
        assertNotNull(savedEmployee);
        assertEquals("Fabiano Campos", savedEmployee.getName());
        assertEquals("fabiano.fic@gmail.com", savedEmployee.getEmail());
    }

    @Test
    void testRetrieveAllEmployees() {
        List<Employee> retrievedAllEmployees = employeeRepository.findAll();

        assertFalse(retrievedAllEmployees.isEmpty());
        assertEquals(5, retrievedAllEmployees.size());
    }

    @Test
    void testRetrieveEmployeeById() {
        Optional<Employee> retrievedEmployee = employeeRepository.findById(employeeId);

        assertFalse(retrievedEmployee.isEmpty());
        assertEquals("Ana Silva", retrievedEmployee.get().getName());
        assertEquals("ana.silva@empresa.com", retrievedEmployee.get().getEmail());
    }

    @Test
    void testUpdateEmployeeNameAndPasswordAndStatus() {
        Employee employee = employeeRepository.findById(employeeId).get();
        employee.setPassword("NEWRANDOMHASHPASSWORD");
        employee.setName("Oscar");
        employee.setStatus(EmployeeStatus.INACTIVE);

        Employee updatedEmployee = employeeRepository.save(employee);

        assertNotNull(updatedEmployee);
        assertEquals("Oscar", updatedEmployee.getName());
        assertEquals(EmployeeStatus.INACTIVE, updatedEmployee.getStatus());
        assertEquals("NEWRANDOMHASHPASSWORD", updatedEmployee.getPassword());
    }

    @Test
    void testDeleteEmployee() {
        Employee employee = employeeRepository.findById(employeeId).get();
        employeeRepository.delete(employee);

        assertFalse(employeeRepository.findById(employeeId).isPresent());
    }

    /*
     * Eligibility tests use a week far from the seed shifts and approved time off, so results don't depend on the
     * day the suite runs. Seed availability: Bruno MONDAY available 08-16, Carla TUESDAY available 13-21,
     * Diego FRIDAY not available 08-12, Ana SATURDAY available 08-18. Ana (employeeId) owns the shift and
     * Eduarda is INACTIVE, so neither is ever a candidate.
     */
    private static final LocalDate WEEK = LocalDate.now().plusWeeks(5).with(DayOfWeek.MONDAY);
    private static final LocalDate MONDAY = WEEK;
    private static final LocalDate TUESDAY = WEEK.plusDays(1);
    private static final LocalDate WEDNESDAY = WEEK.plusDays(2);
    private static final LocalDate FRIDAY = WEEK.plusDays(4);

    @Test
    void testFindEligibleForShift_withoutRestrictions() {
        assertEquals(List.of("Bruno Costa", "Carla Souza", "Diego Rocha"),
                candidateNames(WEDNESDAY, LocalTime.of(8, 0), LocalTime.of(16, 0)));
    }

    @Test
    void testFindEligibleForShift_excludesEmployeeWithOverlappingShift() {
        saveShiftForBruno(WEDNESDAY, LocalTime.of(15, 0), LocalTime.of(20, 0), ShiftStatus.SCHEDULED);

        assertEquals(List.of("Carla Souza", "Diego Rocha"),
                candidateNames(WEDNESDAY, LocalTime.of(8, 0), LocalTime.of(16, 0)));
    }

    @Test
    void testFindEligibleForShift_ignoresCanceledOverlappingShift() {
        saveShiftForBruno(WEDNESDAY, LocalTime.of(9, 0), LocalTime.of(12, 0), ShiftStatus.CANCELED);

        assertTrue(candidateNames(WEDNESDAY, LocalTime.of(8, 0), LocalTime.of(16, 0)).contains("Bruno Costa"));
    }

    @Test
    void testFindEligibleForShift_keepsEmployeeWithAdjacentShift() {
        saveShiftForBruno(WEDNESDAY, LocalTime.of(16, 0), LocalTime.of(20, 0), ShiftStatus.SCHEDULED);

        assertTrue(candidateNames(WEDNESDAY, LocalTime.of(8, 0), LocalTime.of(16, 0)).contains("Bruno Costa"));
    }

    @Test
    void testFindEligibleForShift_excludesEmployeeWithApprovedTimeOff() {
        saveTimeOffForBruno(WEDNESDAY.minusDays(1), WEDNESDAY, RequestStatus.APPROVED);

        assertFalse(candidateNames(WEDNESDAY, LocalTime.of(8, 0), LocalTime.of(16, 0)).contains("Bruno Costa"));
    }

    @Test
    void testFindEligibleForShift_ignoresNotApprovedTimeOff() {
        saveTimeOffForBruno(WEDNESDAY, WEDNESDAY, RequestStatus.PENDING);
        saveTimeOffForBruno(WEDNESDAY, WEDNESDAY.plusDays(1), RequestStatus.REJECTED);

        assertTrue(candidateNames(WEDNESDAY, LocalTime.of(8, 0), LocalTime.of(16, 0)).contains("Bruno Costa"));
    }

    @Test
    void testFindEligibleForShift_keepsEmployeeWhoseAvailabilityCoversTheShift() {
        assertTrue(candidateNames(MONDAY, LocalTime.of(8, 0), LocalTime.of(16, 0)).contains("Bruno Costa"));
        assertTrue(candidateNames(TUESDAY, LocalTime.of(14, 0), LocalTime.of(20, 0)).contains("Carla Souza"));
    }

    @Test
    void testFindEligibleForShift_excludesEmployeeWhoseAvailabilityDoesNotCoverTheShift() {
        assertEquals(List.of("Carla Souza", "Diego Rocha"),
                candidateNames(MONDAY, LocalTime.of(10, 0), LocalTime.of(18, 0)));
        assertFalse(candidateNames(TUESDAY, LocalTime.of(8, 0), LocalTime.of(16, 0)).contains("Carla Souza"));
    }

    @Test
    void testFindEligibleForShift_excludesEmployeeNotAvailableDuringTheShift() {
        assertEquals(List.of("Bruno Costa", "Carla Souza"),
                candidateNames(FRIDAY, LocalTime.of(10, 0), LocalTime.of(14, 0)));
        assertTrue(candidateNames(FRIDAY, LocalTime.of(12, 0), LocalTime.of(16, 0)).contains("Diego Rocha"));
    }

    @Test
    void testIsEligibleForShift() {
        assertTrue(employeeRepository.isEligibleForShift(employee1Id, WEDNESDAY, DayOfWeek.WEDNESDAY,
                LocalTime.of(8, 0), LocalTime.of(16, 0)));
        assertFalse(employeeRepository.isEligibleForShift(employee1Id, MONDAY, DayOfWeek.MONDAY,
                LocalTime.of(10, 0), LocalTime.of(18, 0)));
        assertFalse(employeeRepository.isEligibleForShift(UUID.randomUUID(), WEDNESDAY, DayOfWeek.WEDNESDAY,
                LocalTime.of(8, 0), LocalTime.of(16, 0)));
    }

    private List<String> candidateNames(LocalDate shiftDate, LocalTime startTime, LocalTime endTime) {
        return employeeRepository.findEligibleForShift(employeeId, shiftDate, shiftDate.getDayOfWeek(), startTime, endTime)
                .stream()
                .map(Employee::getName)
                .toList();
    }

    private void saveShiftForBruno(LocalDate shiftDate, LocalTime startTime, LocalTime endTime, ShiftStatus status) {
        shiftRepository.save(Shift.builder()
                .employee(employeeRepository.findById(employee1Id).get())
                .store(storeRepository.findById(storeId).get())
                .shiftDate(shiftDate)
                .startTime(startTime)
                .endTime(endTime)
                .status(status)
                .build());
    }

    private void saveTimeOffForBruno(LocalDate startDate, LocalDate endDate, RequestStatus status) {
        timeOffRequestRepository.save(TimeOffRequest.builder()
                .employee(employeeRepository.findById(employee1Id).get())
                .startDate(startDate)
                .endDate(endDate)
                .type(TimeOffRequestType.HOLIDAY)
                .status(status)
                .build());
    }
}
