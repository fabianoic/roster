package com.ficsolution.roster.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ficsolution.roster.config.JwtConfig;
import com.ficsolution.roster.config.SecurityConfig;
import com.ficsolution.roster.model.Employee;
import com.ficsolution.roster.model.Role;
import com.ficsolution.roster.model.Shift;
import com.ficsolution.roster.model.Store;
import com.ficsolution.roster.model.enumModel.EmployeeStatus;
import com.ficsolution.roster.model.enumModel.ShiftStatus;
import com.ficsolution.roster.service.ShiftService;
import com.ficsolution.roster.util.Util;
import com.ficsolution.roster.web.dto.shift.CreateShiftRequest;
import com.ficsolution.roster.web.dto.shift.UpdateShiftRequest;
import org.junit.jupiter.api.BeforeAll;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ShiftController.class)
@Import({SecurityConfig.class, JwtConfig.class})
public class ShiftControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ShiftService shiftService;

    private final ObjectMapper mapper = new ObjectMapper();
    private final String path = "/shifts";

    private static UUID id = UUID.randomUUID();
    private static Shift shift;
    private static Store store;
    private static Employee employee;

    @BeforeAll
    static void setUp() {
        employee = new Employee(UUID.randomUUID(), "Fabiano Campos",
                "fabiano.fic@gmail.com",
                "RANDOMHASHPASSWORD",
                new Role(UUID.fromString("df501f58-dc8a-470c-a26d-5786633b6009"), "STAFF"),
                EmployeeStatus.ACTIVE,
                LocalDateTime.now(),
                LocalDateTime.now(),
                false,
                0,
                null);

        store = new Store(UUID.randomUUID(), "Blackrock", "Blackrock Shopping Center", LocalDateTime.now(), LocalDateTime.now());

        shift = new Shift(
                id,
                employee,
                store,
                LocalDate.now(),
                LocalTime.of(8, 0),
                LocalTime.of(16, 0),
                ShiftStatus.SCHEDULED,
                LocalDateTime.now(),
                LocalDateTime.now());
    }

    @Test
    void mustCreateAShift() throws Exception {
        UUID id = UUID.randomUUID();
        UUID employeeId = UUID.randomUUID();
        UUID storeId = UUID.randomUUID();
        Shift shift = Shift.builder()
                .id(id)
                .employee(Employee.builder().id(employeeId).role(Role.builder().id(UUID.randomUUID()).build()).build())
                .store(Store.builder().id(storeId).build())
                .shiftDate(LocalDate.now())
                .startTime(LocalTime.of(8, 0))
                .endTime(LocalTime.of(16, 0))
                .status(ShiftStatus.SCHEDULED)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        CreateShiftRequest request = new CreateShiftRequest(
                employeeId,
                storeId,
                LocalDate.now(),
                LocalTime.of(8, 0),
                LocalTime.of(16, 0)
        );
        given(shiftService.createShift(any(Shift.class))).willReturn(shift);

        mockMvc.perform(post(path)
                        .with(Util.authority)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(id.toString()));
    }

    @Test
    void mustRetrieveShiftById() throws Exception {
        UUID id = UUID.randomUUID();
        UUID employeeId = UUID.randomUUID();
        UUID storeId = UUID.randomUUID();
        Shift shift = Shift.builder()
                .id(id)
                .employee(Employee.builder().id(employeeId).role(Role.builder().id(UUID.randomUUID()).build()).build())
                .store(Store.builder().id(storeId).build())
                .shiftDate(LocalDate.now())
                .startTime(LocalTime.of(8, 0))
                .endTime(LocalTime.of(16, 0))
                .status(ShiftStatus.SCHEDULED)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        given(shiftService.retrieveShiftById(any(UUID.class))).willReturn(shift);

        mockMvc.perform(get(String.format("%s/%s", path, id))
                        .with(Util.authority))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.employee.id").value(employeeId.toString()))
                .andExpect(jsonPath("$.store.id").value(storeId.toString()));
    }

    @Test
    void mustRetrieveAllShifts() throws Exception {
        List<Shift> shifts = Arrays.asList(
                shift,
                new Shift(
                        UUID.randomUUID(),
                        Employee.builder().id(UUID.randomUUID()).build(),
                        store,
                        LocalDate.now().plusDays(1),
                        LocalTime.of(10, 0),
                        LocalTime.of(18, 0),
                        ShiftStatus.SCHEDULED,
                        LocalDateTime.now(),
                        LocalDateTime.now())
        );
        Pageable pageable = PageRequest.of(0, 10);
        Page<Shift> page = new PageImpl<>(shifts, pageable, shifts.size());
        given(shiftService.retrieveAllShifts(any(), any())).willReturn(page);

        mockMvc.perform(get(path)
                        .with(Util.authority))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.*", hasSize(2)));
    }

    @Test
    void mustRetrieveShiftByEmployeeId() throws Exception {
        List<Shift> shifts = Collections.singletonList(shift);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Shift> page = new PageImpl<>(shifts, pageable, shifts.size());
        given(shiftService.retrieveAllShifts(any(), any())).willReturn(page);

        mockMvc.perform(get(path)
                        .with(Util.authority)
                        .flashAttr("employeeId", employee.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.*", hasSize(1)));
    }

    @Test
    void mustUpdateShiftInfo() throws Exception {
        UUID id = UUID.randomUUID();
        UUID employeeId = UUID.randomUUID();
        UUID storeId = UUID.randomUUID();
        Shift shift = Shift.builder()
                .id(id)
                .employee(Employee.builder().id(employeeId).role(Role.builder().id(UUID.randomUUID()).build()).build())
                .store(Store.builder().id(storeId).build())
                .shiftDate(LocalDate.now())
                .startTime(LocalTime.of(10, 0))
                .endTime(LocalTime.of(18, 0))
                .status(ShiftStatus.COMPLETED)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        UpdateShiftRequest update = new UpdateShiftRequest(
                storeId,
                ShiftStatus.COMPLETED,
                LocalTime.of(10, 0),
                LocalTime.of(18, 0)
        );
        given(shiftService.retrieveShiftById(any(UUID.class))).willReturn(shift);
        given(shiftService.updateShiftInfo(any(UUID.class), any(Shift.class))).willReturn(shift);

        mockMvc.perform(put(String.format("%s/%s", path, id))
                        .with(Util.authority)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.startTime").value("10:00:00"))
                .andExpect(jsonPath("$.endTime").value("18:00:00"))
                .andExpect(jsonPath("$.status").value(ShiftStatus.COMPLETED.toString()));

    }

    @Test
    void mustReturn403WhenStaffCreatesShift() throws Exception {
        CreateShiftRequest request = new CreateShiftRequest(
                UUID.randomUUID(), UUID.randomUUID(), LocalDate.now(), LocalTime.of(8, 0), LocalTime.of(16, 0));

        mockMvc.perform(post(path)
                        .with(Util.staffAuthority)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void mustReturn403WhenStaffUpdatesShift() throws Exception {
        UUID id = UUID.randomUUID();
        UpdateShiftRequest update = new UpdateShiftRequest(
                UUID.randomUUID(), ShiftStatus.COMPLETED, LocalTime.of(10, 0), LocalTime.of(18, 0));

        mockMvc.perform(put(String.format("%s/%s", path, id))
                        .with(Util.staffAuthority)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(update)))
                .andExpect(status().isForbidden());
    }

    @Test
    void mustAllowStaffToRetrieveAllShifts() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Shift> page = new PageImpl<>(List.of(shift), pageable, 1);
        given(shiftService.retrieveAllShifts(any(), any())).willReturn(page);

        mockMvc.perform(get(path)
                        .with(Util.staffAuthority))
                .andExpect(status().isOk());
    }

    @Test
    void mustAllowStaffToRetrieveShiftById() throws Exception {
        UUID id = UUID.randomUUID();
        given(shiftService.retrieveShiftById(any(UUID.class))).willReturn(shift);

        mockMvc.perform(get(String.format("%s/%s", path, id))
                        .with(Util.staffAuthority))
                .andExpect(status().isOk());
    }
}
