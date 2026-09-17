package com.ficsolution.roster.web.dto.shift;

import com.ficsolution.roster.model.Shift;
import com.ficsolution.roster.model.enumModel.ShiftStatus;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record ShiftResponse(
        UUID id,
        EmployeeSummary employee,
        StoreSummary store,
        LocalDate shiftDate,
        LocalTime startTime,
        LocalTime endTime,
        ShiftStatus status

) {
    public static ShiftResponse from(Shift shift) {
        return new ShiftResponse(
                shift.getId(),
                EmployeeSummary.from(shift.getEmployee()),
                StoreSummary.from(shift.getStore()),
                shift.getShiftDate(),
                shift.getStartTime(),
                shift.getEndTime(),
                shift.getStatus()
        );
    }
}
