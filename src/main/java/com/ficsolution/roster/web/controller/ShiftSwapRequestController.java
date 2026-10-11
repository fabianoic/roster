package com.ficsolution.roster.web.controller;

import com.ficsolution.roster.model.ShiftSwapRequest;
import com.ficsolution.roster.repository.specification.ShiftSwapRequestSpecification;
import com.ficsolution.roster.security.Permissions;
import com.ficsolution.roster.security.SecurityUtil;
import com.ficsolution.roster.service.ShiftSwapRequestService;
import com.ficsolution.roster.web.dto.common.PagedResponse;
import com.ficsolution.roster.web.dto.shift.CreateSwapRequest;
import com.ficsolution.roster.web.dto.shift.ShiftSwapFilter;
import com.ficsolution.roster.web.dto.shift.ShiftSwapResponse;
import com.ficsolution.roster.web.dto.shift.UpdateShiftSwap;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

@RestController
@RequiredArgsConstructor
public class ShiftSwapRequestController {

    private final ShiftSwapRequestService shiftSwapRequestService;

    @PostMapping("/shifts/{id}/swap-requests")
    public ResponseEntity<ShiftSwapResponse> createSwapShift(@PathVariable UUID id, @Valid @RequestBody CreateSwapRequest swapRequest) {
        SecurityUtil.requireOwnershipOrPermission(swapRequest.requesterId(), Permissions.SWAP_REQUEST_ANY);
        ShiftSwapRequest shiftSwapRequest = swapRequest.toEntity(id);
        shiftSwapRequest = shiftSwapRequestService.createShiftSwapRequest(shiftSwapRequest);
        return ResponseEntity.created(URI.create("/swap-requests/".concat(shiftSwapRequest.getId().toString()))).body(ShiftSwapResponse.from(shiftSwapRequest));
    }

    @PutMapping("/swap-requests/{id}")
    public ResponseEntity<ShiftSwapResponse> updateShiftSwapRequest(@PathVariable UUID id, @Valid @RequestBody UpdateShiftSwap updateShiftSwap) {
        // Only the target may accept/reject; the service validates it against the logged employee
        ShiftSwapRequest shiftSwapRequest = shiftSwapRequestService.changeStatus(id, SecurityUtil.currentEmployeeId(), updateShiftSwap.status());
        return ResponseEntity.ok(ShiftSwapResponse.from(shiftSwapRequest));
    }

    @GetMapping("/swap-requests")
    public ResponseEntity<PagedResponse<ShiftSwapResponse>> retrieveAllShiftSwapRequests(@ModelAttribute ShiftSwapFilter filter, @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        // Without SWAP_REQUEST_ANY the listing must be scoped to requests where the logged employee is requester or target
        List<UUID> ownerIds = Stream.of(filter.requesterId(), filter.targetId()).filter(Objects::nonNull).toList();
        SecurityUtil.requireOwnershipOrPermission(ownerIds, Permissions.SWAP_REQUEST_ANY);
        Specification<ShiftSwapRequest> specification = Specification.allOf(
                ShiftSwapRequestSpecification.hasShiftId(filter.shiftId()),
                ShiftSwapRequestSpecification.hasRequesterId(filter.requesterId()),
                ShiftSwapRequestSpecification.hasTargetId(filter.targetId()),
                ShiftSwapRequestSpecification.hasStatus(filter.status())
        );
        Page<ShiftSwapRequest> shiftSwapRequests = shiftSwapRequestService.retrieveAllShiftSwapRequests(specification, pageable);
        return ResponseEntity.ok(PagedResponse.from(shiftSwapRequests.map(ShiftSwapResponse::from)));
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
