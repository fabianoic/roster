package com.ficsolution.roster.web.dto.timeoff;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.ficsolution.roster.model.Employee;
import com.ficsolution.roster.model.TimeOffRequest;
import com.ficsolution.roster.model.enums.RequestStatus;
import com.ficsolution.roster.model.enums.TimeOffRequestType;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public record CreateTimeOffRequest(
        @NotNull UUID employeeId,
        @NotNull
        @JsonSerialize(using = LocalDateSerializer.class)
        @JsonDeserialize(using = LocalDateDeserializer.class)
        LocalDate startDate,
        @NotNull
        @JsonSerialize(using = LocalDateSerializer.class)
        @JsonDeserialize(using = LocalDateDeserializer.class)
        LocalDate endDate,
        String reason,
        @NotNull TimeOffRequestType type
) {
    public TimeOffRequest toEntity() {
        return TimeOffRequest.builder()
                .employee(Employee.builder().id(employeeId).build())
                .startDate(startDate)
                .endDate(endDate)
                .reason(reason)
                .type(type)
                .status(RequestStatus.PENDING)
                .build();
    }
}
