package com.ficsolution.roster.service;

import com.ficsolution.roster.exception.AvailabilityNotFoundException;
import com.ficsolution.roster.model.Availability;
import com.ficsolution.roster.model.Employee;
import com.ficsolution.roster.repository.AvailabilityRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.ficsolution.roster.util.Util.availabilityId;
import static com.ficsolution.roster.util.Util.employeeId;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AvailabilityServiceTest {

    @InjectMocks
    private AvailabilityService availabilityService;

    @Mock
    private AvailabilityRepository availabilityRepository;

    @Mock
    private EmployeeService employeeService;

    private static Availability availability;

    @BeforeAll
    static void setUp() {
        Employee employee = new Employee();
        employee.setId(employeeId);
        availability = new Availability(
                availabilityId,
                employee,
                DayOfWeek.THURSDAY,
                false,
                "test reason for tests.",
                LocalTime.of(0, 0),
                LocalTime.of(23, 59)
        );
    }

    @Test
    void testCreateAvailability() {
        when(availabilityRepository.save(any(Availability.class))).thenReturn(availability);
        when(employeeService.retrieveEmployeeById(employeeId)).thenReturn(availability.getEmployee());

        Availability savedAvailability = availabilityService.createAvailability(availability);

        assertNotNull(savedAvailability);
        assertEquals(availabilityId, savedAvailability.getId());
        assertEquals(employeeId, availability.getEmployee().getId());
        verify(availabilityRepository, times(1)).save(availability);
        verify(employeeService, times(1)).retrieveEmployeeById(employeeId);
    }

    @Test
    void testRetrieveAllAvailabilities() {
        Employee newEmployee = new Employee();
        newEmployee.setId(employeeId);
        List<Availability> availabilityList = Arrays.asList(
                availability,
                new Availability(
                        UUID.randomUUID(),
                        newEmployee,
                        DayOfWeek.FRIDAY,
                        false,
                        "test reason for tests.",
                        LocalTime.of(0, 0),
                        LocalTime.of(23, 59)
                )
        );
        when(availabilityRepository.findAll()).thenReturn(availabilityList);

        List<Availability> retrievedList = availabilityService.findAllAvailabilities();

        assertNotNull(retrievedList);
        assertEquals(2, retrievedList.size());
        assertEquals(DayOfWeek.FRIDAY, retrievedList.get(1).getWeekday());
        verify(availabilityRepository, times(1)).findAll();
    }

    @Test
    void testRetrieveAvailabilityById() {
        when(availabilityRepository.findById(availabilityId)).thenReturn(Optional.of(availability));

        Availability retrieveAvailabilityById = availabilityService.retrieveAvailabilityById(availabilityId);

        assertNotNull(retrieveAvailabilityById);
        assertEquals(availabilityId, retrieveAvailabilityById.getId());
        assertEquals(employeeId, availability.getEmployee().getId());
        verify(availabilityRepository, times(1)).findById(availabilityId);
    }

    @Test
    void testRetrieveAvailabilityById_notFound() {
        when(availabilityRepository.findById(availabilityId)).thenReturn(Optional.empty());

        assertThrows(AvailabilityNotFoundException.class, () -> availabilityService.retrieveAvailabilityById(availabilityId));
        verify(availabilityRepository, times(1)).findById(availabilityId);
    }

    @Test
    void testRetrieveAllAvailabilityByEmployeeId() {
        when(availabilityRepository.findByEmployeeId(employeeId)).thenReturn(List.of(availability));

        List<Availability> retrieveAvailability = availabilityService.retrieveAllAvailabilityByEmployeeId(employeeId);

        assertNotNull(retrieveAvailability);
        assertEquals(1, retrieveAvailability.size());
        assertEquals(employeeId, availability.getEmployee().getId());
        verify(availabilityRepository, times(1)).findByEmployeeId(employeeId);
    }

    @Test
    void testUpdateAvailability() {
        Employee newEmployee = new Employee();
        newEmployee.setId(employeeId);
        Availability newAvailability = new Availability(
                availabilityId,
                newEmployee,
                DayOfWeek.MONDAY,
                true,
                null,
                LocalTime.of(0, 0),
                LocalTime.of(23, 59)
        );
        when(availabilityRepository.findById(availabilityId)).thenReturn(Optional.of(availability));
        when(availabilityRepository.save(any(Availability.class))).thenReturn(newAvailability);

        Availability updatedAvailability = availabilityService.updateAvailability(availabilityId, newAvailability);

        assertNotNull(updatedAvailability);
        assertEquals(DayOfWeek.MONDAY, updatedAvailability.getWeekday());
        assertTrue(updatedAvailability.isAvailable());
        verify(availabilityRepository, times(1)).findById(availabilityId);
        verify(availabilityRepository, times(1)).save(newAvailability);
    }

    @Test
    void testDeleteAvailability() {
        when(availabilityRepository.findById(availabilityId)).thenReturn(Optional.of(availability));

        availabilityService.deleteAvailability(availabilityId);

        verify(availabilityRepository, times(1)).delete(availability);
        verify(availabilityRepository, times(1)).findById(availabilityId);
    }
}
