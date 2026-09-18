package com.ficsolution.roster.web.dto.timeoff;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.ficsolution.roster.model.TimeOffRequest;
import com.ficsolution.roster.model.enumModel.RequestStatus;
import com.ficsolution.roster.model.enumModel.TimeOffRequestType;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record UpdateTimeOffRequest(
        @NotNull
        @JsonSerialize(using = LocalDateSerializer.class)
        @JsonDeserialize(using = LocalDateDeserializer.class)
        LocalDate startDate,
        @NotNull
        @JsonSerialize(using = LocalDateSerializer.class)
        @JsonDeserialize(using = LocalDateDeserializer.class)
        LocalDate endDate,
        String reason,
        @NotNull TimeOffRequestType type,
        @NotNull RequestStatus status
) {
    public TimeOffRequest toEntity() {
        return TimeOffRequest.builder()
                .startDate(startDate)
                .endDate(endDate)
                .reason(reason)
                .type(type)
                .status(status)
                .build();
    }
}
