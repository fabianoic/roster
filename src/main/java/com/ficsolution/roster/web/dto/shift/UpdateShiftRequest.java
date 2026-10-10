package com.ficsolution.roster.web.dto.shift;

import com.ficsolution.roster.model.Shift;
import com.ficsolution.roster.model.Store;
import com.ficsolution.roster.model.enums.ShiftStatus;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;
import java.util.UUID;

public record UpdateShiftRequest(
        @NotNull UUID storeId,
        @NotNull ShiftStatus status,
        @NotNull LocalTime startTime,
        @NotNull LocalTime endTime
) {
    public Shift toEntity() {
        return Shift.builder()
                .store(Store.builder().id(storeId).build())
                .status(status)
                .startTime(startTime)
                .endTime(endTime)
                .build();
    }

    @AssertTrue(message = "endTime must be after startTime")
    boolean isEndAfterStart() {
        return endTime == null ||
                startTime == null ||
                endTime.isAfter(startTime);
    }
}
