package com.ficsolution.roster.web.dto.availability;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.ficsolution.roster.model.Availability;
import jakarta.validation.constraints.NotNull;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record UpdateAvailabilityRequest(
        @NotNull DayOfWeek weekday,
        boolean available,
        String note,
        @NotNull
        @JsonSerialize(using = LocalTimeSerializer.class)
        @JsonDeserialize(using = LocalTimeDeserializer.class)
        LocalTime startTime,
        @NotNull
        @JsonSerialize(using = LocalTimeSerializer.class)
        @JsonDeserialize(using = LocalTimeDeserializer.class)
        LocalTime endTime
) {
    public Availability toEntity() {
        return new Availability(
                null,
                null,
                weekday,
                available,
                note,
                startTime,
                endTime
        );
    }
}
