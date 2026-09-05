package com.ficsolution.roster.service;

import com.ficsolution.roster.exception.ActionNotAllowedException;
import com.ficsolution.roster.exception.ConflictShiftException;
import com.ficsolution.roster.exception.ObjectNotFoundException;
import com.ficsolution.roster.model.Employee;
import com.ficsolution.roster.model.Shift;
import com.ficsolution.roster.model.ShiftSwapRequest;
import com.ficsolution.roster.model.enumModel.RequestStatus;
import com.ficsolution.roster.repository.ShiftSwapRequestRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.ficsolution.roster.util.Util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class ShiftSwapRequestServiceTest {

    @InjectMocks
    private ShiftSwapRequestService shiftSwapRequestService;

    @Mock
    private ShiftSwapRequestRepository shiftSwapRequestRepository;

    @Mock
    private EmployeeService employeeService;

    @Mock
    private ShiftService shiftService;

    @Test
    void testCreateShiftSwapRequest() {
        Shift shift = new Shift();
        shift.setId(shiftId);
        Employee requester = new Employee();
        requester.setId(employeeId);
        shift.setEmployee(requester);
        Employee target = new Employee();
        target.setId(employee1Id);
        ShiftSwapRequest shiftSwapRequest = new ShiftSwapRequest();
        shiftSwapRequest.setShift(shift);
        shiftSwapRequest.setRequester(requester);
        shiftSwapRequest.setTarget(target);
        when(shiftSwapRequestRepository.save(any(ShiftSwapRequest.class))).thenReturn(shiftSwapRequest);
        when(shiftService.retrieveShiftById(shiftId)).thenReturn(shift);
        when(employeeService.retrieveEmployeeById(employeeId)).thenReturn(requester);
        when(employeeService.retrieveEmployeeById(employee1Id)).thenReturn(target);

        ShiftSwapRequest savedShiftSwapRequest = shiftSwapRequestService.createShiftSwapRequest(shiftSwapRequest);

        assertNotNull(savedShiftSwapRequest);
        assertEquals(RequestStatus.PENDING, savedShiftSwapRequest.getStatus());
        assertEquals(savedShiftSwapRequest.getCreatedAt(), savedShiftSwapRequest.getUpdatedAt());
        verify(shiftSwapRequestRepository, times(1)).save(shiftSwapRequest);
        verify(shiftService, times(1)).retrieveShiftById(shiftId);
        verify(employeeService, times(2)).retrieveEmployeeById(any());
    }

    @Test
    void testCreateShiftSwapRequest_conflictShiftException() {
        Employee requester = new Employee();
        requester.setId(employeeId);
        Shift shift = new Shift();
        shift.setId(UUID.randomUUID());
        shift.setEmployee(requester);
        Employee target = new Employee();
        target.setId(employee1Id);
        ShiftSwapRequest shiftSwapRequest = new ShiftSwapRequest();
        shiftSwapRequest.setShift(shift);
        shiftSwapRequest.setRequester(target);

        when(shiftService.retrieveShiftById(any())).thenReturn(shift);

        assertThrows(ConflictShiftException.class, () -> shiftSwapRequestService.createShiftSwapRequest(shiftSwapRequest));
    }

    @Test
    void testRetrieveAllShiftSwapRequests() {
        List<ShiftSwapRequest> shiftSwapRequestList = List.of(
                new ShiftSwapRequest(),
                new ShiftSwapRequest(),
                new ShiftSwapRequest()
        );
        when(shiftSwapRequestRepository.findAll()).thenReturn(shiftSwapRequestList);

        List<ShiftSwapRequest> retrievedShiftSwapRequests = shiftSwapRequestService.retrieveAllShiftSwapRequests();

        assertNotNull(retrievedShiftSwapRequests);
        assertFalse(retrievedShiftSwapRequests.isEmpty());
        assertEquals(3, retrievedShiftSwapRequests.size());
    }

    @Test
    void testRetrieveShiftSwapRequestById() {
        Shift shift = new Shift();
        shift.setId(shiftId);
        Employee requester = new Employee();
        requester.setId(employeeId);
        shift.setEmployee(requester);
        Employee target = new Employee();
        target.setId(employee1Id);
        ShiftSwapRequest shiftSwapRequest = new ShiftSwapRequest();
        shiftSwapRequest.setShift(shift);
        shiftSwapRequest.setRequester(requester);
        shiftSwapRequest.setTarget(target);
        when(shiftSwapRequestRepository.findById(shiftSwapRequestId)).thenReturn(Optional.of(shiftSwapRequest));

        ShiftSwapRequest retrievedShiftSwapRequest = shiftSwapRequestService.retrieveShiftSwapRequestById(shiftSwapRequestId);

        assertNotNull(retrievedShiftSwapRequest);
        assertEquals(employeeId, retrievedShiftSwapRequest.getRequester().getId());
        assertEquals(employee1Id, retrievedShiftSwapRequest.getTarget().getId());
        assertEquals(shiftId, retrievedShiftSwapRequest.getShift().getId());
        verify(shiftSwapRequestRepository, times(1)).findById(shiftSwapRequestId);
    }

    @Test
    void testRetrieveShiftSwapRequestById_notFound() {
        when(shiftSwapRequestRepository.findById(shiftSwapRequestId)).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> shiftSwapRequestService.retrieveShiftSwapRequestById(shiftSwapRequestId));
        verify(shiftSwapRequestRepository, times(1)).findById(shiftSwapRequestId);
    }

    @Test
    void testShiftSwapRequestChangeStatus_pendingValidation() {
        Shift shift = new Shift();
        shift.setId(shiftId);
        Employee requester = new Employee();
        requester.setId(employee1Id);
        shift.setEmployee(requester);
        Employee target = new Employee();
        target.setId(employeeId);
        ShiftSwapRequest shiftSwapRequest = new ShiftSwapRequest();
        shiftSwapRequest.setShift(shift);
        shiftSwapRequest.setRequester(requester);
        shiftSwapRequest.setTarget(target);
        shiftSwapRequest.setStatus(RequestStatus.APPROVED);
        LocalDateTime updatedAt = LocalDateTime.now();
        shiftSwapRequest.setUpdatedAt(updatedAt);
        when(shiftSwapRequestRepository.findById(shiftSwapRequestId)).thenReturn(Optional.of(shiftSwapRequest));

        assertThrows(ActionNotAllowedException.class, () -> shiftSwapRequestService.changeStatus(shiftSwapRequestId, employeeId, RequestStatus.REJECTED));
        verify(shiftSwapRequestRepository, times(1)).findById(shiftSwapRequestId);
        verify(shiftService, times(0)).swapShiftEmployee(shiftId, employeeId);
        verify(shiftSwapRequestRepository, times(0)).save(shiftSwapRequest);
    }

    @Test
    void testShiftSwapRequestChangeToApproveStatus() {
        Shift shift = new Shift();
        shift.setId(shiftId);
        Employee requester = new Employee();
        requester.setId(employee1Id);
        shift.setEmployee(requester);
        Employee target = new Employee();
        target.setId(employeeId);
        ShiftSwapRequest shiftSwapRequest = new ShiftSwapRequest();
        shiftSwapRequest.setShift(shift);
        shiftSwapRequest.setRequester(requester);
        shiftSwapRequest.setTarget(target);
        shiftSwapRequest.setStatus(RequestStatus.PENDING);
        LocalDateTime updatedAt = LocalDateTime.now();
        shiftSwapRequest.setUpdatedAt(updatedAt);
        when(shiftSwapRequestRepository.findById(shiftSwapRequestId)).thenReturn(Optional.of(shiftSwapRequest));
        when(shiftService.swapShiftEmployee(shiftId, employeeId)).thenReturn(shift);
        when(shiftSwapRequestRepository.save(any(ShiftSwapRequest.class))).thenReturn(shiftSwapRequest);

        ShiftSwapRequest updatedShiftSwapRequest = shiftSwapRequestService.changeStatus(shiftSwapRequestId, employeeId, RequestStatus.APPROVED);

        assertNotNull(updatedShiftSwapRequest);
        assertEquals(RequestStatus.APPROVED, updatedShiftSwapRequest.getStatus());
        assertEquals(updatedAt, updatedShiftSwapRequest.getUpdatedAt());
        verify(shiftSwapRequestRepository, times(1)).findById(shiftSwapRequestId);
        verify(shiftService, times(1)).swapShiftEmployee(shiftId, employeeId);
    }

    @Test
    void testShiftSwapRequestChangeToRejectStatus() {
        Shift shift = new Shift();
        shift.setId(shiftId);
        Employee requester = new Employee();
        requester.setId(employee1Id);
        shift.setEmployee(requester);
        Employee target = new Employee();
        target.setId(employeeId);
        ShiftSwapRequest shiftSwapRequest = new ShiftSwapRequest();
        shiftSwapRequest.setShift(shift);
        shiftSwapRequest.setRequester(requester);
        shiftSwapRequest.setTarget(target);
        shiftSwapRequest.setStatus(RequestStatus.PENDING);
        LocalDateTime updatedAt = LocalDateTime.now();
        shiftSwapRequest.setUpdatedAt(updatedAt);
        when(shiftSwapRequestRepository.findById(shiftSwapRequestId)).thenReturn(Optional.of(shiftSwapRequest));
        when(shiftSwapRequestRepository.save(any(ShiftSwapRequest.class))).thenReturn(shiftSwapRequest);

        ShiftSwapRequest updatedShiftSwapRequest = shiftSwapRequestService.changeStatus(shiftSwapRequestId, employeeId, RequestStatus.REJECTED);

        assertNotNull(updatedShiftSwapRequest);
        assertEquals(RequestStatus.REJECTED, updatedShiftSwapRequest.getStatus());
        assertEquals(updatedAt, updatedShiftSwapRequest.getUpdatedAt());
        verify(shiftSwapRequestRepository, times(1)).findById(shiftSwapRequestId);
        verify(shiftService, times(0)).swapShiftEmployee(shiftId, employeeId);
    }

    @Test
    void testDeleteShiftSwapRequest() {
        ShiftSwapRequest shiftSwapRequest = new ShiftSwapRequest();
        shiftSwapRequest.setId(shiftSwapRequestId);
        shiftSwapRequest.setStatus(RequestStatus.PENDING);

        when(shiftSwapRequestRepository.findById(shiftSwapRequestId)).thenReturn(Optional.of(shiftSwapRequest));

        shiftSwapRequestService.deleteShiftSwapRequest(shiftSwapRequestId);

        verify(shiftSwapRequestRepository, times(1)).findById(shiftSwapRequestId);
        verify(shiftSwapRequestRepository, times(1)).delete(shiftSwapRequest);
    }

    @Test
    void testDeleteShiftSwapRequest_pendingValidation() {
        ShiftSwapRequest shiftSwapRequest = new ShiftSwapRequest();
        shiftSwapRequest.setId(shiftSwapRequestId);
        shiftSwapRequest.setStatus(RequestStatus.APPROVED);

        when(shiftSwapRequestRepository.findById(shiftSwapRequestId)).thenReturn(Optional.of(shiftSwapRequest));

        assertThrows(ActionNotAllowedException.class, () -> shiftSwapRequestService.deleteShiftSwapRequest(shiftSwapRequestId));

        verify(shiftSwapRequestRepository, times(1)).findById(shiftSwapRequestId);
        verify(shiftSwapRequestRepository, times(0)).delete(shiftSwapRequest);
    }
}
