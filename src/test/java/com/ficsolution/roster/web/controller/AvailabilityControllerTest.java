package com.ficsolution.roster.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ficsolution.roster.config.JwtConfig;
import com.ficsolution.roster.config.SecurityConfig;
import com.ficsolution.roster.exception.ObjectNotFoundException;
import com.ficsolution.roster.model.Availability;
import com.ficsolution.roster.model.Employee;
import com.ficsolution.roster.service.AvailabilityService;
import com.ficsolution.roster.util.Util;
import com.ficsolution.roster.web.dto.availability.CreateAvailabilityRequest;
import com.ficsolution.roster.web.dto.availability.UpdateAvailabilityRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AvailabilityController.class)
@Import({SecurityConfig.class, JwtConfig.class})
public class AvailabilityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AvailabilityService availabilityService;

    private final ObjectMapper mapper = new ObjectMapper();
    private final String path = "/availabilities";

    private Availability buildAvailability(UUID id, DayOfWeek weekday, boolean isAvailable) {
        return new Availability(
                id,
                Employee.builder().id(Util.employeeId).name("Fabiano Campos").build(),
                weekday,
                isAvailable,
                "test reason for tests.",
                LocalTime.of(0, 0),
                LocalTime.of(23, 59)
        );
    }

    @Test
    void mustCreateAvailabilityWithSuccess() throws Exception {
        UUID id = UUID.randomUUID();
        Availability availability = buildAvailability(id, DayOfWeek.THURSDAY, false);
        CreateAvailabilityRequest request = new CreateAvailabilityRequest(
                Util.employeeId,
                DayOfWeek.THURSDAY,
                false,
                "test reason for tests.",
                LocalTime.of(0, 0),
                LocalTime.of(23, 59)
        );
        given(availabilityService.createAvailability(any(Availability.class))).willReturn(availability);

        mockMvc.perform(post(path)
                        .with(Util.authority)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.weekday").value(DayOfWeek.THURSDAY.toString()))
                .andExpect(jsonPath("$.available").value(false));
    }

    @Test
    void mustReturn400WhenCreateRequestHasMissingFields() throws Exception {
        CreateAvailabilityRequest request = new CreateAvailabilityRequest(
                null,
                DayOfWeek.THURSDAY,
                false,
                "test reason for tests.",
                LocalTime.of(0, 0),
                LocalTime.of(23, 59)
        );

        mockMvc.perform(post(path)
                        .with(Util.authority)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void mustReturn404WhenEmployeeNotFoundOnCreate() throws Exception {
        CreateAvailabilityRequest request = new CreateAvailabilityRequest(
                Util.employeeId,
                DayOfWeek.THURSDAY,
                false,
                "test reason for tests.",
                LocalTime.of(0, 0),
                LocalTime.of(23, 59)
        );
        given(availabilityService.createAvailability(any(Availability.class)))
                .willThrow(ObjectNotFoundException.class);

        mockMvc.perform(post(path)
                        .with(Util.authority)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void mustReturn401WhenNotAuthorized() throws Exception {
        mockMvc.perform(get(path))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void mustRetrieveAllAvailabilities() throws Exception {
        List<Availability> availabilities = List.of(
                buildAvailability(UUID.randomUUID(), DayOfWeek.THURSDAY, false),
                buildAvailability(UUID.randomUUID(), DayOfWeek.FRIDAY, true)
        );
        given(availabilityService.findAllAvailabilities()).willReturn(availabilities);

        mockMvc.perform(get(path)
                        .with(Util.authority))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(2)));
    }

    @Test
    void mustRetrieveAvailabilitiesByEmployeeId() throws Exception {
        List<Availability> availabilities = List.of(buildAvailability(UUID.randomUUID(), DayOfWeek.THURSDAY, false));
        given(availabilityService.retrieveAllAvailabilityByEmployeeId(Util.employeeId)).willReturn(availabilities);

        mockMvc.perform(get(path)
                        .with(Util.authority)
                        .param("employeeId", Util.employeeId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(1)));
    }

    @Test
    void mustRetrieveAvailabilityById() throws Exception {
        UUID id = UUID.randomUUID();
        Availability availability = buildAvailability(id, DayOfWeek.THURSDAY, false);
        given(availabilityService.retrieveAvailabilityById(id)).willReturn(availability);

        mockMvc.perform(get(String.format("%s/%s", path, id))
                        .with(Util.authority))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.employee.id").value(Util.employeeId.toString()));
    }

    @Test
    void mustReturn404WhenIdNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        given(availabilityService.retrieveAvailabilityById(id)).willThrow(ObjectNotFoundException.class);

        mockMvc.perform(get(String.format("%s/%s", path, id))
                        .with(Util.authority))
                .andExpect(status().isNotFound());
    }

    @Test
    void mustUpdateAvailabilityWithSuccess() throws Exception {
        UUID id = UUID.randomUUID();
        Availability updated = buildAvailability(id, DayOfWeek.MONDAY, true);
        UpdateAvailabilityRequest request = new UpdateAvailabilityRequest(
                DayOfWeek.MONDAY,
                true,
                "test reason for tests.",
                LocalTime.of(0, 0),
                LocalTime.of(23, 59)
        );
        given(availabilityService.updateAvailability(any(UUID.class), any(Availability.class))).willReturn(updated);

        mockMvc.perform(put(String.format("%s/%s", path, id))
                        .with(Util.authority)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.weekday").value(DayOfWeek.MONDAY.toString()))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    void mustReturn400WhenUpdateRequestHasMissingFields() throws Exception {
        UUID id = UUID.randomUUID();
        UpdateAvailabilityRequest request = new UpdateAvailabilityRequest(
                null,
                true,
                "test reason for tests.",
                LocalTime.of(0, 0),
                LocalTime.of(23, 59)
        );

        mockMvc.perform(put(String.format("%s/%s", path, id))
                        .with(Util.authority)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void mustReturn404WhenUpdatingNonExistentAvailability() throws Exception {
        UUID id = UUID.randomUUID();
        UpdateAvailabilityRequest request = new UpdateAvailabilityRequest(
                DayOfWeek.MONDAY,
                true,
                "test reason for tests.",
                LocalTime.of(0, 0),
                LocalTime.of(23, 59)
        );
        given(availabilityService.updateAvailability(any(UUID.class), any(Availability.class)))
                .willThrow(ObjectNotFoundException.class);

        mockMvc.perform(put(String.format("%s/%s", path, id))
                        .with(Util.authority)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void mustDeleteAvailability() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(availabilityService).deleteAvailability(id);

        mockMvc.perform(delete(String.format("%s/%s", path, id))
                        .with(Util.authority))
                .andExpect(status().isNoContent());
    }

    @Test
    void mustReturn404WhenDeletingNonExistentAvailability() throws Exception {
        UUID id = UUID.randomUUID();
        org.mockito.BDDMockito.willThrow(ObjectNotFoundException.class)
                .given(availabilityService).deleteAvailability(id);

        mockMvc.perform(delete(String.format("%s/%s", path, id))
                        .with(Util.authority))
                .andExpect(status().isNotFound());
    }
}
