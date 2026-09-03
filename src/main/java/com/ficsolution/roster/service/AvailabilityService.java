package com.ficsolution.roster.service;

import com.ficsolution.roster.exception.AvailabilityNotFoundException;
import com.ficsolution.roster.model.Availability;
import com.ficsolution.roster.model.Employee;
import com.ficsolution.roster.repository.AvailabilityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AvailabilityService {

    @Autowired
    private AvailabilityRepository availabilityRepository;
    @Autowired
    private EmployeeService employeeService;

    public Availability createAvailability(Availability availability) {
        Employee employee = employeeService.retrieveEmployeeById(availability.getEmployee().getId());

        availability.setEmployee(employee);

        return availabilityRepository.save(availability);
    }

    public List<Availability> findAllAvailabilities() {
        return availabilityRepository.findAll();
    }

    public Availability retrieveAvailabilityById(UUID availabilityId) {
        return availabilityRepository.findById(availabilityId).orElseThrow(() -> new AvailabilityNotFoundException("Availability not found, id: " + availabilityId));
    }

    public List<Availability> retrieveAllAvailabilityByEmployeeId(UUID employeeId) {
        return availabilityRepository.findByEmployeeId(employeeId);
    }

    public Availability updateAvailability(UUID availabilityId, Availability newAvailability) {
        Availability availability = retrieveAvailabilityById(availabilityId);

        availability.setAvailable(newAvailability.isAvailable());
        availability.setNote(newAvailability.getNote());
        availability.setStartTime(newAvailability.getStartTime());
        availability.setEndTime(newAvailability.getEndTime());
        availability.setWeekday(newAvailability.getWeekday());

        return availabilityRepository.save(availability);
    }

    public void deleteAvailability(UUID availabilityId) {
        Availability availability = retrieveAvailabilityById(availabilityId);

        availabilityRepository.delete(availability);
    }
}
