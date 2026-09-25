package com.ficsolution.roster.web.dto.shift;

import com.ficsolution.roster.model.enums.RequestStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateShiftSwap(
        @NotNull RequestStatus status
) {
}
