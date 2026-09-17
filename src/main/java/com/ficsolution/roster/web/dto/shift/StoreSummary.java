package com.ficsolution.roster.web.dto.shift;

import com.ficsolution.roster.model.Store;

import java.util.UUID;

public record StoreSummary(
        UUID id,
        String name) {
    public static StoreSummary from(Store store) {
        return new StoreSummary(store.getId(), store.getName());
    }
}
