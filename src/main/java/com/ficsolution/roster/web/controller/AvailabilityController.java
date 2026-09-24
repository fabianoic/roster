package com.ficsolution.roster.web.controller;

import com.ficsolution.roster.model.Availability;
import com.ficsolution.roster.security.SecurityUtil;
import com.ficsolution.roster.service.AvailabilityService;
import com.ficsolution.roster.web.dto.availability.AvailabilityResponse;
import com.ficsolution.roster.web.dto.availability.CreateAvailabilityRequest;
import com.ficsolution.roster.web.dto.availability.UpdateAvailabilityRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/availabilities")
@RequiredArgsConstructor
public class AvailabilityController {

    private final AvailabilityService availabilityService;

    @PostMapping
    public ResponseEntity<AvailabilityResponse> createAvailability(@Valid @RequestBody CreateAvailabilityRequest createAvailabilityRequest) {
        SecurityUtil.requireOwnershipOrRole(createAvailabilityRequest.employeeId(), "MANAGER", "SUPERVISOR");
        Availability availability = availabilityService.createAvailability(createAvailabilityRequest.toEntity());
        AvailabilityResponse response = AvailabilityResponse.from(availability);
        return ResponseEntity.created(URI.create(String.format("/availabilities/%s", response.id()))).body(response);
    }

    @GetMapping
    public ResponseEntity<List<AvailabilityResponse>> retrieveAllAvailabilities() {
        SecurityUtil.requireRole("MANAGER", "SUPERVISOR");
        List<AvailabilityResponse> responses = availabilityService.findAllAvailabilities().stream()
                .map(AvailabilityResponse::from)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping(params = "employeeId")
    public ResponseEntity<List<AvailabilityResponse>> retrieveAvailabilitiesByEmployeeId(@RequestParam UUID employeeId) {
        SecurityUtil.requireOwnershipOrRole(employeeId, "MANAGER", "SUPERVISOR");
        List<AvailabilityResponse> responses = availabilityService.retrieveAllAvailabilityByEmployeeId(employeeId).stream()
                .map(AvailabilityResponse::from)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AvailabilityResponse> retrieveAvailabilityById(@PathVariable UUID id) {
        Availability availability = availabilityService.retrieveAvailabilityById(id);
        SecurityUtil.requireOwnershipOrRole(availability.getEmployee().getId(), "MANAGER", "SUPERVISOR");
        return ResponseEntity.ok(AvailabilityResponse.from(availability));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AvailabilityResponse> updateAvailability(@PathVariable UUID id, @Valid @RequestBody UpdateAvailabilityRequest updateAvailabilityRequest) {
        Availability existing = availabilityService.retrieveAvailabilityById(id);
        SecurityUtil.requireOwnershipOrRole(existing.getEmployee().getId(), "MANAGER", "SUPERVISOR");
        Availability availability = availabilityService.updateAvailability(id, updateAvailabilityRequest.toEntity());
        return ResponseEntity.ok(AvailabilityResponse.from(availability));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAvailability(@PathVariable UUID id) {
        Availability existing = availabilityService.retrieveAvailabilityById(id);
        SecurityUtil.requireOwnershipOrRole(existing.getEmployee().getId(), "MANAGER", "SUPERVISOR");
        availabilityService.deleteAvailability(id);
        return ResponseEntity.noContent().build();
    }
}
