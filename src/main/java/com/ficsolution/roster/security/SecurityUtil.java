package com.ficsolution.roster.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public final class SecurityUtil {

    private SecurityUtil() {
    }

    public static UUID currentEmployeeId() {
        return UUID.fromString(currentAuthentication().getName());
    }

    public static boolean hasAnyRole(String... roles) {
        Set<String> required = Arrays.stream(roles).map(role -> "ROLE_" + role).collect(Collectors.toSet());
        return currentAuthentication().getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(required::contains);
    }

    public static void requireRole(String... roles) {
        if (!hasAnyRole(roles)) {
            throw new AccessDeniedException("Requires one of roles: " + Arrays.toString(roles));
        }
    }

    public static void requireOwnershipOrRole(UUID ownerId, String... bypassRoles) {
        if (hasAnyRole(bypassRoles)) {
            return;
        }
        if (!currentEmployeeId().equals(ownerId)) {
            throw new AccessDeniedException("Not permitted to access this resource.");
        }
    }

    public static void requireOwnershipOrRole(Collection<UUID> acceptableOwnerIds, String... bypassRoles) {
        if (hasAnyRole(bypassRoles)) {
            return;
        }
        if (!acceptableOwnerIds.contains(currentEmployeeId())) {
            throw new AccessDeniedException("Not permitted to access this resource.");
        }
    }

    private static Authentication currentAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
}
