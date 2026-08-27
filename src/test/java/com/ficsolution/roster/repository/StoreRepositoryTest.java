package com.ficsolution.roster.repository;

import com.ficsolution.roster.model.Store;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.testcontainers.utility.TestcontainersConfiguration;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(TestcontainersConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class StoreRepositoryTest {

    @Autowired
    private StoreRepository storeRepository;

    private UUID id = UUID.randomUUID();

    @Test
    void testCreateStore() {
        // arrange
        Store store = new Store(id, "Ballsbridge", "Shelbourn 01 - Dublin 4", LocalDateTime.now(), LocalDateTime.now());

        // act
        store = storeRepository.save(store);

        // assert
        assertNotNull(store);
        assertNotNull(store.getCreatedAt());
        assertNotNull(store.getUpdatedAt());
        assertEquals(id, store.getId());
        assertEquals("Ballsbridge", store.getName());
    }

    @Test
    void testRetrieveAllStores() {
        // arrange
        List<Store> stores = Arrays.asList(
                new Store(id, "Ballsbridge", "Shelbourn 01 - Dublin 4", LocalDateTime.now(), LocalDateTime.now()),
                new Store(UUID.randomUUID(), "Blackrock", "Blackrock Shopping Center", LocalDateTime.now(), LocalDateTime.now()));
        storeRepository.saveAll(stores);
        // act
        List<Store> retrievedStores = storeRepository.findAll();

        // assert
        assertNotNull(retrievedStores);
        assertEquals(2, retrievedStores.size());
        assertEquals("Ballsbridge", retrievedStores.get(0).getName());
        assertEquals("Blackrock", retrievedStores.get(1).getName());
    }

    @Test
    void testRetrieveStoreById() {
        // arrange
        Store store = new Store(id, "Ballsbridge", "Shelbourn 01 - Dublin 4", LocalDateTime.now(), LocalDateTime.now());
        storeRepository.save(store);

        // act
        Optional<Store> retrievedStore = storeRepository.findById(id);

        // assert
        assertFalse(retrievedStore.isEmpty());
        assertEquals("Ballsbridge", retrievedStore.get().getName());
    }

    @Test
    void testUpdateStoreNameAndAddress() {
        // arrange
        Store store = new Store(id, "Ballsbridge", "Shelbourn 01 - Dublin 4", LocalDateTime.now(), LocalDateTime.now());
        storeRepository.save(store);
        store.setName("Blackrock");
        store.setAddress("Blackrock Shopping Center");

        // act
        Store savedStore = storeRepository.save(store);

        // assert
        assertNotNull(savedStore);
        assertEquals("Blackrock", savedStore.getName());
        assertEquals("Blackrock Shopping Center", savedStore.getAddress());
    }

    @Test
    void testDeleteStore() {
        // arrange
        Store store = new Store(id, "Ballsbridge", "Shelbourn 01 - Dublin 4", LocalDateTime.now(), LocalDateTime.now());
        storeRepository.save(store);

        // act
        storeRepository.delete(store);
        Optional<Store> deletedStore = storeRepository.findById(id);

        // assert
        assertFalse(deletedStore.isPresent());
    }
}
