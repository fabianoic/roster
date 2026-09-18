package com.ficsolution.roster.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ficsolution.roster.config.JwtConfig;
import com.ficsolution.roster.config.SecurityConfig;
import com.ficsolution.roster.exception.ObjectConflictException;
import com.ficsolution.roster.exception.ObjectNotFoundException;
import com.ficsolution.roster.model.Employee;
import com.ficsolution.roster.model.Shift;
import com.ficsolution.roster.model.ShiftSwapRequest;
import com.ficsolution.roster.model.Store;
import com.ficsolution.roster.model.enumModel.RequestStatus;
import com.ficsolution.roster.model.enumModel.ShiftStatus;
import com.ficsolution.roster.service.ShiftSwapRequestService;
import com.ficsolution.roster.util.Util;
import com.ficsolution.roster.web.dto.shift.CreateSwapRequest;
import com.ficsolution.roster.web.dto.shift.UpdateShiftSwap;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ShiftSwapRequestController.class)
@Import({SecurityConfig.class, JwtConfig.class})
public class ShiftSwapRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ShiftSwapRequestService shiftSwapRequestService;

    private final ObjectMapper mapper = new ObjectMapper();

    private ShiftSwapRequest buildShiftSwapRequest(RequestStatus status) {
        Store store = Store.builder()
                .id(Util.storeId)
                .name("Blackrock")
                .address("Blackrock Shopping Center")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        Shift shift = Shift.builder()
                .id(Util.shiftId)
                .employee(Employee.builder().id(Util.employeeId).build())
                .store(store)
                .shiftDate(LocalDate.now())
                .startTime(LocalTime.of(8, 0))
                .endTime(LocalTime.of(16, 0))
                .status(ShiftStatus.SCHEDULED)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        Employee requester = Employee.builder().id(Util.employeeId).name("Fabiano Campos").build();
        Employee target = Employee.builder().id(Util.employee1Id).name("Pietro Silva").build();

        return ShiftSwapRequest.builder()
                .id(Util.shiftSwapRequestId)
                .shift(shift)
                .requester(requester)
                .target(target)
                .status(status)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void mustCreateSwapRequestWithSuccess() throws Exception {
        ShiftSwapRequest shiftSwapRequest = buildShiftSwapRequest(RequestStatus.PENDING);
        CreateSwapRequest request = new CreateSwapRequest(Util.shiftId, Util.employeeId, Util.employee1Id);
        given(shiftSwapRequestService.createShiftSwapRequest(any(ShiftSwapRequest.class))).willReturn(shiftSwapRequest);

        mockMvc.perform(post(String.format("/shifts/%s/swap-requests", Util.shiftId))
                        .with(Util.authority)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").value(Util.shiftSwapRequestId.toString()))
                .andExpect(jsonPath("$.status").value(RequestStatus.PENDING.toString()));
    }

    @Test
    void mustReturn400WhenCreateRequestHasMissingFields() throws Exception {
        CreateSwapRequest request = new CreateSwapRequest(null, Util.employeeId, Util.employee1Id);

        mockMvc.perform(post(String.format("/shifts/%s/swap-requests", Util.shiftId))
                        .with(Util.authority)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void mustReturn404WhenShiftNotFoundOnCreate() throws Exception {
        CreateSwapRequest request = new CreateSwapRequest(Util.shiftId, Util.employeeId, Util.employee1Id);
        given(shiftSwapRequestService.createShiftSwapRequest(any(ShiftSwapRequest.class)))
                .willThrow(ObjectNotFoundException.class);

        mockMvc.perform(post(String.format("/shifts/%s/swap-requests", Util.shiftId))
                        .with(Util.authority)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void mustReturn409WhenRequesterIsNotShiftOwner() throws Exception {
        CreateSwapRequest request = new CreateSwapRequest(Util.shiftId, Util.employeeId, Util.employee1Id);
        given(shiftSwapRequestService.createShiftSwapRequest(any(ShiftSwapRequest.class)))
                .willThrow(ObjectConflictException.class);

        mockMvc.perform(post(String.format("/shifts/%s/swap-requests", Util.shiftId))
                        .with(Util.authority)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void mustReturn401WhenNotAuthorized() throws Exception {
        CreateSwapRequest request = new CreateSwapRequest(Util.shiftId, Util.employeeId, Util.employee1Id);

        mockMvc.perform(post(String.format("/shifts/%s/swap-requests", Util.shiftId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void mustUpdateShiftSwapStatusWithSuccess() throws Exception {
        ShiftSwapRequest shiftSwapRequest = buildShiftSwapRequest(RequestStatus.APPROVED);
        UpdateShiftSwap update = new UpdateShiftSwap(RequestStatus.APPROVED, Util.employee1Id);
        given(shiftSwapRequestService.changeStatus(any(UUID.class), any(UUID.class), any(RequestStatus.class)))
                .willReturn(shiftSwapRequest);

        mockMvc.perform(put(String.format("/swap-requests/%s", Util.shiftSwapRequestId))
                        .with(Util.authority)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(Util.shiftSwapRequestId.toString()))
                .andExpect(jsonPath("$.status").value(RequestStatus.APPROVED.toString()));
    }

    @Test
    void mustReturn400WhenUpdateRequestHasMissingFields() throws Exception {
        UpdateShiftSwap update = new UpdateShiftSwap(null, Util.employee1Id);

        mockMvc.perform(put(String.format("/swap-requests/%s", Util.shiftSwapRequestId))
                        .with(Util.authority)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(update)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void mustReturn404WhenShiftSwapRequestNotFound() throws Exception {
        UpdateShiftSwap update = new UpdateShiftSwap(RequestStatus.APPROVED, Util.employee1Id);
        given(shiftSwapRequestService.changeStatus(any(UUID.class), any(UUID.class), any(RequestStatus.class)))
                .willThrow(ObjectNotFoundException.class);

        mockMvc.perform(put(String.format("/swap-requests/%s", Util.shiftSwapRequestId))
                        .with(Util.authority)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(update)))
                .andExpect(status().isNotFound());
    }

    @Test
    void mustReturn409WhenChangeStatusNotAllowed() throws Exception {
        UpdateShiftSwap update = new UpdateShiftSwap(RequestStatus.APPROVED, Util.employee1Id);
        given(shiftSwapRequestService.changeStatus(any(UUID.class), any(UUID.class), any(RequestStatus.class)))
                .willThrow(ObjectConflictException.class);

        mockMvc.perform(put(String.format("/swap-requests/%s", Util.shiftSwapRequestId))
                        .with(Util.authority)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(update)))
                .andExpect(status().isConflict());
    }

    @Test
    void mustRetrieveShiftSwapRequestById() throws Exception {
        ShiftSwapRequest shiftSwapRequest = buildShiftSwapRequest(RequestStatus.PENDING);
        given(shiftSwapRequestService.retrieveShiftSwapRequestById(Util.shiftSwapRequestId)).willReturn(shiftSwapRequest);

        mockMvc.perform(get(String.format("/swap-requests/%s", Util.shiftSwapRequestId))
                        .with(Util.authority))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(Util.shiftSwapRequestId.toString()))
                .andExpect(jsonPath("$.status").value(RequestStatus.PENDING.toString()));
    }

    @Test
    void mustReturn404WhenRetrievingNonExistentShiftSwapRequest() throws Exception {
        given(shiftSwapRequestService.retrieveShiftSwapRequestById(Util.shiftSwapRequestId))
                .willThrow(ObjectNotFoundException.class);

        mockMvc.perform(get(String.format("/swap-requests/%s", Util.shiftSwapRequestId))
                        .with(Util.authority))
                .andExpect(status().isNotFound());
    }

    @Test
    void mustDeleteShiftSwapRequest() throws Exception {
        doNothing().when(shiftSwapRequestService).deleteShiftSwapRequest(Util.shiftSwapRequestId);

        mockMvc.perform(delete(String.format("/swap-requests/%s", Util.shiftSwapRequestId))
                        .with(Util.authority))
                .andExpect(status().isNoContent());
    }

    @Test
    void mustReturn404WhenDeletingNonExistentShiftSwapRequest() throws Exception {
        org.mockito.BDDMockito.willThrow(ObjectNotFoundException.class)
                .given(shiftSwapRequestService).deleteShiftSwapRequest(Util.shiftSwapRequestId);

        mockMvc.perform(delete(String.format("/swap-requests/%s", Util.shiftSwapRequestId))
                        .with(Util.authority))
                .andExpect(status().isNotFound());
    }

    @Test
    void mustReturn409WhenDeletingNonPendingShiftSwapRequest() throws Exception {
        org.mockito.BDDMockito.willThrow(ObjectConflictException.class)
                .given(shiftSwapRequestService).deleteShiftSwapRequest(Util.shiftSwapRequestId);

        mockMvc.perform(delete(String.format("/swap-requests/%s", Util.shiftSwapRequestId))
                        .with(Util.authority))
                .andExpect(status().isConflict());
    }
}
