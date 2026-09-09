package com.ficsolution.roster.web.controller;

import com.ficsolution.roster.config.JwtConfig;
import com.ficsolution.roster.config.SecurityConfig;
import com.ficsolution.roster.model.Employee;
import com.ficsolution.roster.model.Role;
import com.ficsolution.roster.model.enumModel.EmployeeStatus;
import com.ficsolution.roster.service.EmployeeService;
import com.ficsolution.roster.web.dto.employee.CreateEmployeeRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmployeeController.class)
@Import({SecurityConfig.class, JwtConfig.class})
public class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EmployeeService employeeService;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void mustReturn401WhenNotAuthorized() throws Exception {
        mockMvc.perform(get("/employees"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void mustCreateEmployeeWithSuccess() throws Exception {
        UUID id = UUID.randomUUID();
        String name = "Fabiano";
        Employee employee = Employee.builder()
                .id(id)
                .name(name)
                .email("fabiano.fic@gmail.com")
                .role(Role.builder().id(UUID.randomUUID()).build())
                .status(EmployeeStatus.ACTIVE).build();
        CreateEmployeeRequest createEmployeeRequest = new CreateEmployeeRequest(
                "Fabiano",
                "fabiano.fic@gmail.com",
                "NEWPASSWORDHASH",
                UUID.randomUUID()
        );
        given(employeeService.createEmployee(any(CreateEmployeeRequest.class))).willReturn(employee);

        mockMvc.perform(post("/employees")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_MANAGER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(createEmployeeRequest)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value(name));
    }

    @Test
    void mustReturn400WhenInvalidEmail() throws Exception {
        CreateEmployeeRequest createEmployeeRequest = new CreateEmployeeRequest(
                "Fabiano",
                "emailnotvalid",
                "NEWPASSWORD",
                UUID.randomUUID()
        );

        mockMvc.perform(post("/employees")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_MANAGER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(createEmployeeRequest)))
                .andExpect(status().isBadRequest());

    }

    @Test
    void mustRetrieveAnEmployee() throws Exception {
        UUID id = UUID.randomUUID();
        String name = "Fabiano";
        Employee employee = Employee.builder()
                .id(id)
                .name(name)
                .email("fabiano.fic@gmail.com")
                .role(Role.builder().id(UUID.randomUUID()).build())
                .status(EmployeeStatus.ACTIVE).build();

        given(employeeService.retrieveEmployeeById(id)).willReturn(employee);

        mockMvc.perform(get(String.format("/employees/%s", id))
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_MANAGER"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value(name));

    }
}
