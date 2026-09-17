package com.ficsolution.roster.web.controller;

import com.ficsolution.roster.model.Shift;
import com.ficsolution.roster.service.ShiftService;
import com.ficsolution.roster.specification.ShiftSpecification;
import com.ficsolution.roster.web.dto.common.PagedResponse;
import com.ficsolution.roster.web.dto.shift.CreateShiftRequest;
import com.ficsolution.roster.web.dto.shift.ShiftFilter;
import com.ficsolution.roster.web.dto.shift.ShiftResponse;
import com.ficsolution.roster.web.dto.shift.UpdateShiftRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/shifts")
@RequiredArgsConstructor
public class ShiftController {

    private final ShiftService shiftService;

    @PostMapping
    public ResponseEntity<ShiftResponse> createShift(@Valid @RequestBody CreateShiftRequest shiftRequest) {
        Shift shift = shiftRequest.toEntity();
        shift = shiftService.createShift(shift);
        return ResponseEntity.created(URI.create(String.format("/shifts/%s", shift.getId())))
                .body(ShiftResponse.from(shift));
    }

    @GetMapping
    public ResponseEntity<PagedResponse<ShiftResponse>> retrieveAllShifts(@ModelAttribute ShiftFilter filter, Pageable pageable) {
        Specification<Shift> specification = Specification.allOf(
                ShiftSpecification.hasEmployeeId(filter.employeeId()),
                ShiftSpecification.hasStoreId(filter.storeId()),
                ShiftSpecification.shiftDateBetween(filter.start(), filter.end())
        );
        Page<Shift> shifts = shiftService.retrieveAllShifts(specification, pageable);
        Page<ShiftResponse> responses = shifts.map(ShiftResponse::from);
        return ResponseEntity.ok(new PagedResponse<>(
                responses.getContent(),
                responses.getNumber(),
                responses.getSize(),
                responses.getTotalElements(),
                responses.getTotalPages()
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShiftResponse> retrieveShiftById(@PathVariable UUID id) {
        Shift shift = shiftService.retrieveShiftById(id);
        ShiftResponse shiftResponse = ShiftResponse.from(shift);
        return ResponseEntity.ok(shiftResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ShiftResponse> updateShiftInfo(@PathVariable UUID id, @Valid @RequestBody UpdateShiftRequest updateShiftRequest) {
        Shift shift = updateShiftRequest.toEntity();
        shift = shiftService.updateShiftInfo(id, shift);
        return ResponseEntity.ok(ShiftResponse.from(shift));
    }
}
