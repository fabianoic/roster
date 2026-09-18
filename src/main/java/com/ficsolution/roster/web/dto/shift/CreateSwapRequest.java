package com.ficsolution.roster.web.dto.shift;

import com.ficsolution.roster.model.Employee;
import com.ficsolution.roster.model.Shift;
import com.ficsolution.roster.model.ShiftSwapRequest;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateSwapRequest(
        @NotNull UUID shiftId,
        @NotNull UUID requesterId,
        @NotNull UUID targetId
) {
    public ShiftSwapRequest toEntity() {
        return ShiftSwapRequest.builder()
                .shift(Shift.builder().id(shiftId).build())
                .requester(Employee.builder().id(requesterId).build())
                .target(Employee.builder().id(targetId).build())
                .build();
    }
}
