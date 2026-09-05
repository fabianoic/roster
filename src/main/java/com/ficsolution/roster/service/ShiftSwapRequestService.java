package com.ficsolution.roster.service;

import com.ficsolution.roster.exception.ActionNotAllowedException;
import com.ficsolution.roster.exception.ConflictShiftException;
import com.ficsolution.roster.exception.EmployeeHasNotPermissionException;
import com.ficsolution.roster.exception.ObjectNotFoundException;
import com.ficsolution.roster.model.Employee;
import com.ficsolution.roster.model.Shift;
import com.ficsolution.roster.model.ShiftSwapRequest;
import com.ficsolution.roster.model.enumModel.RequestStatus;
import com.ficsolution.roster.repository.ShiftSwapRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ShiftSwapRequestService {

    @Autowired
    private ShiftSwapRequestRepository shiftSwapRequestRepository;

    @Autowired
    private ShiftService shiftService;

    @Autowired
    private EmployeeService employeeService;

    public ShiftSwapRequest createShiftSwapRequest(ShiftSwapRequest shiftSwapRequest) {
        Shift shift = shiftService.retrieveShiftById(shiftSwapRequest.getShift().getId());

        if (!shift.getEmployee().getId().equals(shiftSwapRequest.getRequester().getId())) {
            throw new ConflictShiftException("This shift is not from the requester employee. id: " + shift.getId());
        }

        Employee requester = employeeService.retrieveEmployeeById(shiftSwapRequest.getRequester().getId());
        Employee target = employeeService.retrieveEmployeeById(shiftSwapRequest.getTarget().getId());

        shiftSwapRequest.setStatus(RequestStatus.PENDING);
        shiftSwapRequest.setRequester(requester);
        shiftSwapRequest.setTarget(target);
        shiftSwapRequest.setShift(shift);
        shiftSwapRequest.setCreatedAt(LocalDateTime.now());
        shiftSwapRequest.setUpdatedAt(LocalDateTime.now());

        return shiftSwapRequestRepository.save(shiftSwapRequest);
    }

    public List<ShiftSwapRequest> retrieveAllShiftSwapRequests() {
        return shiftSwapRequestRepository.findAll();
    }

    public ShiftSwapRequest retrieveShiftSwapRequestById(UUID shiftSwapRequestId) {
        return shiftSwapRequestRepository.findById(shiftSwapRequestId).orElseThrow(() -> new ObjectNotFoundException("Shift swap request not found, id: " + shiftSwapRequestId));
    }

    public ShiftSwapRequest changeStatus(UUID shiftSwapRequestId, UUID employeeId, RequestStatus status) {
        ShiftSwapRequest shiftSwapRequest = retrieveShiftSwapRequestById(shiftSwapRequestId);

        if (!shiftSwapRequest.getTarget().getId().equals(employeeId)) {
            throw new EmployeeHasNotPermissionException("Change status failed, requester hasn't permission!");
        }

        shiftService.swapShiftEmployee(shiftSwapRequest.getShift().getId(), employeeId);

        shiftSwapRequest.setStatus(status);

        return shiftSwapRequestRepository.save(shiftSwapRequest);
    }

    public void deleteShiftSwapRequest(UUID shiftSwapRequestId) {
        ShiftSwapRequest shiftSwapRequest = retrieveShiftSwapRequestById(shiftSwapRequestId);

        if (shiftSwapRequest.getStatus().equals(RequestStatus.PENDING)) {
            shiftSwapRequestRepository.delete(shiftSwapRequest);
            return;
        }

        throw new ActionNotAllowedException("Delete a shift swap request that isn't pending it's not allowed.");
    }
}
