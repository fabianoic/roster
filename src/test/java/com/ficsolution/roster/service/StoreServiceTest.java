package com.ficsolution.roster.service;

import com.ficsolution.roster.exception.StoreNotFoundException;
import com.ficsolution.roster.model.Store;
import com.ficsolution.roster.repository.StoreRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StoreServiceTest {

    @InjectMocks
    private StoreService storeService;

    @Mock
    private StoreRepository storeRepository;

    private UUID id = UUID.randomUUID();

    @Test
    void testCreateStore() {
        // arrange
        Store store = new Store(id, "Ballsbridge", "Shelbourn 01 - Dublin 4", LocalDateTime.now(), LocalDateTime.now());
        when(storeRepository.save(any(Store.class))).thenReturn(store);

        // act
        Store createdStore = storeService.createStore(store);

        // assert
        assertNotNull(createdStore);
        assertEquals("Ballsbridge", store.getName());
        verify(storeRepository, times(1)).save(store);
    }

    @Test
    void testRetriveAllStores() {
        // arrange
        List<Store> stores = Arrays.asList(
                new Store(id, "Ballsbridge", "Shelbourn 01 - Dublin 4", LocalDateTime.now(), LocalDateTime.now()),
                new Store(UUID.randomUUID(), "Blackrock", "Blackrock Shopping Center", LocalDateTime.now(), LocalDateTime.now())
        );
        when(storeRepository.findAll()).thenReturn(stores);

        // act
        List<Store> retrievedStores = storeService.retrieveAllStores();

        // assert
        assertNotNull(retrievedStores);
        assertEquals(2, stores.size());
        verify(storeRepository, times(1)).findAll();
    }

    @Test
    void testRetriveStoreById() {
        // arrange
        Store store = new Store(id, "Ballsbridge", "Shelbourn 01 - Dublin 4", LocalDateTime.now(), LocalDateTime.now());
        when(storeRepository.findById(id)).thenReturn(Optional.of(store));

        // act
        Store retrievedStore = storeService.retrieveStoreById(id);

        // assert
        assertNotNull(retrievedStore);
        assertEquals("Ballsbridge", retrievedStore.getName());
        verify(storeRepository, times(1)).findById(id);
    }

    @Test
    void testRetriveStoreById_NotFound() {
        // arrange
        when(storeRepository.findById(id)).thenReturn(Optional.empty());

        // act & assert
        assertThrows(StoreNotFoundException.class, () -> storeService.retrieveStoreById(id));
        verify(storeRepository, times(1)).findById(id);
    }

    @Test
    void testUpdateStoreNameAndAddress() {
        // arrange
        Store store = new Store(id, "Ballsbridge", "Shelbourn 01 - Dublin 4", LocalDateTime.now(), LocalDateTime.now());
        Store editStore = new Store(id, "Blackrock", "Blackrock Shopping Center", null, null);
        when(storeRepository.findById(id)).thenReturn(Optional.of(store));
        when(storeRepository.save(any(Store.class))).thenReturn(store);

        // act
        Store updatedStore = storeService.updateStore(id, editStore);

        // assert
        assertNotNull(updatedStore);
        assertEquals("Blackrock", updatedStore.getName());
        assertEquals("Blackrock Shopping Center", updatedStore.getAddress());
        verify(storeRepository, times(1)).findById(id);
        verify(storeRepository, times(1)).save(store);
    }

    @Test
    void testDeleteStore() {
        // arrange
        Store store = new Store(id, "Ballsbridge", "Shelbourn 01 - Dublin 4", LocalDateTime.now(), LocalDateTime.now());
        when(storeRepository.findById(id)).thenReturn(Optional.of(store));

        // act
        storeService.deleteStore(id);

        // assert
        verify(storeRepository, times(1)).findById(id);
        verify(storeRepository, times(1)).delete(store);
    }
}
