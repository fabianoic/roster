package com.ficsolution.roster.web.dto.store;

import com.ficsolution.roster.model.Store;

import java.time.LocalDateTime;
import java.util.UUID;

public record StoreResponse(
        UUID id,
        String name,
        String address,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static StoreResponse from(Store store) {
        return new StoreResponse(
                store.getId(),
                store.getName(),
                store.getAddress(),
                store.getCreatedAt(),
                store.getUpdatedAt());
    }
}
