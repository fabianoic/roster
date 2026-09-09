package com.ficsolution.roster.repository;

import com.ficsolution.roster.model.ShiftSwapRequest;
import com.ficsolution.roster.model.enumModel.RequestStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.testcontainers.utility.TestcontainersConfiguration;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.ficsolution.roster.util.Util.*;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(TestcontainersConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ShiftSwapRequestRepositoryTest {

    @Autowired
    private ShiftSwapRequestRepository shiftSwapRequestRepository;

    @Autowired
    private ShiftRepository shiftRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Test
    void testCreateShiftSwapRequest() {
        ShiftSwapRequest shiftSwapRequest = new ShiftSwapRequest(
                null,
                shiftRepository.findById(shiftId).get(),
                employeeRepository.findById(employeeId).get(),
                employeeRepository.findById(employee1Id).get(),
                RequestStatus.PENDING,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        ShiftSwapRequest savedShiftSwapRequest = shiftSwapRequestRepository.save(shiftSwapRequest);

        assertNotNull(savedShiftSwapRequest);
        assertNotNull(savedShiftSwapRequest.getId());
    }

    @Test
    void testRetrieveAllShiftSwapRequests() {
        List<ShiftSwapRequest> shiftSwapRequestList = shiftSwapRequestRepository.findAll();

        assertNotNull(shiftSwapRequestList);
        assertFalse(shiftSwapRequestList.isEmpty());
    }

    @Test
    void testRetrieveShiftSwapRequestById() {
        Optional<ShiftSwapRequest> retrievedShift = shiftSwapRequestRepository.findById(shiftSwapRequestId);

        assertFalse(retrievedShift.isEmpty());
        assertEquals(RequestStatus.PENDING, retrievedShift.get().getStatus());
        assertEquals(UUID.fromString("8d7ec490-9fdc-4a5f-b261-82e53bc74d7f"), retrievedShift.get().getRequester().getId());
        assertEquals(UUID.fromString("7f491384-efd0-43de-ab12-82d1aaf11072"), retrievedShift.get().getTarget().getId());
    }

    @Test
    void testRetrieveAllShiftSwapRequestsByRequesterId() {
        List<ShiftSwapRequest> shiftSwapRequestList = shiftSwapRequestRepository.findByRequesterId(employee1Id);

        assertFalse(shiftSwapRequestList.isEmpty());
        assertEquals(RequestStatus.PENDING, shiftSwapRequestList.getFirst().getStatus());
    }

    @Test
    void testUpdateShiftSwapRequest() {
        ShiftSwapRequest shiftSwapRequest = shiftSwapRequestRepository.findById(shiftSwapRequestId).get();

        shiftSwapRequest.setStatus(RequestStatus.APPROVED);
        shiftSwapRequest.setUpdatedAt(LocalDateTime.now());

        ShiftSwapRequest updatedShiftSwapRequest = shiftSwapRequestRepository.save(shiftSwapRequest);

        assertNotNull(updatedShiftSwapRequest);
        assertEquals(RequestStatus.APPROVED, updatedShiftSwapRequest.getStatus());
        assertNotEquals(shiftSwapRequest.getCreatedAt(), updatedShiftSwapRequest.getUpdatedAt());
    }

    @Test
    void testDeleteShiftSwapRequest() {
        shiftSwapRequestRepository.deleteById(shiftSwapRequestId);

        assertFalse(shiftSwapRequestRepository.findById(shiftSwapRequestId).isPresent());
    }
}
