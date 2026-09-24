package com.ficsolution.roster.web.controller;

import com.ficsolution.roster.model.TimeOffRequest;
import com.ficsolution.roster.security.SecurityUtil;
import com.ficsolution.roster.service.TimeOffRequestService;
import com.ficsolution.roster.web.dto.timeoff.CreateTimeOffRequest;
import com.ficsolution.roster.web.dto.timeoff.TimeOffRequestResponse;
import com.ficsolution.roster.web.dto.timeoff.UpdateTimeOffRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/time-off-requests")
@RequiredArgsConstructor
public class TimeOffRequestController {

    private final TimeOffRequestService timeOffRequestService;

    @PostMapping
    public ResponseEntity<TimeOffRequestResponse> createTimeOffRequest(@Valid @RequestBody CreateTimeOffRequest createTimeOffRequest) {
        SecurityUtil.requireOwnershipOrRole(createTimeOffRequest.employeeId(), "MANAGER", "SUPERVISOR");
        TimeOffRequest timeOffRequest = timeOffRequestService.createTimeOffRequest(createTimeOffRequest.toEntity());
        TimeOffRequestResponse response = TimeOffRequestResponse.from(timeOffRequest);
        return ResponseEntity.created(URI.create(String.format("/time-off-requests/%s", response.id()))).body(response);
    }

    @GetMapping
    public ResponseEntity<List<TimeOffRequestResponse>> retrieveAllTimeOffRequests() {
        SecurityUtil.requireRole("MANAGER", "SUPERVISOR");
        List<TimeOffRequestResponse> responses = timeOffRequestService.findAllTimeOffRequests().stream()
                .map(TimeOffRequestResponse::from)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping(params = "employeeId")
    public ResponseEntity<List<TimeOffRequestResponse>> retrieveTimeOffRequestsByEmployeeId(@RequestParam UUID employeeId) {
        SecurityUtil.requireOwnershipOrRole(employeeId, "MANAGER", "SUPERVISOR");
        List<TimeOffRequestResponse> responses = timeOffRequestService.retrieveAllTimeOffRequestsByEmployeeId(employeeId).stream()
                .map(TimeOffRequestResponse::from)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TimeOffRequestResponse> retrieveTimeOffRequestById(@PathVariable UUID id) {
        TimeOffRequest timeOffRequest = timeOffRequestService.retrieveTimeOffRequestById(id);
        SecurityUtil.requireOwnershipOrRole(timeOffRequest.getEmployee().getId(), "MANAGER", "SUPERVISOR");
        return ResponseEntity.ok(TimeOffRequestResponse.from(timeOffRequest));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TimeOffRequestResponse> updateTimeOffRequest(@PathVariable UUID id, @Valid @RequestBody UpdateTimeOffRequest updateTimeOffRequest) {
        TimeOffRequest timeOffRequest = timeOffRequestService.updateTimeOffRequest(id, updateTimeOffRequest.toEntity());
        return ResponseEntity.ok(TimeOffRequestResponse.from(timeOffRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTimeOffRequest(@PathVariable UUID id) {
        TimeOffRequest existing = timeOffRequestService.retrieveTimeOffRequestById(id);
        SecurityUtil.requireOwnershipOrRole(existing.getEmployee().getId(), "MANAGER", "SUPERVISOR");
        timeOffRequestService.deleteTimeOffRequest(id);
        return ResponseEntity.noContent().build();
    }
}
