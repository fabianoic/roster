package com.ficsolution.roster.service;

import com.ficsolution.roster.exception.TimeOffRequestNotFoundException;
import com.ficsolution.roster.model.Employee;
import com.ficsolution.roster.model.TimeOffRequest;
import com.ficsolution.roster.model.enumModel.TimeOffRequestStatus;
import com.ficsolution.roster.model.enumModel.TimeOffRequestType;
import com.ficsolution.roster.repository.TimeOffRequestRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.ficsolution.roster.util.Util.employeeId;
import static com.ficsolution.roster.util.Util.timeOffRequestId;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TimeOffRequestServiceTest {

    @InjectMocks
    private TimeOffRequestService timeOffRequestService;

    @Mock
    private TimeOffRequestRepository timeOffRequestRepository;

    @Mock
    private EmployeeService employeeService;

    private static TimeOffRequest timeOffRequest;

    @BeforeAll
    static void setUp() {
        Employee employee = new Employee();
        employee.setId(employeeId);
        timeOffRequest = new TimeOffRequest(
                timeOffRequestId,
                employee,
                LocalDate.now(),
                LocalDate.now(),
                "I'll move",
                TimeOffRequestType.PERSONAL,
                TimeOffRequestStatus.PENDING,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    @Test
    void testCreateTimeOffRequest() {
        when(employeeService.retrieveEmployeeById(employeeId)).thenReturn(timeOffRequest.getEmployee());
        when(timeOffRequestRepository.save(any(TimeOffRequest.class))).thenReturn(timeOffRequest);

        TimeOffRequest savedTimeOffRequest = timeOffRequestService.createTimeOffRequest(timeOffRequest);

        assertNotNull(savedTimeOffRequest);
        assertEquals(TimeOffRequestType.PERSONAL, savedTimeOffRequest.getType());
        assertEquals(TimeOffRequestStatus.PENDING, savedTimeOffRequest.getStatus());
        verify(timeOffRequestRepository, times(1)).save(timeOffRequest);
    }

    @Test
    void testRetrieveAllTimeOffRequests() {
        List<TimeOffRequest> timeOffRequestList = List.of(
                timeOffRequest,
                new TimeOffRequest(
                        UUID.randomUUID(),
                        timeOffRequest.getEmployee(),
                        LocalDate.now(),
                        LocalDate.now(),
                        "I'll move again",
                        TimeOffRequestType.PERSONAL,
                        TimeOffRequestStatus.PENDING,
                        LocalDateTime.now().plusDays(1),
                        LocalDateTime.now().plusDays(1)
                )
        );
        when(timeOffRequestRepository.findAll()).thenReturn(timeOffRequestList);

        List<TimeOffRequest> retrieveAllTimeOffRequests = timeOffRequestService.findAllTimeOffRequests();

        assertNotNull(retrieveAllTimeOffRequests);
        assertEquals(2, retrieveAllTimeOffRequests.size());
        verify(timeOffRequestRepository, times(1)).findAll();
    }

    @Test
    void testRetrieveTimeOffRequestById() {
        when(timeOffRequestRepository.findById(timeOffRequestId)).thenReturn(Optional.of(timeOffRequest));

        TimeOffRequest retrievedTimeOffRequest = timeOffRequestService.retrieveTimeOffRequestById(timeOffRequestId);

        assertNotNull(retrievedTimeOffRequest);
        assertEquals(TimeOffRequestType.PERSONAL, retrievedTimeOffRequest.getType());
        assertEquals(TimeOffRequestStatus.PENDING, retrievedTimeOffRequest.getStatus());
        verify(timeOffRequestRepository, times(1)).findById(timeOffRequestId);
    }

    @Test
    void testRetrieveTimeOffRequestById_notFound() {
        when(timeOffRequestRepository.findById(timeOffRequestId)).thenReturn(Optional.empty());

        assertThrows(TimeOffRequestNotFoundException.class, () -> timeOffRequestService.retrieveTimeOffRequestById(timeOffRequestId));
    }

    @Test
    void testRetrieveAllTimeOffRequestByEmployeeId() {
        when(timeOffRequestRepository.findByEmployeeId(timeOffRequest.getEmployee().getId())).thenReturn(List.of(timeOffRequest));

        List<TimeOffRequest> retrievedAllTimeOffRequests = timeOffRequestService.retrieveAllTimeOffRequestsByEmployeeId(employeeId);

        assertNotNull(retrievedAllTimeOffRequests);
        assertEquals(1, retrievedAllTimeOffRequests.size());
        verify(timeOffRequestRepository, times(1)).findByEmployeeId(timeOffRequest.getEmployee().getId());
    }

    @Test
    void testUpdateTimeOffRequest() {
        when(timeOffRequestRepository.findById(timeOffRequestId)).thenReturn(Optional.of(timeOffRequest));
        TimeOffRequest newTimeOffRequest = new TimeOffRequest(
                timeOffRequestId,
                timeOffRequest.getEmployee(),
                timeOffRequest.getStartDate(),
                timeOffRequest.getEndDate(),
                "new reason",
                TimeOffRequestType.PERSONAL,
                TimeOffRequestStatus.PENDING,
                timeOffRequest.getCreatedAt(),
                LocalDateTime.now().plusDays(1)
        );
        when(timeOffRequestRepository.save(any(TimeOffRequest.class))).thenReturn(newTimeOffRequest);

        TimeOffRequest updatedTimeOffRequest = timeOffRequestService.updateTimeOffRequest(timeOffRequestId, newTimeOffRequest);

        assertNotNull(updatedTimeOffRequest);
        assertNotEquals(timeOffRequest.getCreatedAt(), updatedTimeOffRequest.getUpdatedAt());
        verify(timeOffRequestRepository, times(1)).findById(timeOffRequestId);
        verify(timeOffRequestRepository, times(1)).save(any());
    }

    @Test
    void testDeleteTimeOffRequest() {
        when(timeOffRequestRepository.findById(timeOffRequestId)).thenReturn(Optional.of(timeOffRequest));

        timeOffRequestService.deleteTimeOffRequest(timeOffRequestId);

        verify(timeOffRequestRepository, times(1)).findById(timeOffRequestId);
        verify(timeOffRequestRepository, times(1)).delete(timeOffRequest);
    }
}
