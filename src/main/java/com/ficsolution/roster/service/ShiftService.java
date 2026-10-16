package com.ficsolution.roster.service;

import com.ficsolution.roster.exception.ObjectConflictException;
import com.ficsolution.roster.exception.ObjectNotFoundException;
import com.ficsolution.roster.exception.OperationException;
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

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ShiftService {

    private final ShiftRepository shiftRepository;
    private final EmployeeService employeeService;
    private final StoreService storeService;
    private final Clock clock;

    @Transactional
    public Shift createShift(Shift shift) {
        Employee employee = employeeService.retrieveEmployeeById(shift.getEmployee().getId());
        Store store = storeService.retrieveStoreById(shift.getStore().getId());

        validShiftTime(shift);
        validConflictShiftDateAndTime(shift.getEmployee().getId(), shift);

        shift.setEmployee(employee);
        shift.setStore(store);
        shift.setStatus(ShiftStatus.SCHEDULED);

        return shiftRepository.save(shift);
    }

    private static void validShiftTime(Shift shift) {
        if (shift.getStartTime().isAfter(shift.getEndTime()) || shift.getStartTime().equals(shift.getEndTime())) {
            throw new OperationException("Shift", "Start and End of shift time need to be a valid time.");
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

        if (shift.getStatus().isFinal()) {
            throw new OperationException("Shift", "Completed or canceled shifts can't be updated");
        }

        if (newShift.getStatus() == ShiftStatus.COMPLETED) {
            throw new OperationException("Shift", "COMPLETED is set automatically and can't be requested");
        }

        if (newShift.getStatus() == ShiftStatus.CANCELED) {
            shift.setStatus(newShift.getStatus());
        } else {
            shift.setEndTime(newShift.getEndTime());
            shift.setStartTime(newShift.getStartTime());
            validConflictShiftDateAndTime(shift.getEmployee().getId(), shift);

            if (!shift.getStore().getId().equals(newShift.getStore().getId())) {
                shift.setStore(storeService.retrieveStoreById(newShift.getStore().getId()));
            }
        }

        return shiftRepository.save(shift);
    }

    @Transactional
    public Shift swapShiftEmployee(UUID shiftId, UUID employeeId) {
        Shift shift = retrieveShiftById(shiftId);

        if (shift.getStatus().isFinal()) {
            throw new OperationException("Shift", "Completed or canceled shifts can't be updated");
        }

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

        if (shifts.stream().filter(s -> !s.getId().equals(shift.getId())).anyMatch(existingShift -> isOverlapping(existingShift, shift))) {
            throw new ObjectConflictException("ShiftTime", "There are conflicts shift time");
        }
    }

    private boolean isOverlapping(Shift existingShift, Shift newShift) {
        return existingShift.getStartTime().isBefore(newShift.getEndTime())
                && existingShift.getEndTime().isAfter(newShift.getStartTime());
    }

    @Transactional
    public int completePastShifts() {
        return shiftRepository.updateShiftStatusBeforeShiftDate(
                ShiftStatus.SCHEDULED,
                ShiftStatus.COMPLETED,
                LocalDateTime.now(),
                LocalDate.now(clock)
        );
    }

    @Transactional
    public Shift updateShiftEmployee(UUID shiftId, Employee employee) {
        Shift shift = retrieveShiftById(shiftId);

        if (shift.getStatus() == ShiftStatus.CANCELED) {
            throw new OperationException("Shift", "Operation failed, shift is canceled");
        }

        validConflictShiftDateAndTime(employee.getId(), shift);

        employee = employeeService.retrieveEmployeeById(employee.getId());

        shift.setEmployee(employee);
        shiftRepository.save(shift);

        return shift;
    }
}
