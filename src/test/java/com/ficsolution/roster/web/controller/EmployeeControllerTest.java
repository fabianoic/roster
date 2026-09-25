package com.ficsolution.roster.web.controller;

import com.ficsolution.roster.config.JwtConfig;
import com.ficsolution.roster.config.SecurityConfig;
import com.ficsolution.roster.exception.ObjectNotFoundException;
import com.ficsolution.roster.model.Employee;
import com.ficsolution.roster.model.Role;
import com.ficsolution.roster.model.enumModel.EmployeeStatus;
import com.ficsolution.roster.service.EmployeeService;
import com.ficsolution.roster.util.Util;
import com.ficsolution.roster.web.dto.employee.ChangePasswordRequest;
import com.ficsolution.roster.web.dto.employee.CreateEmployeeRequest;
import com.ficsolution.roster.web.dto.employee.UpdateEmployeeRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmployeeController.class)
@Import({SecurityConfig.class, JwtConfig.class})
public class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EmployeeService employeeService;

    private final ObjectMapper mapper = new ObjectMapper();

    private final String path = "/employees";

    @Test
    void mustReturn401WhenNotAuthorized() throws Exception {
        mockMvc.perform(get(path))
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

        mockMvc.perform(post(path)
                        .with(Util.authority)
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

        mockMvc.perform(post(path)
                        .with(Util.authority)
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

        mockMvc.perform(get(String.format("%s/%s", path, id))
                        .with(Util.authority))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value(name));
    }

    @Test
    void mustRetrieveAllEmployees() throws Exception {
        Role role = Role.builder().id(UUID.randomUUID()).name("MANAGER").build();
        List<Employee> employees = List.of(
                Employee.builder().id(UUID.randomUUID()).name("ONE").email("one@gmail.com").role(role).build(),
                Employee.builder().id(UUID.randomUUID()).name("TWO").email("two@gmail.com").role(role).build(),
                Employee.builder().id(UUID.randomUUID()).name("THREE").email("three@gmail.com").role(role).build(),
                Employee.builder().id(UUID.randomUUID()).name("FOUR").email("four@gmail.com").role(role).build()
        );
        Pageable pageable = PageRequest.of(0, 10);
        Page<Employee> page = new PageImpl<>(employees, pageable, employees.size());
        given(employeeService.retrieveAllEmployees(any(), any())).willReturn(page);

        mockMvc.perform(get(path)
                        .with(Util.authority))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.*", hasSize(4)));
    }

    @Test
    void mustRetrieveAnUpdatedEmployee() throws Exception {
        UUID id = UUID.fromString("ebb6ea55-b853-4504-a381-0f59ad2a1659");
        UUID roleId = UUID.fromString("9aa2510e-feda-4b1f-bffe-21cc9e180e9f");
        UpdateEmployeeRequest updateEmployeeRequest =
                new UpdateEmployeeRequest("Pietro Silva", "pietro@gmail.com", roleId);
        Employee updatedEmployee = Employee.builder()
                .id(id)
                .name(updateEmployeeRequest.name())
                .email(updateEmployeeRequest.email())
                .role(Role.builder().id(updateEmployeeRequest.roleId()).build())
                .build();
        given(employeeService.updateEmployeeInfo(any(UUID.class), any(Employee.class))).willReturn(updatedEmployee);

        mockMvc.perform(put(String.format("%s/%s", path, id))
                        .with(Util.authority)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updateEmployeeRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("Pietro Silva"))
                .andExpect(jsonPath("$.email").value("pietro@gmail.com"));
    }

    @Test
    void mustChangeEmployeeStatus() throws Exception {
        UUID id = UUID.fromString("ebb6ea55-b853-4504-a381-0f59ad2a1659");
        Employee employee = Employee.builder()
                .id(UUID.randomUUID())
                .name("Fabiano C")
                .email("fabiano@gmail.com")
                .role(Role.builder().id(UUID.randomUUID()).build())
                .status(EmployeeStatus.INACTIVE)
                .build();
        given(employeeService.changeEmployeeStatus(id)).willReturn(employee);

        mockMvc.perform(put(String.format("%s/%s/change-status", path, id))
                        .with(Util.authority))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(EmployeeStatus.INACTIVE.toString()));
    }

    @Test
    void mustChangeEmployeePassword() throws Exception {
        UUID id = UUID.randomUUID();
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest("OLDPASSWORDSAVED", "NEWPASSOWRDTOSAVE");
        Employee employee = Employee.builder()
                .id(UUID.randomUUID())
                .name("Fabiano C")
                .email("fabiano@gmail.com")
                .role(Role.builder().id(UUID.randomUUID()).build())
                .status(EmployeeStatus.ACTIVE)
                .build();

        given(employeeService.changeEmployeePassword(id, changePasswordRequest)).willReturn(employee);

        mockMvc.perform(put(String.format("%s/%s/change-password", path, id))
                        .with(Util.authority)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(changePasswordRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void mustReturn400WhenInvalidPassword() throws Exception {
        UUID id = UUID.randomUUID();
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest("INVALID", "INVALID");
        Employee employee = Employee.builder()
                .id(UUID.randomUUID())
                .name("Fabiano C")
                .email("fabiano@gmail.com")
                .role(Role.builder().id(UUID.randomUUID()).build())
                .status(EmployeeStatus.ACTIVE)
                .build();

        given(employeeService.changeEmployeePassword(id, changePasswordRequest)).willReturn(employee);

        mockMvc.perform(put(String.format("%s/%s/change-password", path, id))
                        .with(Util.authority)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(changePasswordRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void mustRetrieveEmployeeByEmail() throws Exception {
        String email = "fabiano@gmail.com";
        Employee employee = Employee.builder()
                .id(UUID.randomUUID())
                .name("Fabiano C")
                .email(email)
                .role(Role.builder().id(UUID.randomUUID()).build())
                .status(EmployeeStatus.ACTIVE)
                .build();
        given(employeeService.retrieveEmployeeByEmail(email)).willReturn(employee);

        mockMvc.perform(get(path)
                        .with(Util.authority)
                        .param("email", email))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Fabiano C"))
                .andExpect(jsonPath("$.email").value(email));
    }

    @Test
    void mustReturn404WhenEmployeeNotFound() throws Exception {
        given(employeeService.retrieveEmployeeByEmail("nonexistent@gmail.com")).willThrow(ObjectNotFoundException.class);

        mockMvc.perform(get(path)
                        .with(Util.authority)
                        .param("email", "nonexistent@gmail.com"))
                .andExpect(status().isNotFound());
    }

    @Test
    void mustReturn403WhenStaffCreatesEmployee() throws Exception {
        CreateEmployeeRequest createEmployeeRequest = new CreateEmployeeRequest(
                "Fabiano", "fabiano.fic@gmail.com", "NEWPASSWORDHASH", UUID.randomUUID());

        mockMvc.perform(post(path)
                        .with(Util.staffAuthority)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(createEmployeeRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    void mustReturn403WhenStaffUpdatesEmployee() throws Exception {
        UpdateEmployeeRequest updateEmployeeRequest = new UpdateEmployeeRequest("Pietro Silva", "pietro@gmail.com", Util.roleId);

        mockMvc.perform(put(String.format("%s/%s", path, Util.employeeId))
                        .with(Util.staffAuthority)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updateEmployeeRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    void mustReturn403WhenStaffChangesEmployeeStatus() throws Exception {
        mockMvc.perform(put(String.format("%s/%s/change-status", path, Util.employeeId))
                        .with(Util.staffAuthority))
                .andExpect(status().isForbidden());
    }

    @Test
    void mustReturn403WhenStaffListsAllEmployees() throws Exception {
        mockMvc.perform(get(path)
                        .with(Util.staffAuthority))
                .andExpect(status().isForbidden());
    }

    @Test
    void mustReturn403WhenStaffSearchesEmployeeByEmail() throws Exception {
        mockMvc.perform(get(path)
                        .with(Util.staffAuthority)
                        .param("email", "fabiano@gmail.com"))
                .andExpect(status().isForbidden());
    }

    @Test
    void mustAllowStaffToRetrieveOwnProfileById() throws Exception {
        Employee employee = Employee.builder()
                .id(Util.employeeId)
                .name("Fabiano C")
                .email("fabiano@gmail.com")
                .role(Role.builder().id(UUID.randomUUID()).build())
                .status(EmployeeStatus.ACTIVE)
                .build();
        given(employeeService.retrieveEmployeeById(Util.employeeId)).willReturn(employee);

        mockMvc.perform(get(String.format("%s/%s", path, Util.employeeId))
                        .with(Util.staffAuthority))
                .andExpect(status().isOk());
    }

    @Test
    void mustReturn403WhenStaffRetrievesAnotherEmployeeProfileById() throws Exception {
        mockMvc.perform(get(String.format("%s/%s", path, Util.employeeId))
                        .with(Util.staffOtherAuthority))
                .andExpect(status().isForbidden());
    }

    @Test
    void mustAllowSupervisorToRetrieveAnyEmployeeProfileById() throws Exception {
        Employee employee = Employee.builder()
                .id(Util.employeeId)
                .name("Fabiano C")
                .email("fabiano@gmail.com")
                .role(Role.builder().id(UUID.randomUUID()).build())
                .status(EmployeeStatus.ACTIVE)
                .build();
        given(employeeService.retrieveEmployeeById(Util.employeeId)).willReturn(employee);

        mockMvc.perform(get(String.format("%s/%s", path, Util.employeeId))
                        .with(Util.supervisorAuthority))
                .andExpect(status().isOk());
    }

    @Test
    void mustAllowStaffToChangeOwnPassword() throws Exception {
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest("OLDPASSWORDSAVED", "NEWPASSOWRDTOSAVE");
        Employee employee = Employee.builder()
                .id(Util.employeeId)
                .name("Fabiano C")
                .email("fabiano@gmail.com")
                .role(Role.builder().id(UUID.randomUUID()).build())
                .status(EmployeeStatus.ACTIVE)
                .build();
        given(employeeService.changeEmployeePassword(Util.employeeId, changePasswordRequest)).willReturn(employee);

        mockMvc.perform(put(String.format("%s/%s/change-password", path, Util.employeeId))
                        .with(Util.staffAuthority)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(changePasswordRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void mustReturn403WhenStaffChangesAnotherEmployeePassword() throws Exception {
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest("OLDPASSWORDSAVED", "NEWPASSOWRDTOSAVE");

        mockMvc.perform(put(String.format("%s/%s/change-password", path, Util.employeeId))
                        .with(Util.staffOtherAuthority)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(changePasswordRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    void mustReturn403WhenSupervisorChangesAnotherEmployeePassword() throws Exception {
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest("OLDPASSWORDSAVED", "NEWPASSOWRDTOSAVE");

        mockMvc.perform(put(String.format("%s/%s/change-password", path, Util.employee1Id))
                        .with(Util.supervisorAuthority)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(changePasswordRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    void mustAllowManagerToChangeAnotherEmployeePassword() throws Exception {
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest("OLDPASSWORDSAVED", "NEWPASSOWRDTOSAVE");
        Employee employee = Employee.builder()
                .id(Util.employee1Id)
                .name("Pietro Silva")
                .email("pietro@gmail.com")
                .role(Role.builder().id(UUID.randomUUID()).build())
                .status(EmployeeStatus.ACTIVE)
                .build();
        given(employeeService.changeEmployeePassword(Util.employee1Id, changePasswordRequest)).willReturn(employee);

        mockMvc.perform(put(String.format("%s/%s/change-password", path, Util.employee1Id))
                        .with(Util.authority)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(changePasswordRequest)))
                .andExpect(status().isOk());
    }
}
