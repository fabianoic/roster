package com.ficsolution.roster.web.controller;

import com.ficsolution.roster.model.Availability;
import com.ficsolution.roster.repository.specification.AvailabilitySpecification;
import com.ficsolution.roster.security.Permissions;
import com.ficsolution.roster.security.SecurityUtil;
import com.ficsolution.roster.service.AvailabilityService;
import com.ficsolution.roster.web.dto.availability.AvailabilityFilter;
import com.ficsolution.roster.web.dto.availability.AvailabilityResponse;
import com.ficsolution.roster.web.dto.availability.CreateAvailabilityRequest;
import com.ficsolution.roster.web.dto.availability.UpdateAvailabilityRequest;
import com.ficsolution.roster.web.dto.common.PagedResponse;
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
@RequestMapping("/availabilities")
@RequiredArgsConstructor
public class AvailabilityController {

    private final AvailabilityService availabilityService;

    @PostMapping
    public ResponseEntity<AvailabilityResponse> createAvailability(@Valid @RequestBody CreateAvailabilityRequest createAvailabilityRequest) {
        SecurityUtil.requireOwnershipOrPermission(createAvailabilityRequest.employeeId(), Permissions.AVAILABILITY_ANY);
        Availability availability = availabilityService.createAvailability(createAvailabilityRequest.toEntity());
        AvailabilityResponse response = AvailabilityResponse.from(availability);
        return ResponseEntity.created(URI.create(String.format("/availabilities/%s", response.id()))).body(response);
    }

    @GetMapping
    public ResponseEntity<PagedResponse<AvailabilityResponse>> retrieveAllAvailabilities(@ModelAttribute AvailabilityFilter filter, @PageableDefault(sort = {"weekday", "startTime"}) Pageable pageable) {
        // Without AVAILABILITY_ANY the listing must be scoped to the logged employee
        if (filter.employeeId() == null) {
            SecurityUtil.requirePermission(Permissions.AVAILABILITY_ANY);
        } else {
            SecurityUtil.requireOwnershipOrPermission(filter.employeeId(), Permissions.AVAILABILITY_ANY);
        }
        Specification<Availability> specification = Specification.allOf(
                AvailabilitySpecification.hasEmployeeId(filter.employeeId()),
                AvailabilitySpecification.hasWeekday(filter.weekday()),
                AvailabilitySpecification.isAvailable(filter.available())
        );
        Page<Availability> availabilities = availabilityService.retrieveAllAvailabilities(specification, pageable);
        return ResponseEntity.ok(PagedResponse.from(availabilities.map(AvailabilityResponse::from)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AvailabilityResponse> retrieveAvailabilityById(@PathVariable UUID id) {
        Availability availability = availabilityService.retrieveAvailabilityById(id);
        SecurityUtil.requireOwnershipOrPermission(availability.getEmployee().getId(), Permissions.AVAILABILITY_ANY);
        return ResponseEntity.ok(AvailabilityResponse.from(availability));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AvailabilityResponse> updateAvailability(@PathVariable UUID id, @Valid @RequestBody UpdateAvailabilityRequest updateAvailabilityRequest) {
        Availability existing = availabilityService.retrieveAvailabilityById(id);
        SecurityUtil.requireOwnershipOrPermission(existing.getEmployee().getId(), Permissions.AVAILABILITY_ANY);
        Availability availability = availabilityService.updateAvailability(id, updateAvailabilityRequest.toEntity());
        return ResponseEntity.ok(AvailabilityResponse.from(availability));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAvailability(@PathVariable UUID id) {
        Availability existing = availabilityService.retrieveAvailabilityById(id);
        SecurityUtil.requireOwnershipOrPermission(existing.getEmployee().getId(), Permissions.AVAILABILITY_ANY);
        availabilityService.deleteAvailability(id);
        return ResponseEntity.noContent().build();
    }
}
