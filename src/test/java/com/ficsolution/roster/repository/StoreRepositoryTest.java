package com.ficsolution.roster.repository;

import com.ficsolution.roster.model.Store;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.testcontainers.utility.TestcontainersConfiguration;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.ficsolution.roster.util.Util.storeId;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(TestcontainersConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class StoreRepositoryTest {

    @Autowired
    private StoreRepository storeRepository;

    @Test
    void testCreateStore() {
        // arrange
        Store store = new Store(null, "Ballsbridge", "Shelbourn 01 - Dublin 4", LocalDateTime.now(), LocalDateTime.now());

        // act
        store = storeRepository.save(store);

        // assert
        assertNotNull(store);
        assertNotNull(store.getCreatedAt());
        assertNotNull(store.getUpdatedAt());
        assertEquals("Ballsbridge", store.getName());
    }

    @Test
    void testRetrieveAllStores() {
        // act
        List<Store> retrievedStores = storeRepository.findAll();

        // assert
        assertNotNull(retrievedStores);
        assertEquals(4, retrievedStores.size());
        assertEquals("Loja Centro", retrievedStores.get(0).getName());
        assertEquals("Loja Shopping Plaza", retrievedStores.get(1).getName());
    }

    @Test
    void testRetrieveStoreById() {
        // act
        Optional<Store> retrievedStore = storeRepository.findById(storeId);

        // assert
        assertFalse(retrievedStore.isEmpty());
        assertEquals("Loja Centro", retrievedStore.get().getName());
    }

    @Test
    void testUpdateStoreNameAndAddress() {
        // arrange
        Store store = storeRepository.findById(storeId).get();
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
        Store store = storeRepository.findById(storeId).get();

        // act
        storeRepository.delete(store);
        Optional<Store> deletedStore = storeRepository.findById(storeId);

        // assert
        assertFalse(deletedStore.isPresent());
    }
}
