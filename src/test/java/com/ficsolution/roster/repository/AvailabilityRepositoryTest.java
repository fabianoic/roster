package com.ficsolution.roster.repository;

import com.ficsolution.roster.model.Availability;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import com.ficsolution.roster.TestcontainersConfiguration;

import java.nio.charset.StandardCharsets;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

    @Autowired
    private EntityManager entityManager;

    private static final UUID LEGACY_AVAILABILITY_ID = UUID.fromString("0f1d2c3b-4a59-4687-9a1b-2c3d4e5f6a7b");

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

    @Test
    void testWeekdayIsMappedAsIsoNumber() {
        // seed row has weekday = 1, which the schema defines as Monday
        assertEquals(DayOfWeek.MONDAY, availabilityRepository.findById(availabilityId).get().getWeekday());

        Availability availability = availabilityRepository.saveAndFlush(Availability.builder()
                .employee(employeeRepository.findById(employeeId).get())
                .weekday(DayOfWeek.SUNDAY)
                .isAvailable(true)
                .startTime(LocalTime.of(8, 0))
                .endTime(LocalTime.of(12, 0))
                .build());

        Number storedWeekday = (Number) entityManager
                .createNativeQuery("select weekday from availability where id = :id")
                .setParameter("id", availability.getId())
                .getSingleResult();
        assertEquals(7, storedWeekday.intValue());
    }

    @Test
    void testFixWeekdayMigrationShiftsOnlyRowsSavedAsOrdinal() throws Exception {
        // a THURSDAY availability saved by the API before the converter existed (ordinal 3)
        entityManager.createNativeQuery("""
                        insert into availability (id, employee_id, weekday, is_available, note, start_time, end_time)
                        values (:id, :employeeId, 3, true, 'legacy row', '08:00', '12:00')
                        """)
                .setParameter("id", LEGACY_AVAILABILITY_ID)
                .setParameter("employeeId", employeeId)
                .executeUpdate();
        String migration = new ClassPathResource("db/migration/V5__fix_availability_weekday.sql")
                .getContentAsString(StandardCharsets.UTF_8);

        entityManager.createNativeQuery(migration).executeUpdate();
        entityManager.clear();

        assertEquals(DayOfWeek.THURSDAY, availabilityRepository.findById(LEGACY_AVAILABILITY_ID).get().getWeekday());
        assertEquals(DayOfWeek.MONDAY, availabilityRepository.findById(availabilityId).get().getWeekday());
    }
}
