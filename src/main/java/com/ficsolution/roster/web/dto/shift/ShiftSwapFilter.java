package com.ficsolution.roster.web.dto.shift;

import com.ficsolution.roster.model.enums.RequestStatus;

import java.util.UUID;

public record ShiftSwapFilter(
        UUID shiftId,
        UUID requesterId,
        UUID targetId,
        RequestStatus status
) {
}
