package com.ficsolution.roster.repository;

import com.ficsolution.roster.model.Employee;
import com.ficsolution.roster.model.TimeOffRequest;
import com.ficsolution.roster.model.enumModel.RequestStatus;
import com.ficsolution.roster.model.enumModel.TimeOffRequestType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.testcontainers.utility.TestcontainersConfiguration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.ficsolution.roster.util.Util.employeeId;
import static com.ficsolution.roster.util.Util.timeOffRequestId;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(TestcontainersConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class TimeOffRequestRepositoryTest {

    @Autowired
    private TimeOffRequestRepository timeOffRequestRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Test
    void testCreateTimeOffRequest() {
        Employee employee = employeeRepository.findById(employeeId).get();

        TimeOffRequest timeOffRequest = new TimeOffRequest(
                timeOffRequestId,
                employee,
                LocalDate.now(),
                LocalDate.now(),
                "I'll move",
                TimeOffRequestType.PERSONAL,
                RequestStatus.PENDING,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        TimeOffRequest savedTimeOffRequest = timeOffRequestRepository.save(timeOffRequest);

        assertNotNull(savedTimeOffRequest);
        assertEquals(RequestStatus.PENDING, savedTimeOffRequest.getStatus());
        assertEquals(TimeOffRequestType.PERSONAL, savedTimeOffRequest.getType());
    }

    @Test
    void testRetrieveTimeOffRequestById() {
        Optional<TimeOffRequest> retrievedTimeOffRequest = timeOffRequestRepository.findById(timeOffRequestId);

        assertFalse(retrievedTimeOffRequest.isEmpty());
        assertEquals(timeOffRequestId, retrievedTimeOffRequest.get().getId());
        assertEquals(RequestStatus.APPROVED, retrievedTimeOffRequest.get().getStatus());
    }

    @Test
    void testRetrieveAllTimeOffRequests() {
        List<TimeOffRequest> timeOffRequests = timeOffRequestRepository.findAll();

        assertNotNull(timeOffRequests);
        assertEquals(4, timeOffRequests.size());
    }

    @Test
    void testRetrieveAllTimeOffRequestByEmployeeId() {
        List<TimeOffRequest> timeOffRequests = timeOffRequestRepository.findByEmployeeId(employeeId);

        assertNotNull(timeOffRequests);
        assertEquals(1, timeOffRequests.size());
        assertEquals(RequestStatus.PENDING, timeOffRequests.getFirst().getStatus());
    }

    @Test
    void testUpdateTimeOffRequest() {
        TimeOffRequest timeOffRequest = timeOffRequestRepository.findById(timeOffRequestId).get();

        TimeOffRequest newTimeOffRequest = new TimeOffRequest(
                timeOffRequestId,
                timeOffRequest.getEmployee(),
                timeOffRequest.getStartDate(),
                timeOffRequest.getEndDate(),
                timeOffRequest.getReason(),
                timeOffRequest.getType(),
                RequestStatus.REJECTED,
                timeOffRequest.getCreatedAt(),
                LocalDateTime.now()
        );

        TimeOffRequest updatedTimeOffRequest = timeOffRequestRepository.save(newTimeOffRequest);

        assertNotNull(updatedTimeOffRequest);
        assertEquals(RequestStatus.REJECTED, updatedTimeOffRequest.getStatus());
        assertNotEquals(updatedTimeOffRequest.getCreatedAt(), updatedTimeOffRequest.getUpdatedAt());
    }

    @Test
    void testDeleteTimeOffRequest() {
        TimeOffRequest timeOffRequest = timeOffRequestRepository.findById(timeOffRequestId).get();

        timeOffRequestRepository.delete(timeOffRequest);

        assertFalse(timeOffRequestRepository.findById(timeOffRequestId).isPresent());
    }
}
