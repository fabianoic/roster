package com.ficsolution.roster.web.controller;

import com.ficsolution.roster.model.Store;
import com.ficsolution.roster.service.StoreService;
import com.ficsolution.roster.web.dto.store.StoreRequest;
import com.ficsolution.roster.web.dto.store.StoreResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/stores")
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;

    @PostMapping
    public ResponseEntity<StoreResponse> createStore(@Valid @RequestBody StoreRequest storeRequest) {
        Store store = storeRequest.toEntity();
        StoreResponse response = StoreResponse.from(storeService.createStore(store));
        return ResponseEntity.created(URI.create(String.format("/stores/%s", response.id()))).body(response);
    }

    @GetMapping
    public ResponseEntity<List<StoreResponse>> retrieveAllStores() {
        List<Store> stores = storeService.retrieveAllStores();
        List<StoreResponse> responses = stores.stream().map(StoreResponse::from).toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StoreResponse> retrieveStoreById(@PathVariable UUID id) {
        Store store = storeService.retrieveStoreById(id);
        return ResponseEntity.ok(StoreResponse.from(store));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StoreResponse> updateStore(@PathVariable UUID id, @Valid @RequestBody StoreRequest storeRequest) {
        Store request = storeRequest.toEntity();
        Store store = storeService.updateStore(id, request);
        return ResponseEntity.ok(StoreResponse.from(store));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStore(@PathVariable UUID id) {
        storeService.deleteStore(id);
        return ResponseEntity.noContent().build();
    }
}
