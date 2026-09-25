package com.ficsolution.roster.web.controller;

import com.ficsolution.roster.model.ShiftSwapRequest;
import com.ficsolution.roster.security.Permissions;
import com.ficsolution.roster.security.SecurityUtil;
import com.ficsolution.roster.service.ShiftSwapRequestService;
import com.ficsolution.roster.web.dto.shift.CreateSwapRequest;
import com.ficsolution.roster.web.dto.shift.ShiftSwapResponse;
import com.ficsolution.roster.web.dto.shift.UpdateShiftSwap;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Set;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ShiftSwapRequestController {

    private final ShiftSwapRequestService shiftSwapRequestService;

    @PostMapping("/shifts/{id}/swap-requests")
    public ResponseEntity<ShiftSwapResponse> createSwapShift(@PathVariable UUID id, @Valid @RequestBody CreateSwapRequest swapRequest) {
        SecurityUtil.requireOwnershipOrPermission(swapRequest.requesterId(), Permissions.SWAP_REQUEST_ANY);
        ShiftSwapRequest shiftSwapRequest = swapRequest.toEntity();
        shiftSwapRequest = shiftSwapRequestService.createShiftSwapRequest(shiftSwapRequest);
        return ResponseEntity.created(URI.create("/swap-requests/".concat(shiftSwapRequest.getId().toString()))).body(ShiftSwapResponse.from(shiftSwapRequest));
    }

    @PutMapping("/swap-requests/{id}")
    public ResponseEntity<ShiftSwapResponse> updateShiftSwapRequest(@PathVariable UUID id, @Valid @RequestBody UpdateShiftSwap updateShiftSwap) {
        SecurityUtil.requireOwnershipOrPermission(updateShiftSwap.targetId(), Permissions.SWAP_REQUEST_ANY);
        ShiftSwapRequest shiftSwapRequest = shiftSwapRequestService.changeStatus(id, updateShiftSwap.targetId(), updateShiftSwap.status());
        return ResponseEntity.ok(ShiftSwapResponse.from(shiftSwapRequest));
    }

    @GetMapping("/swap-requests/{id}")
    public ResponseEntity<ShiftSwapResponse> retrieveShiftSwapRequestById(@PathVariable UUID id) {
        ShiftSwapRequest shiftSwapRequest = shiftSwapRequestService.retrieveShiftSwapRequestById(id);
        SecurityUtil.requireOwnershipOrPermission(
                Set.of(shiftSwapRequest.getRequester().getId(), shiftSwapRequest.getTarget().getId()),
                Permissions.SWAP_REQUEST_ANY);
        return ResponseEntity.ok(ShiftSwapResponse.from(shiftSwapRequest));
    }

    @DeleteMapping("/swap-requests/{id}")
    public ResponseEntity<Void> deleteShiftSwapRequest(@PathVariable UUID id) {
        ShiftSwapRequest shiftSwapRequest = shiftSwapRequestService.retrieveShiftSwapRequestById(id);
        SecurityUtil.requireOwnershipOrPermission(shiftSwapRequest.getRequester().getId(), Permissions.SWAP_REQUEST_ANY);
        shiftSwapRequestService.deleteShiftSwapRequest(id);
        return ResponseEntity.noContent().build();
    }
}
