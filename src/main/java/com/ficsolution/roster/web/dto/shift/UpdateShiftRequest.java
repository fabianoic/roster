package com.ficsolution.roster.web.dto.shift;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.ficsolution.roster.model.Shift;
import com.ficsolution.roster.model.Store;
import com.ficsolution.roster.model.enums.ShiftStatus;

import java.time.LocalTime;
import java.util.UUID;

public record UpdateShiftRequest(
        UUID storeId,
        ShiftStatus status,
        @JsonSerialize(using = LocalTimeSerializer.class)
        @JsonDeserialize(using = LocalTimeDeserializer.class)
        LocalTime startTime,
        @JsonSerialize(using = LocalTimeSerializer.class)
        @JsonDeserialize(using = LocalTimeDeserializer.class)
        LocalTime endTime
) {
    public Shift toEntity() {
        return Shift.builder()
                .store(Store.builder().id(storeId).build())
                .status(status)
                .startTime(startTime)
                .endTime(endTime)
                .build();
    }
}
