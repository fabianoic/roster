package com.ficsolution.roster.web.dto.availability;

import com.ficsolution.roster.model.Availability;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.UUID;

public record AvailabilityResponse(
        UUID id,
        EmployeeSummary employee,
        DayOfWeek weekday,
        boolean available,
        String note,
        LocalTime startTime,
        LocalTime endTime
) {
    public static AvailabilityResponse from(Availability availability) {
        return new AvailabilityResponse(
                availability.getId(),
                EmployeeSummary.from(availability.getEmployee()),
                availability.getWeekday(),
                availability.isAvailable(),
                availability.getNote(),
                availability.getStartTime(),
                availability.getEndTime()
        );
    }
}
