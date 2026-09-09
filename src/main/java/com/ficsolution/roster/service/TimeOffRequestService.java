package com.ficsolution.roster.service;

import com.ficsolution.roster.exception.ObjectNotFoundException;
import com.ficsolution.roster.model.Employee;
import com.ficsolution.roster.model.TimeOffRequest;
import com.ficsolution.roster.repository.TimeOffRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class TimeOffRequestService {

    @Autowired
    private TimeOffRequestRepository timeOffRequestRepository;

    @Autowired
    private EmployeeService employeeService;

    @Transactional
    public TimeOffRequest createTimeOffRequest(TimeOffRequest timeOffRequest) {
        Employee employee = employeeService.retrieveEmployeeById(timeOffRequest.getEmployee().getId());

        timeOffRequest.setEmployee(employee);

        return timeOffRequestRepository.save(timeOffRequest);
    }

    @Transactional(readOnly = true)
    public List<TimeOffRequest> findAllTimeOffRequests() {
        return timeOffRequestRepository.findAll();
    }

    @Transactional(readOnly = true)
    public TimeOffRequest retrieveTimeOffRequestById(UUID timeOffRequestId) {
        return timeOffRequestRepository.findById(timeOffRequestId).orElseThrow(() -> new ObjectNotFoundException("Time off request", timeOffRequestId.toString()));
    }

    @Transactional(readOnly = true)
    public List<TimeOffRequest> retrieveAllTimeOffRequestsByEmployeeId(UUID employeeId) {
        return timeOffRequestRepository.findByEmployeeId(employeeId);
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
