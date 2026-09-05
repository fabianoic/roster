package com.ficsolution.roster.service;

import com.ficsolution.roster.exception.ConflictShiftException;
import com.ficsolution.roster.exception.InvalidDateTimeException;
import com.ficsolution.roster.exception.ObjectNotFoundException;
import com.ficsolution.roster.model.Employee;
import com.ficsolution.roster.model.Shift;
import com.ficsolution.roster.model.Store;
import com.ficsolution.roster.repository.ShiftRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ShiftService {

    @Autowired
    private ShiftRepository shiftRepository;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private StoreService storeService;

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
            throw new InvalidDateTimeException("Start and End of shift time need to be a valid time.");
        }
    }

    public List<Shift> retrieveAllShifts() {
        return shiftRepository.findAll();
    }

    public List<Shift> retrieveAllShiftsBetweenDates(LocalDate startDate, LocalDate endDate) {
        return shiftRepository.findByShiftDateBetween(startDate, endDate);
    }

    public List<Shift> retrieveShiftByEmployeeIdAndShiftDateBetween(UUID employeeId, LocalDate startDate, LocalDate endDate) {
        return shiftRepository.findByEmployeeIdAndShiftDateBetween(employeeId, startDate, endDate);
    }

    public Shift retrieveShiftById(UUID id) {
        return shiftRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException("Shift not found, id: " + id));
    }

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

    public Shift swapShiftEmployee(UUID shiftId, UUID employeeId) {
        Shift shift = retrieveShiftById(shiftId);
        validConflictShiftDateAndTime(employeeId, shift);

        Employee employee = employeeService.retrieveEmployeeById(employeeId);

        shift.setEmployee(employee);

        return shiftRepository.save(shift);
    }

    private void validConflictShiftDateAndTime(UUID employeeId, Shift shift) {
        List<Shift> shifts = shiftRepository.findByEmployeeIdAndShiftDate(employeeId, shift.getShiftDate());

        if (shifts.stream().anyMatch(existingShift -> isOverlapping(existingShift, shift))) {
            throw new ConflictShiftException("There are conflicts shift time");
        }
    }

    private boolean isOverlapping(Shift existingShift, Shift newShift) {
        return existingShift.getStartTime().isBefore(newShift.getEndTime())
                && existingShift.getEndTime().isAfter(newShift.getStartTime());
    }
}
