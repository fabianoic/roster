package com.ficsolution.roster.service;

import com.ficsolution.roster.exception.ObjectNotFoundException;
import com.ficsolution.roster.model.Employee;
import com.ficsolution.roster.model.TimeOffRequest;
import com.ficsolution.roster.repository.TimeOffRequestRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
public class TimeOffRequestService {

    private final TimeOffRequestRepository timeOffRequestRepository;
    private final EmployeeService employeeService;

    @Transactional
    public TimeOffRequest createTimeOffRequest(TimeOffRequest timeOffRequest) {
        Employee employee = employeeService.retrieveEmployeeById(timeOffRequest.getEmployee().getId());

        timeOffRequest.setEmployee(employee);

        return timeOffRequestRepository.save(timeOffRequest);
    }

    @Transactional(readOnly = true)
    public Page<TimeOffRequest> retrieveAllTimeOffRequests(Specification<TimeOffRequest> specification, Pageable pageable) {
        return timeOffRequestRepository.findAll(specification, pageable);
    }

    @Transactional(readOnly = true)
    public TimeOffRequest retrieveTimeOffRequestById(UUID timeOffRequestId) {
        return timeOffRequestRepository.findById(timeOffRequestId).orElseThrow(() -> new ObjectNotFoundException("Time off request", timeOffRequestId.toString()));
    }

    @Transactional
    public TimeOffRequest updateTimeOffRequest(UUID timeOffRequestId, TimeOffRequest newTimeOffRequest) {
        TimeOffRequest timeOffRequest = retrieveTimeOffRequestById(timeOffRequestId);

        timeOffRequest.setReason(newTimeOffRequest.getReason());
        timeOffRequest.setStatus(newTimeOffRequest.getStatus());
        timeOffRequest.setType(newTimeOffRequest.getType());
        timeOffRequest.setStartDate(newTimeOffRequest.getStartDate());
        timeOffRequest.setEndDate(newTimeOffRequest.getEndDate());
        timeOffRequest.setUpdatedAt(LocalDateTime.now());

        return timeOffRequestRepository.save(timeOffRequest);
    }

    @Transactional
    public void deleteTimeOffRequest(UUID timeOffRequestId) {
        TimeOffRequest timeOffRequest = retrieveTimeOffRequestById(timeOffRequestId);

        timeOffRequestRepository.delete(timeOffRequest);
    }
}
