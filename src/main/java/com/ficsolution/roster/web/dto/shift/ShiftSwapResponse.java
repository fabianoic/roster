package com.ficsolution.roster.web.dto.shift;

import com.ficsolution.roster.model.ShiftSwapRequest;
import com.ficsolution.roster.model.enumModel.RequestStatus;

import java.util.UUID;

public record ShiftSwapResponse(
        UUID id,
        ShiftSummary shift,
        EmployeeSummary requester,
        EmployeeSummary target,
        RequestStatus status
) {
    public static ShiftSwapResponse from(ShiftSwapRequest shiftSwapRequest) {
        return new ShiftSwapResponse(
                shiftSwapRequest.getId(),
                ShiftSummary.from(shiftSwapRequest.getShift()),
                EmployeeSummary.from(shiftSwapRequest.getRequester()),
                EmployeeSummary.from(shiftSwapRequest.getTarget()),
                shiftSwapRequest.getStatus()
        );
    }
}
