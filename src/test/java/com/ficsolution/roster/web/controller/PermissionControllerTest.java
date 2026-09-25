package com.ficsolution.roster.web.controller;

import com.ficsolution.roster.config.JwtConfig;
import com.ficsolution.roster.config.SecurityConfig;
import com.ficsolution.roster.model.Permission;
import com.ficsolution.roster.service.PermissionService;
import com.ficsolution.roster.util.Util;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PermissionController.class)
@Import({SecurityConfig.class, JwtConfig.class})
public class PermissionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PermissionService permissionService;

    @Test
    void mustReturn401WhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/permissions"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void mustListPermissions() throws Exception {
        given(permissionService.retrieveAllPermissions()).willReturn(List.of(
                new Permission(UUID.randomUUID(), "SHIFT_READ", "View shifts"),
                new Permission(UUID.randomUUID(), "SHIFT_WRITE", "Create and update shifts")));

        mockMvc.perform(get("/permissions")
                        .with(Util.authority))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("SHIFT_READ"));
    }

    @Test
    void mustReturn403WhenSupervisorListsPermissions() throws Exception {
        mockMvc.perform(get("/permissions")
                        .with(Util.supervisorAuthority))
                .andExpect(status().isForbidden());
    }
}
