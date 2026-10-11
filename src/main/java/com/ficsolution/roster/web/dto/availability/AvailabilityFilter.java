package com.ficsolution.roster.web.dto.availability;

import java.time.DayOfWeek;
import java.util.UUID;

public record AvailabilityFilter(
        UUID employeeId,
        DayOfWeek weekday,
        Boolean available
) {
}
