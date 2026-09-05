package com.ficsolution.roster.service;

import com.ficsolution.roster.exception.ObjectNotFoundException;
import com.ficsolution.roster.model.Store;
import com.ficsolution.roster.repository.StoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class StoreService {

    @Autowired
    private StoreRepository storeRepository;

    public Store createStore(Store store) {
        return storeRepository.save(store);
    }

    public List<Store> retrieveAllStores() {
        return storeRepository.findAll();
    }

    public Store retrieveStoreById(UUID id) {
        return storeRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException("Store not found, id: " + id));
    }

    public Store updateStore(UUID id, Store editStore) {
        Store store = retrieveStoreById(id);

        store.setName(editStore.getName());
        store.setAddress(editStore.getAddress());
        store.setUpdatedAt(LocalDateTime.now().plusMinutes(1));

        return storeRepository.save(store);
    }

    public void deleteStore(UUID id) {
        Store store = retrieveStoreById(id);

        storeRepository.delete(store);
    }
}
