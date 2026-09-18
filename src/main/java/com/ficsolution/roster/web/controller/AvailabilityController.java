package com.ficsolution.roster.web.controller;

import com.ficsolution.roster.model.Availability;
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
        Availability availability = availabilityService.createAvailability(createAvailabilityRequest.toEntity());
        AvailabilityResponse response = AvailabilityResponse.from(availability);
        return ResponseEntity.created(URI.create(String.format("/availabilities/%s", response.id()))).body(response);
    }

    @GetMapping
    public ResponseEntity<List<AvailabilityResponse>> retrieveAllAvailabilities() {
        List<AvailabilityResponse> responses = availabilityService.findAllAvailabilities().stream()
                .map(AvailabilityResponse::from)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping(params = "employeeId")
    public ResponseEntity<List<AvailabilityResponse>> retrieveAvailabilitiesByEmployeeId(@RequestParam UUID employeeId) {
        List<AvailabilityResponse> responses = availabilityService.retrieveAllAvailabilityByEmployeeId(employeeId).stream()
                .map(AvailabilityResponse::from)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AvailabilityResponse> retrieveAvailabilityById(@PathVariable UUID id) {
        return ResponseEntity.ok(AvailabilityResponse.from(availabilityService.retrieveAvailabilityById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AvailabilityResponse> updateAvailability(@PathVariable UUID id, @Valid @RequestBody UpdateAvailabilityRequest updateAvailabilityRequest) {
        Availability availability = availabilityService.updateAvailability(id, updateAvailabilityRequest.toEntity());
        return ResponseEntity.ok(AvailabilityResponse.from(availability));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAvailability(@PathVariable UUID id) {
        availabilityService.deleteAvailability(id);
        return ResponseEntity.noContent().build();
    }
}
