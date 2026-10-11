package com.ficsolution.roster.repository.specification;

import com.ficsolution.roster.model.ShiftSwapRequest;
import com.ficsolution.roster.model.enums.RequestStatus;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public class ShiftSwapRequestSpecification {

    public static Specification<ShiftSwapRequest> hasShiftId(UUID shiftId) {
        return ((root, _, criteriaBuilder) ->
                shiftId == null ? null : criteriaBuilder.equal(root.get("shift").get("id"), shiftId));
    }

    public static Specification<ShiftSwapRequest> hasRequesterId(UUID requesterId) {
        return ((root, _, criteriaBuilder) ->
                requesterId == null ? null : criteriaBuilder.equal(root.get("requester").get("id"), requesterId));
    }

    public static Specification<ShiftSwapRequest> hasTargetId(UUID targetId) {
        return ((root, _, criteriaBuilder) ->
                targetId == null ? null : criteriaBuilder.equal(root.get("target").get("id"), targetId));
    }

    public static Specification<ShiftSwapRequest> hasStatus(RequestStatus status) {
        return ((root, _, criteriaBuilder) ->
                status == null ? null : criteriaBuilder.equal(root.get("status"), status));
    }
}
