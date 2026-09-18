package com.ficsolution.roster.web.dto.shift;

import com.ficsolution.roster.model.enumModel.RequestStatus;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UpdateShiftSwap(
        @NotNull RequestStatus status,
        @NotNull UUID targetId
) {
}
