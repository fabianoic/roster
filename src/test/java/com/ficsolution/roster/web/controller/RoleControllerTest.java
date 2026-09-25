package com.ficsolution.roster.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ficsolution.roster.config.JwtConfig;
import com.ficsolution.roster.config.SecurityConfig;
import com.ficsolution.roster.exception.ObjectNotFoundException;
import com.ficsolution.roster.model.Permission;
import com.ficsolution.roster.model.Role;
import com.ficsolution.roster.service.RoleService;
import com.ficsolution.roster.util.Util;
import com.ficsolution.roster.web.dto.permission.RolePermissionsRequest;
import com.ficsolution.roster.web.dto.role.RoleRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
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

        given(roleService.createRole(any(Role.class))).willReturn(role);

        mockMvc.perform(post("/roles")
                        .with(Util.authority)
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
                        .with(Util.authority)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new RoleRequest(""))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void mustReturn404WhenRoleNotExists() throws Exception {
        UUID id = UUID.randomUUID();
        given(roleService.retrieveById(id)).willThrow(new ObjectNotFoundException("Role", id.toString()));

        mockMvc.perform(get("/roles/{id}", id)
                        .with(Util.authority))
                .andExpect(status().isNotFound());
    }

    @Test
    void mustDeleteRole() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(delete("/roles/{id}", id)
                        .with(Util.authority))
                .andExpect(status().isNoContent());

        then(roleService).should().deleteRole(id);
    }

    @Test
    void mustReturn403WhenStaffListsRoles() throws Exception {
        mockMvc.perform(get("/roles")
                        .with(Util.staffAuthority))
                .andExpect(status().isForbidden());
    }

    @Test
    void mustRetrieveRolePermissions() throws Exception {
        UUID id = UUID.randomUUID();
        Permission permission = new Permission(UUID.randomUUID(), "SHIFT_READ", "View shifts");
        given(roleService.retrievePermissions(id)).willReturn(List.of(permission));

        mockMvc.perform(get("/roles/{id}/permissions", id)
                        .with(Util.authority))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("SHIFT_READ"))
                .andExpect(jsonPath("$[0].description").value("View shifts"));
    }

    @Test
    void mustReplaceRolePermissions() throws Exception {
        UUID id = UUID.randomUUID();
        Set<String> names = Set.of("SHIFT_READ", "SHIFT_WRITE");
        given(roleService.replacePermissions(id, names)).willReturn(List.of(
                new Permission(UUID.randomUUID(), "SHIFT_READ", null),
                new Permission(UUID.randomUUID(), "SHIFT_WRITE", null)));

        mockMvc.perform(put("/roles/{id}/permissions", id)
                        .with(Util.authority)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new RolePermissionsRequest(names))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

        then(roleService).should().replacePermissions(id, names);
    }

    @Test
    void mustReturn404WhenReplacingWithUnknownPermission() throws Exception {
        UUID id = UUID.randomUUID();
        Set<String> names = Set.of("UNKNOWN");
        given(roleService.replacePermissions(id, names)).willThrow(new ObjectNotFoundException("Permission", "UNKNOWN"));

        mockMvc.perform(put("/roles/{id}/permissions", id)
                        .with(Util.authority)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new RolePermissionsRequest(names))))
                .andExpect(status().isNotFound());
    }

    @Test
    void mustReturn400WhenPermissionsIsNull() throws Exception {
        mockMvc.perform(put("/roles/{id}/permissions", UUID.randomUUID())
                        .with(Util.authority)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void mustReturn403WhenSupervisorReplacesRolePermissions() throws Exception {
        mockMvc.perform(put("/roles/{id}/permissions", UUID.randomUUID())
                        .with(Util.supervisorAuthority)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new RolePermissionsRequest(Set.of("ROLE_MANAGE")))))
                .andExpect(status().isForbidden());
    }

    @Test
    void mustReturn403WhenStaffRetrievesRolePermissions() throws Exception {
        mockMvc.perform(get("/roles/{id}/permissions", UUID.randomUUID())
                        .with(Util.staffAuthority))
                .andExpect(status().isForbidden());
    }

    @Test
    void mustReturn403WhenSupervisorCreatesRole() throws Exception {
        mockMvc.perform(post("/roles")
                        .with(Util.supervisorAuthority)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new RoleRequest("NEW_ROLE"))))
                .andExpect(status().isForbidden());
    }
}
