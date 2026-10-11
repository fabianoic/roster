package com.ficsolution.roster.web.controller;

import com.ficsolution.roster.model.TimeOffRequest;
import com.ficsolution.roster.repository.specification.TimeOffRequestSpecification;
import com.ficsolution.roster.security.Permissions;
import com.ficsolution.roster.security.SecurityUtil;
import com.ficsolution.roster.service.TimeOffRequestService;
import com.ficsolution.roster.web.dto.common.PagedResponse;
import com.ficsolution.roster.web.dto.timeoff.CreateTimeOffRequest;
import com.ficsolution.roster.web.dto.timeoff.TimeOffRequestFilter;
import com.ficsolution.roster.web.dto.timeoff.TimeOffRequestResponse;
import com.ficsolution.roster.web.dto.timeoff.UpdateTimeOffRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/time-off-requests")
@RequiredArgsConstructor
public class TimeOffRequestController {

    private final TimeOffRequestService timeOffRequestService;

    @PostMapping
    public ResponseEntity<TimeOffRequestResponse> createTimeOffRequest(@Valid @RequestBody CreateTimeOffRequest createTimeOffRequest) {
        SecurityUtil.requireOwnershipOrPermission(createTimeOffRequest.employeeId(), Permissions.TIME_OFF_ANY);
        TimeOffRequest timeOffRequest = timeOffRequestService.createTimeOffRequest(createTimeOffRequest.toEntity());
        TimeOffRequestResponse response = TimeOffRequestResponse.from(timeOffRequest);
        return ResponseEntity.created(URI.create(String.format("/time-off-requests/%s", response.id()))).body(response);
    }

    @GetMapping
    public ResponseEntity<PagedResponse<TimeOffRequestResponse>> retrieveAllTimeOffRequests(@ModelAttribute TimeOffRequestFilter filter, @PageableDefault(sort = "startDate") Pageable pageable) {
        // Without TIME_OFF_ANY the listing must be scoped to the logged employee
        if (filter.employeeId() == null) {
            SecurityUtil.requirePermission(Permissions.TIME_OFF_ANY);
        } else {
            SecurityUtil.requireOwnershipOrPermission(filter.employeeId(), Permissions.TIME_OFF_ANY);
        }
        Specification<TimeOffRequest> specification = Specification.allOf(
                TimeOffRequestSpecification.hasEmployeeId(filter.employeeId()),
                TimeOffRequestSpecification.hasStatus(filter.status()),
                TimeOffRequestSpecification.hasType(filter.type()),
                TimeOffRequestSpecification.overlapsPeriod(filter.start(), filter.end())
        );
        Page<TimeOffRequest> timeOffRequests = timeOffRequestService.retrieveAllTimeOffRequests(specification, pageable);
        return ResponseEntity.ok(PagedResponse.from(timeOffRequests.map(TimeOffRequestResponse::from)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TimeOffRequestResponse> retrieveTimeOffRequestById(@PathVariable UUID id) {
        TimeOffRequest timeOffRequest = timeOffRequestService.retrieveTimeOffRequestById(id);
        SecurityUtil.requireOwnershipOrPermission(timeOffRequest.getEmployee().getId(), Permissions.TIME_OFF_ANY);
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
        SecurityUtil.requireOwnershipOrPermission(existing.getEmployee().getId(), Permissions.TIME_OFF_ANY);
        timeOffRequestService.deleteTimeOffRequest(id);
        return ResponseEntity.noContent().build();
    }
}
