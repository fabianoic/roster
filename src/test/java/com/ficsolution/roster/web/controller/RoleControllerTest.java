package com.ficsolution.roster.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ficsolution.roster.config.JwtConfig;
import com.ficsolution.roster.config.SecurityConfig;
import com.ficsolution.roster.exception.ObjectNotFoundException;
import com.ficsolution.roster.model.Role;
import com.ficsolution.roster.service.RoleService;
import com.ficsolution.roster.web.dto.role.RoleRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RoleController.class)
@Import({SecurityConfig.class, JwtConfig.class})
public class RoleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper mapper = new ObjectMapper();

    @MockitoBean
    private RoleService roleService;

    @Test
    void mustReturn401WhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/roles"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void mustCreateRoleWithSuccess() throws Exception {
        UUID id = UUID.randomUUID();
        Role role = new Role();
        role.setId(id);
        role.setName("NEW_ROLE");

        given(roleService.createRole(any(RoleRequest.class))).willReturn(role);

        mockMvc.perform(post("/roles")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_MANAGER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new RoleRequest("NEW_ROLE"))))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("NEW_ROLE"));
    }

    @Test
    void mustReturn400WhenEmptyName() throws Exception {
        mockMvc.perform(post("/roles")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_MANAGER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new RoleRequest(""))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void mustReturn404WhenRoleNotExists() throws Exception {
        UUID id = UUID.randomUUID();
        given(roleService.retrieveById(id)).willThrow(new ObjectNotFoundException("Role", id.toString()));

        mockMvc.perform(get("/roles/{id}", id)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_MANAGER"))))
                .andExpect(status().isNotFound());
    }

    @Test
    void mustDeleteRole() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(delete("/roles/{id}", id)
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_MANAGER"))))
                .andExpect(status().isNoContent());

        then(roleService).should().deleteRole(id);
    }
}
