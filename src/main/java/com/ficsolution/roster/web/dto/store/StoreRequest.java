package com.ficsolution.roster.web.dto.store;

import com.ficsolution.roster.model.Store;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record StoreRequest(
        @NotBlank @Size(max = 120) String name,
        @NotBlank String address) {
    public Store toEntity() {
        return Store.builder()
                .name(this.name)
                .address(this.address)
                .build();
    }
}
