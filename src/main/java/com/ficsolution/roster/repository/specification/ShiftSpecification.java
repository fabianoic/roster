package com.ficsolution.roster.repository.specification;

import com.ficsolution.roster.model.Shift;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.UUID;

public class ShiftSpecification {

    public static Specification<Shift> hasEmployeeId(UUID employeeId) {
        return ((root, _, criteriaBuilder) ->
                employeeId == null ? null : criteriaBuilder.equal(root.get("employee").get("id"), employeeId));
    }

    public static Specification<Shift> hasStoreId(UUID storeId) {
        return ((root, _, criteriaBuilder) ->
                storeId == null ? null : criteriaBuilder.equal(root.get("store").get("id"), storeId));
    }

    public static Specification<Shift> shiftDateBetween(LocalDate start, LocalDate end) {
        return ((root, _, criteriaBuilder) ->
        {
            if (start != null && end != null) return criteriaBuilder.between(root.get("shiftDate"), start, end);
            if (start != null) return criteriaBuilder.greaterThanOrEqualTo(root.get("shiftDate"), start);
            if (end != null) return criteriaBuilder.lessThanOrEqualTo(root.get("shiftDate"), end);
            return null;
        });
    }
}
