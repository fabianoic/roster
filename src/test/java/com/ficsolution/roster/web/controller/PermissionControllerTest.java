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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
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
        given(permissionService.retrieveAllPermissions(any(), any())).willReturn(new PageImpl<>(List.of(
                new Permission(UUID.randomUUID(), "SHIFT_READ", "View shifts"),
                new Permission(UUID.randomUUID(), "SHIFT_WRITE", "Create and update shifts"))));

        mockMvc.perform(get("/permissions")
                        .with(Util.authority))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].name").value("SHIFT_READ"));
    }

    @Test
    void mustReturn403WhenSupervisorListsPermissions() throws Exception {
        mockMvc.perform(get("/permissions")
                        .with(Util.supervisorAuthority))
                .andExpect(status().isForbidden());
    }

    @Test
    void mustListPermissionsSortedByNameByDefault() throws Exception {
        given(permissionService.retrieveAllPermissions(any(), any())).willReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/permissions")
                        .with(Util.authority)
                        .param("name", "shift"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)))
                .andExpect(jsonPath("$.totalElements").value(0));

        then(permissionService).should().retrieveAllPermissions(any(), argThat(pageable ->
                pageable.getSort().equals(Sort.by("name"))));
    }
}
