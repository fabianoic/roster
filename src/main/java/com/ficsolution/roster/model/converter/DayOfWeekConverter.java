package com.ficsolution.roster.model.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.DayOfWeek;

/**
 * Stores {@link DayOfWeek} as ISO-8601 numbers (1 = Monday ... 7 = Sunday), matching the
 * {@code weekday} column constraint, instead of the enum ordinal (0 = Monday).
 */
@Converter
public class DayOfWeekConverter implements AttributeConverter<DayOfWeek, Short> {

    @Override
    public Short convertToDatabaseColumn(DayOfWeek dayOfWeek) {
        return dayOfWeek == null ? null : (short) dayOfWeek.getValue();
    }

    @Override
    public DayOfWeek convertToEntityAttribute(Short value) {
        return value == null ? null : DayOfWeek.of(value);
    }
}
