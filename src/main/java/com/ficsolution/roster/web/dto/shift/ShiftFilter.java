package com.ficsolution.roster.web.dto.shift;

import java.time.LocalDate;
import java.util.UUID;

public record ShiftFilter(
        UUID employeeId,
        UUID storeId,
        LocalDate start,
        LocalDate end
) {
}
