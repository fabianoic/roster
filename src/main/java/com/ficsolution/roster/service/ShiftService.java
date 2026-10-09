package com.ficsolution.roster.service;

import com.ficsolution.roster.exception.ObjectConflictException;
import com.ficsolution.roster.exception.ObjectNotFoundException;
import com.ficsolution.roster.model.Employee;
import com.ficsolution.roster.model.Shift;
import com.ficsolution.roster.model.Store;
import com.ficsolution.roster.model.enums.ShiftStatus;
import com.ficsolution.roster.repository.ShiftRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ShiftService {

    private final ShiftRepository shiftRepository;
    private final EmployeeService employeeService;
    private final StoreService storeService;

    @Transactional
    public Shift createShift(Shift shift) {
        validShiftTime(shift);
        validConflictShiftDateAndTime(shift.getEmployee().getId(), shift);

        Employee employee = employeeService.retrieveEmployeeById(shift.getEmployee().getId());
        Store store = storeService.retrieveStoreById(shift.getStore().getId());

        shift.setEmployee(employee);
        shift.setStore(store);

        return shiftRepository.save(shift);
    }

    private static void validShiftTime(Shift shift) {
        if (shift.getStartTime().isAfter(shift.getEndTime()) || shift.getStartTime().equals(shift.getEndTime())) {
            throw new ObjectConflictException("ShiftTime", "Start and End of shift time need to be a valid time.");
        }
    }

    @Transactional(readOnly = true)
    public Page<Shift> retrieveAllShifts(Specification<Shift> specification, Pageable pageable) {
        return shiftRepository.findAll(specification, pageable);
    }

    @Transactional(readOnly = true)
    public Shift retrieveShiftById(UUID id) {
        return shiftRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException("Shift", id.toString()));
    }

    @Transactional
    public Shift updateShiftInfo(UUID id, Shift newShift) {
        validShiftTime(newShift);
        Shift shift = retrieveShiftById(id);

        if (!shift.getStore().getId().equals(newShift.getStore().getId())) {
            shift.setStore(storeService.retrieveStoreById(newShift.getStore().getId()));
        }
        shift.setStatus(newShift.getStatus());
        shift.setStartTime(newShift.getStartTime());
        shift.setEndTime(newShift.getEndTime());
        shift.setUpdatedAt(LocalDateTime.now());

        return shiftRepository.save(shift);
    }

    @Transactional
    public Shift swapShiftEmployee(UUID shiftId, UUID employeeId) {
        Shift shift = retrieveShiftById(shiftId);
        Employee employee = employeeService.retrieveEmployeeById(employeeId);

        if (!employeeService.isEligibleForShift(employeeId, shift)) {
            throw new ObjectConflictException("Employee", "Employee is not available for this shift: conflicting shift, approved time off or availability restriction");
        }

        shift.setEmployee(employee);

        return shiftRepository.save(shift);
    }

    @Transactional(readOnly = true)
    public List<Employee> retrieveCandidatesToSwapShift(Shift shift) {
        if (shift.getStatus() != ShiftStatus.SCHEDULED) {
            throw new ObjectConflictException("Shift", "The action of Retrieve candidates for this request is not allowed. Need to be a SCHEDULED shift");
        }

        return employeeService.retrieveCandidatesToAShift(shift);
    }

    private void validConflictShiftDateAndTime(UUID employeeId, Shift shift) {
        List<Shift> shifts = shiftRepository.findByEmployeeIdAndShiftDateAndStatusNot(employeeId, shift.getShiftDate(), ShiftStatus.CANCELED);

        if (shifts.stream().anyMatch(existingShift -> isOverlapping(existingShift, shift))) {
            throw new ObjectConflictException("ShiftTime", "There are conflicts shift time");
        }
    }

    private boolean isOverlapping(Shift existingShift, Shift newShift) {
        return existingShift.getStartTime().isBefore(newShift.getEndTime())
                && existingShift.getEndTime().isAfter(newShift.getStartTime());
    }
}
