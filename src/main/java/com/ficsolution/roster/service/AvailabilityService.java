package com.ficsolution.roster.service;

import com.ficsolution.roster.exception.ObjectNotFoundException;
import com.ficsolution.roster.model.Availability;
import com.ficsolution.roster.model.Employee;
import com.ficsolution.roster.repository.AvailabilityRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AvailabilityService {

    private final AvailabilityRepository availabilityRepository;
    private final EmployeeService employeeService;

    @Transactional
    public Availability createAvailability(Availability availability) {
        Employee employee = employeeService.retrieveEmployeeById(availability.getEmployee().getId());

        availability.setEmployee(employee);

        return availabilityRepository.save(availability);
    }

    @Transactional(readOnly = true)
    public List<Availability> findAllAvailabilities() {
        return availabilityRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Availability retrieveAvailabilityById(UUID availabilityId) {
        return availabilityRepository.findById(availabilityId).orElseThrow(() -> new ObjectNotFoundException("Availability", availabilityId.toString()));
    }

    @Transactional(readOnly = true)
    public List<Availability> retrieveAllAvailabilityByEmployeeId(UUID employeeId) {
        return availabilityRepository.findByEmployeeId(employeeId);
    }

    @Transactional
    public Availability updateAvailability(UUID availabilityId, Availability newAvailability) {
        Availability availability = retrieveAvailabilityById(availabilityId);

        availability.setAvailable(newAvailability.isAvailable());
        availability.setNote(newAvailability.getNote());
        availability.setStartTime(newAvailability.getStartTime());
        availability.setEndTime(newAvailability.getEndTime());
        availability.setWeekday(newAvailability.getWeekday());

        return availabilityRepository.save(availability);
    }

    @Transactional
    public void deleteAvailability(UUID availabilityId) {
        Availability availability = retrieveAvailabilityById(availabilityId);

        availabilityRepository.delete(availability);
    }
}
