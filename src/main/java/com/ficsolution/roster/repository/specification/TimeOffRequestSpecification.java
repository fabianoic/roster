package com.ficsolution.roster.repository.specification;

import com.ficsolution.roster.model.TimeOffRequest;
import com.ficsolution.roster.model.enums.RequestStatus;
import com.ficsolution.roster.model.enums.TimeOffRequestType;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.UUID;

public class TimeOffRequestSpecification {

    public static Specification<TimeOffRequest> hasEmployeeId(UUID employeeId) {
        return ((root, _, criteriaBuilder) ->
                employeeId == null ? null : criteriaBuilder.equal(root.get("employee").get("id"), employeeId));
    }

    public static Specification<TimeOffRequest> hasStatus(RequestStatus status) {
        return ((root, _, criteriaBuilder) ->
                status == null ? null : criteriaBuilder.equal(root.get("status"), status));
    }

    public static Specification<TimeOffRequest> hasType(TimeOffRequestType type) {
        return ((root, _, criteriaBuilder) ->
                type == null ? null : criteriaBuilder.equal(root.get("type"), type));
    }

    // Matches requests whose period overlaps [start, end]
    public static Specification<TimeOffRequest> overlapsPeriod(LocalDate start, LocalDate end) {
        return ((root, _, criteriaBuilder) ->
        {
            if (start != null && end != null) return criteriaBuilder.and(
                    criteriaBuilder.greaterThanOrEqualTo(root.get("endDate"), start),
                    criteriaBuilder.lessThanOrEqualTo(root.get("startDate"), end));
            if (start != null) return criteriaBuilder.greaterThanOrEqualTo(root.get("endDate"), start);
            if (end != null) return criteriaBuilder.lessThanOrEqualTo(root.get("startDate"), end);
            return null;
        });
    }
}
