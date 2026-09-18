package com.ficsolution.roster.web.dto.timeoff;

import com.ficsolution.roster.model.TimeOffRequest;
import com.ficsolution.roster.model.enumModel.RequestStatus;
import com.ficsolution.roster.model.enumModel.TimeOffRequestType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record TimeOffRequestResponse(
        UUID id,
        EmployeeSummary employee,
        LocalDate startDate,
        LocalDate endDate,
        String reason,
        TimeOffRequestType type,
        RequestStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static TimeOffRequestResponse from(TimeOffRequest timeOffRequest) {
        return new TimeOffRequestResponse(
                timeOffRequest.getId(),
                EmployeeSummary.from(timeOffRequest.getEmployee()),
                timeOffRequest.getStartDate(),
                timeOffRequest.getEndDate(),
                timeOffRequest.getReason(),
                timeOffRequest.getType(),
                timeOffRequest.getStatus(),
                timeOffRequest.getCreatedAt(),
                timeOffRequest.getUpdatedAt()
        );
    }
}
