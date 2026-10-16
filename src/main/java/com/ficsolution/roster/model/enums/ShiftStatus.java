package com.ficsolution.roster.model.enums;

public enum ShiftStatus {
    SCHEDULED, COMPLETED, CANCELED;

    public boolean isFinal() {
        return this == COMPLETED || this == CANCELED;
    }
}
