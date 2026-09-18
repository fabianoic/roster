package com.ficsolution.roster.web.dto.shift;

import com.ficsolution.roster.model.Shift;
import com.ficsolution.roster.model.enumModel.ShiftStatus;
import com.ficsolution.roster.web.dto.store.StoreResponse;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record ShiftSummary(
        UUID id,
        StoreResponse store,
        LocalDate shiftDate,
        LocalTime startTime,
        LocalTime endTime,
        ShiftStatus status
) {
    public static ShiftSummary from(Shift shift) {
        return new ShiftSummary(
                shift.getId(),
                StoreResponse.from(shift.getStore()),
                shift.getShiftDate(),
                shift.getStartTime(),
                shift.getEndTime(),
                shift.getStatus()
        );
    }
}
