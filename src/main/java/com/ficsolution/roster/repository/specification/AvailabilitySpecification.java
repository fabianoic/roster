package com.ficsolution.roster.repository.specification;

import com.ficsolution.roster.model.Availability;
import org.springframework.data.jpa.domain.Specification;

import java.time.DayOfWeek;
import java.util.UUID;

public class AvailabilitySpecification {

    public static Specification<Availability> hasEmployeeId(UUID employeeId) {
        return ((root, _, criteriaBuilder) ->
                employeeId == null ? null : criteriaBuilder.equal(root.get("employee").get("id"), employeeId));
    }

    public static Specification<Availability> hasWeekday(DayOfWeek weekday) {
        return ((root, _, criteriaBuilder) ->
                weekday == null ? null : criteriaBuilder.equal(root.get("weekday"), weekday));
    }

    public static Specification<Availability> isAvailable(Boolean available) {
        return ((root, _, criteriaBuilder) ->
                available == null ? null : criteriaBuilder.equal(root.get("isAvailable"), available));
    }
}
