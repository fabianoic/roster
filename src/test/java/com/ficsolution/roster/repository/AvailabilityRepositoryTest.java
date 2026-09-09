package com.ficsolution.roster.repository;

import com.ficsolution.roster.model.Availability;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.testcontainers.utility.TestcontainersConfiguration;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static com.ficsolution.roster.util.Util.availabilityId;
import static com.ficsolution.roster.util.Util.employeeId;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(TestcontainersConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class AvailabilityRepositoryTest {

    @Autowired
    private AvailabilityRepository availabilityRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Test
    void testCreateAndRetrieveAvailabilityById() {
        Availability availability = new Availability(
                null,
                employeeRepository.findById(employeeId).get(),
                DayOfWeek.THURSDAY,
                false,
                "test reason for tests.",
                LocalTime.of(0, 0),
                LocalTime.of(23, 59)
        );
        availability = availabilityRepository.save(availability);

        Optional<Availability> retrievedAvailability = availabilityRepository.findById(availability.getId());

        assertFalse(retrievedAvailability.isEmpty());
        assertEquals(DayOfWeek.THURSDAY, retrievedAvailability.get().getWeekday());
        assertEquals(employeeId, retrievedAvailability.get().getEmployee().getId());
    }

    @Test
    void testRetrieveAllAvailability() {
        List<Availability> retrievedAllAvailability =
                availabilityRepository.findAll();

        assertNotNull(retrievedAllAvailability);
        assertEquals(4, retrievedAllAvailability.size());
    }

    @Test
    void testRetrieveAllAvailabilityByEmployeeId() {
        Availability availability = new Availability(
                null,
                employeeRepository.findById(employeeId).get(),
                DayOfWeek.THURSDAY,
                false,
                "test reason for tests.",
                LocalTime.of(0, 0),
                LocalTime.of(23, 59)
        );
        availabilityRepository.save(availability);
        List<Availability> retrievedAllAvailability =
                availabilityRepository.findByEmployeeId(employeeId);

        assertNotNull(retrievedAllAvailability);
        assertEquals(2, retrievedAllAvailability.size());
    }

    @Test
    void testUpdateAvailability() {
        Availability availability = availabilityRepository.findById(availabilityId).get();
        availabilityRepository.save(availability);
        availability.setAvailable(true);
        availability.setWeekday(DayOfWeek.FRIDAY);

        Availability updatedAvailability = availabilityRepository.save(availability);

        assertNotNull(updatedAvailability);
        assertEquals(DayOfWeek.FRIDAY, updatedAvailability.getWeekday());
        assertTrue(updatedAvailability.isAvailable());
    }

    @Test
    void testDeleteAvailability() {
        Availability availability = availabilityRepository.findById(availabilityId).get();

        availabilityRepository.delete(availability);

        assertFalse(availabilityRepository.findById(availabilityId).isPresent());
    }
}
