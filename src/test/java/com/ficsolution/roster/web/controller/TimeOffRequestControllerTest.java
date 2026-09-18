package com.ficsolution.roster.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ficsolution.roster.config.JwtConfig;
import com.ficsolution.roster.config.SecurityConfig;
import com.ficsolution.roster.exception.ObjectNotFoundException;
import com.ficsolution.roster.model.Employee;
import com.ficsolution.roster.model.TimeOffRequest;
import com.ficsolution.roster.model.enumModel.RequestStatus;
import com.ficsolution.roster.model.enumModel.TimeOffRequestType;
import com.ficsolution.roster.service.TimeOffRequestService;
import com.ficsolution.roster.util.Util;
import com.ficsolution.roster.web.dto.timeoff.CreateTimeOffRequest;
import com.ficsolution.roster.web.dto.timeoff.UpdateTimeOffRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

@WebMvcTest(TimeOffRequestController.class)
@Import({SecurityConfig.class, JwtConfig.class})
public class TimeOffRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TimeOffRequestService timeOffRequestService;

    private final ObjectMapper mapper = new ObjectMapper();
    private final String path = "/time-off-requests";

    private TimeOffRequest buildTimeOffRequest(UUID id, RequestStatus status) {
        return TimeOffRequest.builder()
                .id(id)
                .employee(Employee.builder().id(Util.employeeId).name("Fabiano Campos").build())
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(2))
                .reason("I'll move")
                .type(TimeOffRequestType.PERSONAL)
                .status(status)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void mustCreateTimeOffRequestWithSuccess() throws Exception {
        UUID id = UUID.randomUUID();
        TimeOffRequest timeOffRequest = buildTimeOffRequest(id, RequestStatus.PENDING);
        CreateTimeOffRequest request = new CreateTimeOffRequest(
                Util.employeeId,
                LocalDate.now(),
                LocalDate.now().plusDays(2),
                "I'll move",
                TimeOffRequestType.PERSONAL
        );
        given(timeOffRequestService.createTimeOffRequest(any(TimeOffRequest.class))).willReturn(timeOffRequest);

        mockMvc.perform(post(path)
                        .with(Util.authority)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.type").value(TimeOffRequestType.PERSONAL.toString()))
                .andExpect(jsonPath("$.status").value(RequestStatus.PENDING.toString()));
    }

    @Test
    void mustReturn400WhenCreateRequestHasMissingFields() throws Exception {
        CreateTimeOffRequest request = new CreateTimeOffRequest(
                null,
                LocalDate.now(),
                LocalDate.now().plusDays(2),
                "I'll move",
                TimeOffRequestType.PERSONAL
        );

        mockMvc.perform(post(path)
                        .with(Util.authority)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void mustReturn404WhenEmployeeNotFoundOnCreate() throws Exception {
        CreateTimeOffRequest request = new CreateTimeOffRequest(
                Util.employeeId,
                LocalDate.now(),
                LocalDate.now().plusDays(2),
                "I'll move",
                TimeOffRequestType.PERSONAL
        );
        given(timeOffRequestService.createTimeOffRequest(any(TimeOffRequest.class)))
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
    void mustRetrieveAllTimeOffRequests() throws Exception {
        List<TimeOffRequest> timeOffRequests = List.of(
                buildTimeOffRequest(UUID.randomUUID(), RequestStatus.PENDING),
                buildTimeOffRequest(UUID.randomUUID(), RequestStatus.APPROVED)
        );
        given(timeOffRequestService.findAllTimeOffRequests()).willReturn(timeOffRequests);

        mockMvc.perform(get(path)
                        .with(Util.authority))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(2)));
    }

    @Test
    void mustRetrieveTimeOffRequestsByEmployeeId() throws Exception {
        List<TimeOffRequest> timeOffRequests = List.of(buildTimeOffRequest(UUID.randomUUID(), RequestStatus.PENDING));
        given(timeOffRequestService.retrieveAllTimeOffRequestsByEmployeeId(Util.employeeId)).willReturn(timeOffRequests);

        mockMvc.perform(get(path)
                        .with(Util.authority)
                        .param("employeeId", Util.employeeId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(1)));
    }

    @Test
    void mustRetrieveTimeOffRequestById() throws Exception {
        UUID id = UUID.randomUUID();
        TimeOffRequest timeOffRequest = buildTimeOffRequest(id, RequestStatus.PENDING);
        given(timeOffRequestService.retrieveTimeOffRequestById(id)).willReturn(timeOffRequest);

        mockMvc.perform(get(String.format("%s/%s", path, id))
                        .with(Util.authority))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.employee.id").value(Util.employeeId.toString()));
    }

    @Test
    void mustReturn404WhenIdNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        given(timeOffRequestService.retrieveTimeOffRequestById(id)).willThrow(ObjectNotFoundException.class);

        mockMvc.perform(get(String.format("%s/%s", path, id))
                        .with(Util.authority))
                .andExpect(status().isNotFound());
    }

    @Test
    void mustUpdateTimeOffRequestWithSuccess() throws Exception {
        UUID id = UUID.randomUUID();
        TimeOffRequest updated = buildTimeOffRequest(id, RequestStatus.APPROVED);
        UpdateTimeOffRequest request = new UpdateTimeOffRequest(
                LocalDate.now(),
                LocalDate.now().plusDays(2),
                "new reason",
                TimeOffRequestType.PERSONAL,
                RequestStatus.APPROVED
        );
        given(timeOffRequestService.updateTimeOffRequest(any(UUID.class), any(TimeOffRequest.class))).willReturn(updated);

        mockMvc.perform(put(String.format("%s/%s", path, id))
                        .with(Util.authority)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.status").value(RequestStatus.APPROVED.toString()));
    }

    @Test
    void mustReturn400WhenUpdateRequestHasMissingFields() throws Exception {
        UUID id = UUID.randomUUID();
        UpdateTimeOffRequest request = new UpdateTimeOffRequest(
                null,
                LocalDate.now().plusDays(2),
                "new reason",
                TimeOffRequestType.PERSONAL,
                RequestStatus.APPROVED
        );

        mockMvc.perform(put(String.format("%s/%s", path, id))
                        .with(Util.authority)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void mustReturn404WhenUpdatingNonExistentRequest() throws Exception {
        UUID id = UUID.randomUUID();
        UpdateTimeOffRequest request = new UpdateTimeOffRequest(
                LocalDate.now(),
                LocalDate.now().plusDays(2),
                "new reason",
                TimeOffRequestType.PERSONAL,
                RequestStatus.APPROVED
        );
        given(timeOffRequestService.updateTimeOffRequest(any(UUID.class), any(TimeOffRequest.class)))
                .willThrow(ObjectNotFoundException.class);

        mockMvc.perform(put(String.format("%s/%s", path, id))
                        .with(Util.authority)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void mustDeleteTimeOffRequest() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(timeOffRequestService).deleteTimeOffRequest(id);

        mockMvc.perform(delete(String.format("%s/%s", path, id))
                        .with(Util.authority))
                .andExpect(status().isNoContent());
    }

    @Test
    void mustReturn404WhenDeletingNonExistentRequest() throws Exception {
        UUID id = UUID.randomUUID();
        org.mockito.BDDMockito.willThrow(ObjectNotFoundException.class)
                .given(timeOffRequestService).deleteTimeOffRequest(id);

        mockMvc.perform(delete(String.format("%s/%s", path, id))
                        .with(Util.authority))
                .andExpect(status().isNotFound());
    }
}
