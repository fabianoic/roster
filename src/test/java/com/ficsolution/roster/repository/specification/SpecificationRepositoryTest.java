package com.ficsolution.roster.repository.specification;

import com.ficsolution.roster.TestcontainersConfiguration;
import com.ficsolution.roster.model.Availability;
import com.ficsolution.roster.model.Permission;
import com.ficsolution.roster.model.Role;
import com.ficsolution.roster.model.ShiftSwapRequest;
import com.ficsolution.roster.model.Store;
import com.ficsolution.roster.model.TimeOffRequest;
import com.ficsolution.roster.model.enums.RequestStatus;
import com.ficsolution.roster.model.enums.TimeOffRequestType;
import com.ficsolution.roster.repository.AvailabilityRepository;
import com.ficsolution.roster.repository.PermissionRepository;
import com.ficsolution.roster.repository.RoleRepository;
import com.ficsolution.roster.repository.ShiftSwapRequestRepository;
import com.ficsolution.roster.repository.StoreRepository;
import com.ficsolution.roster.repository.TimeOffRequestRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

import static com.ficsolution.roster.util.Util.*;
import static org.junit.jupiter.api.Assertions.*;

// Runs the specifications against the rows seeded by V2__seed_tables.sql and V4__permissions.sql
@DataJpaTest
@Import(TestcontainersConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class SpecificationRepositoryTest {

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private AvailabilityRepository availabilityRepository;

    @Autowired
    private TimeOffRequestRepository timeOffRequestRepository;

    @Autowired
    private ShiftSwapRequestRepository shiftSwapRequestRepository;

    @Test
    void testStoreNameContainsIgnoringCase() {
        Specification<Store> specification = Specification.allOf(StoreSpecification.nameContains("cENTRO"));

        Page<Store> stores = storeRepository.findAll(specification, PageRequest.of(0, 10));

        assertEquals(1, stores.getTotalElements());
        assertEquals(storeId, stores.getContent().getFirst().getId());
    }

    @Test
    void testStoreAddressContains() {
        Specification<Store> specification = Specification.allOf(StoreSpecification.addressContains("copacabana"));

        Page<Store> stores = storeRepository.findAll(specification, PageRequest.of(0, 10));

        assertEquals(1, stores.getTotalElements());
        assertEquals("Loja Zona Sul", stores.getContent().getFirst().getName());
    }

    @Test
    void testStoreWithoutFiltersIsPagedAndSorted() {
        Specification<Store> specification = Specification.allOf(
                StoreSpecification.nameContains(null),
                StoreSpecification.addressContains(null)
        );

        Page<Store> stores = storeRepository.findAll(specification, PageRequest.of(0, 2, Sort.by("name")));
        long total = storeRepository.count();

        assertEquals(total, stores.getTotalElements());
        assertEquals(2, stores.getContent().size());
        assertEquals((int) Math.ceil(total / 2.0), stores.getTotalPages());
        List<String> names = stores.getContent().stream().map(Store::getName).toList();
        assertEquals(names.stream().sorted().toList(), names);
    }

    @Test
    void testRoleNameContains() {
        Specification<Role> specification = Specification.allOf(RoleSpecification.nameContains("manager"));

        Page<Role> roles = roleRepository.findAll(specification, PageRequest.of(0, 10));

        assertEquals(1, roles.getTotalElements());
        assertEquals(roleId, roles.getContent().getFirst().getId());
    }

    @Test
    void testPermissionNameContainsSortedByName() {
        Specification<Permission> specification = Specification.allOf(PermissionSpecification.nameContains("shift_"));

        Page<Permission> permissions = permissionRepository.findAll(specification, PageRequest.of(0, 10, Sort.by("name")));

        assertEquals(List.of("SHIFT_READ", "SHIFT_WRITE"),
                permissions.getContent().stream().map(Permission::getName).toList());
    }

    @Test
    void testAvailabilityByEmployeeAndWeekday() {
        Specification<Availability> specification = Specification.allOf(
                AvailabilitySpecification.hasEmployeeId(employee1Id),
                AvailabilitySpecification.hasWeekday(DayOfWeek.MONDAY)
        );

        Page<Availability> availabilities = availabilityRepository.findAll(specification, PageRequest.of(0, 10));

        assertEquals(1, availabilities.getTotalElements());
        assertEquals(availabilityId, availabilities.getContent().getFirst().getId());
    }

    @Test
    void testAvailabilityByWeekdayWithoutMatches() {
        Specification<Availability> specification = Specification.allOf(
                AvailabilitySpecification.hasEmployeeId(employee1Id),
                AvailabilitySpecification.hasWeekday(DayOfWeek.SUNDAY)
        );

        Page<Availability> availabilities = availabilityRepository.findAll(specification, PageRequest.of(0, 10));

        assertTrue(availabilities.isEmpty());
    }

    @Test
    void testAvailabilityIsAvailable() {
        Specification<Availability> specification = Specification.allOf(AvailabilitySpecification.isAvailable(false));

        Page<Availability> availabilities = availabilityRepository.findAll(specification, PageRequest.of(0, 10));

        assertFalse(availabilities.isEmpty());
        assertTrue(availabilities.getContent().stream().noneMatch(Availability::isAvailable));
    }

    @Test
    void testTimeOffRequestByEmployeeId() {
        Specification<TimeOffRequest> specification = Specification.allOf(TimeOffRequestSpecification.hasEmployeeId(employee1Id));

        Page<TimeOffRequest> timeOffRequests = timeOffRequestRepository.findAll(specification, PageRequest.of(0, 10));

        assertEquals(1, timeOffRequests.getTotalElements());
        assertEquals(timeOffRequestId, timeOffRequests.getContent().getFirst().getId());
    }

    @Test
    void testTimeOffRequestByStatusAndType() {
        Specification<TimeOffRequest> specification = Specification.allOf(
                TimeOffRequestSpecification.hasStatus(RequestStatus.PENDING),
                TimeOffRequestSpecification.hasType(TimeOffRequestType.HOLIDAY)
        );

        Page<TimeOffRequest> timeOffRequests = timeOffRequestRepository.findAll(specification, PageRequest.of(0, 10));

        assertEquals(2, timeOffRequests.getTotalElements());
        assertTrue(timeOffRequests.getContent().stream()
                .allMatch(t -> t.getStatus() == RequestStatus.PENDING && t.getType() == TimeOffRequestType.HOLIDAY));
    }

    @Test
    void testTimeOffRequestOverlappingPeriod() {
        // Seeds: +5..+6, +10..+15, +20..+30, +40..+45 days from today
        LocalDate today = LocalDate.now();
        Specification<TimeOffRequest> specification = Specification.allOf(
                TimeOffRequestSpecification.overlapsPeriod(today.plusDays(12), today.plusDays(21))
        );

        Page<TimeOffRequest> timeOffRequests = timeOffRequestRepository.findAll(specification, PageRequest.of(0, 10, Sort.by("startDate")));

        assertEquals(2, timeOffRequests.getTotalElements());
        assertEquals(timeOffRequestId, timeOffRequests.getContent().getFirst().getId());
        assertEquals(today.plusDays(20), timeOffRequests.getContent().get(1).getStartDate());
    }

    @Test
    void testTimeOffRequestOverlappingOpenEndedPeriod() {
        LocalDate today = LocalDate.now();

        long fromDay35 = timeOffRequestRepository.count(Specification.allOf(
                TimeOffRequestSpecification.overlapsPeriod(today.plusDays(35), null)));
        long untilDay7 = timeOffRequestRepository.count(Specification.allOf(
                TimeOffRequestSpecification.overlapsPeriod(null, today.plusDays(7))));

        assertEquals(1, fromDay35);
        assertEquals(1, untilDay7);
    }

    @Test
    void testShiftSwapRequestByRequesterAndStatus() {
        Specification<ShiftSwapRequest> specification = Specification.allOf(
                ShiftSwapRequestSpecification.hasRequesterId(employee1Id),
                ShiftSwapRequestSpecification.hasStatus(RequestStatus.PENDING)
        );

        Page<ShiftSwapRequest> shiftSwapRequests = shiftSwapRequestRepository.findAll(specification, PageRequest.of(0, 10));

        assertEquals(1, shiftSwapRequests.getTotalElements());
        assertEquals(shiftSwapRequestId, shiftSwapRequests.getContent().getFirst().getId());
    }

    @Test
    void testShiftSwapRequestByTarget() {
        Specification<ShiftSwapRequest> specification = Specification.allOf(ShiftSwapRequestSpecification.hasTargetId(employee1Id));

        Page<ShiftSwapRequest> shiftSwapRequests = shiftSwapRequestRepository.findAll(specification, PageRequest.of(0, 10));

        assertEquals(1, shiftSwapRequests.getTotalElements());
        assertEquals(RequestStatus.APPROVED, shiftSwapRequests.getContent().getFirst().getStatus());
    }

    @Test
    void testShiftSwapRequestByShiftId() {
        ShiftSwapRequest seeded = shiftSwapRequestRepository.findById(shiftSwapRequestId).orElseThrow();
        Specification<ShiftSwapRequest> specification = Specification.allOf(
                ShiftSwapRequestSpecification.hasShiftId(seeded.getShift().getId())
        );

        Page<ShiftSwapRequest> shiftSwapRequests = shiftSwapRequestRepository.findAll(specification, PageRequest.of(0, 10));

        assertTrue(shiftSwapRequests.getContent().stream().anyMatch(s -> s.getId().equals(shiftSwapRequestId)));
        assertTrue(shiftSwapRequests.getContent().stream().allMatch(s -> s.getShift().getId().equals(seeded.getShift().getId())));
    }
}
