package com.ficsolution.roster.web.controller;

import com.ficsolution.roster.config.JwtConfig;
import com.ficsolution.roster.config.SecurityConfig;
import com.ficsolution.roster.exception.ObjectNotFoundException;
import com.ficsolution.roster.model.Store;
import com.ficsolution.roster.service.StoreService;
import com.ficsolution.roster.util.Util;
import com.ficsolution.roster.web.dto.store.StoreRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StoreController.class)
@Import({SecurityConfig.class, JwtConfig.class})
public class StoreControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StoreService storeService;

    private final ObjectMapper mapper = new ObjectMapper();
    private final String path = "/stores";

    @Test
    void mustCreateAStore() throws Exception {
        String name = "Ballsbridge";
        String address = "Avoca Market - Shelbourne";
        Store store = Store.builder()
                .id(UUID.randomUUID())
                .name(name)
                .address(address)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        given(storeService.createStore(any(Store.class))).willReturn(store);

        mockMvc.perform(post(path)
                        .with(Util.authority)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new StoreRequest(name, address))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.address").value(address));
    }

    @Test
    void mustReturnAllStores() throws Exception {
        List<Store> stores = List.of(
                Store.builder().id(UUID.randomUUID()).name("STORE 1").address("ADDRESS 1").createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build(),
                Store.builder().id(UUID.randomUUID()).name("STORE 2").address("ADDRESS 2").createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build(),
                Store.builder().id(UUID.randomUUID()).name("STORE 3").address("ADDRESS 3").createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build(),
                Store.builder().id(UUID.randomUUID()).name("STORE 4").address("ADDRESS 4").createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build()
        );
        given(storeService.retrieveAllStores()).willReturn(stores);

        mockMvc.perform(get(path)
                        .with(Util.authority))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(4)));
    }

    @Test
    void retrieveStoreById() throws Exception {
        UUID id = UUID.randomUUID();
        String name = "Ballsbridge";
        String address = "Avoca Market - Shelbourne";
        Store store = Store.builder()
                .id(id)
                .name(name)
                .address(address)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        given(storeService.retrieveStoreById(id)).willReturn(store);

        mockMvc.perform(get(String.format("%s/%s", path, id))
                        .with(Util.authority))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.address").value(address));
    }

    @Test
    void mustReturn404WhenIdNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        given(storeService.retrieveStoreById(id)).willThrow(ObjectNotFoundException.class);

        mockMvc.perform(get(String.format("%s/%s", path, id))
                        .with(Util.authority))
                .andExpect(status().isNotFound());
    }

    @Test
    void mustReturnUpdatedStore() throws Exception {
        UUID id = UUID.randomUUID();
        Store store = Store.builder()
                .id(id)
                .name("new Name")
                .address("new Address")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        StoreRequest request = new StoreRequest("new Name", "new Address");
        given(storeService.updateStore(any(UUID.class), any(Store.class))).willReturn(store);

        mockMvc.perform(put(String.format("%s/%s", path, id))
                        .with(Util.authority)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("new Name"))
                .andExpect(jsonPath("$.address").value("new Address"));
    }

    @Test
    void mustDeleteStore() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(storeService).deleteStore(id);

        mockMvc.perform(delete(String.format("%s/%s", path, id))
                        .with(Util.authority))
                .andExpect(status().isNoContent());

    }
}
