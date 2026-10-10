package com.ficsolution.roster.web.dto.shift;

import com.ficsolution.roster.model.Employee;
import com.ficsolution.roster.model.Shift;
import com.ficsolution.roster.model.Store;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record CreateShiftRequest(
        @NotNull UUID employeeId,
        @NotNull UUID storeId,
        @NotNull LocalDate shiftDate,
        @NotNull LocalTime startTime,
        @NotNull LocalTime endTime
) {
    public Shift toEntity() {
        return Shift.builder()
                .employee(Employee.builder().id(this.employeeId).build())
                .store(Store.builder().id(storeId).build())
                .shiftDate(shiftDate)
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
