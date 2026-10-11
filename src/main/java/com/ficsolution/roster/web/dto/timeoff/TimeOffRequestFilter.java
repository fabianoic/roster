package com.ficsolution.roster.web.dto.timeoff;

import com.ficsolution.roster.model.enums.RequestStatus;
import com.ficsolution.roster.model.enums.TimeOffRequestType;

import java.time.LocalDate;
import java.util.UUID;

public record TimeOffRequestFilter(
        UUID employeeId,
        RequestStatus status,
        TimeOffRequestType type,
        LocalDate start,
        LocalDate end
) {
}
