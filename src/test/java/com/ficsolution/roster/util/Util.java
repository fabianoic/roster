package com.ficsolution.roster.util;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static com.ficsolution.roster.security.Permissions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

public class Util {

    public static final UUID storeId = UUID.fromString("8ba57eb8-9345-4068-bc65-d63d9575e4bd");
    public static final UUID roleId = UUID.fromString("9aa2510e-feda-4b1f-bffe-21cc9e180e9f");
    public static final UUID employeeId = UUID.fromString("ebb6ea55-b853-4504-a381-0f59ad2a1659");
    public static final UUID employee1Id = UUID.fromString("8d7ec490-9fdc-4a5f-b261-82e53bc74d7f");
    public static final UUID employee2Id = UUID.fromString("2c6a7c9a-df3e-4f0a-9d9a-2a4a2c2f8a11");
    public static final UUID shiftId = UUID.fromString("4355e858-65ba-4e06-b5fd-87c085a47aab");
    public static final UUID availabilityId = UUID.fromString("f38d39e2-df1c-4df7-86b6-5cb10571376a");
    public static final UUID timeOffRequestId = UUID.fromString("6df1b206-60aa-4183-8f63-3db9f5a04f77");
    public static final UUID shiftSwapRequestId = UUID.fromString("78c2dfad-487d-4405-920d-ef89eff01040");

    // Mirrors the role_permission seed in V4__permissions.sql
    public static final List<String> STAFF_PERMISSIONS = List.of(
            STORE_READ, EMPLOYEE_READ_SELF, EMPLOYEE_PASSWORD_SELF, SHIFT_READ,
            SWAP_REQUEST_SELF, TIME_OFF_SELF, AVAILABILITY_SELF);
    public static final List<String> SUPERVISOR_PERMISSIONS = List.of(
            STORE_READ, EMPLOYEE_READ_SELF, EMPLOYEE_READ_ANY, EMPLOYEE_PASSWORD_SELF,
            SHIFT_READ, SHIFT_WRITE, SWAP_REQUEST_SELF, SWAP_REQUEST_ANY,
            TIME_OFF_SELF, TIME_OFF_ANY, TIME_OFF_REVIEW, AVAILABILITY_SELF, AVAILABILITY_ANY);
    public static final List<String> MANAGER_PERMISSIONS = List.of(
            ROLE_MANAGE, STORE_READ, STORE_WRITE,
            EMPLOYEE_READ_SELF, EMPLOYEE_READ_ANY, EMPLOYEE_WRITE, EMPLOYEE_PASSWORD_SELF, EMPLOYEE_PASSWORD_ANY,
            SHIFT_READ, SHIFT_WRITE, SWAP_REQUEST_SELF, SWAP_REQUEST_ANY,
            TIME_OFF_SELF, TIME_OFF_ANY, TIME_OFF_REVIEW, AVAILABILITY_SELF, AVAILABILITY_ANY);

    public static GrantedAuthority[] authorities(List<String> permissions) {
        return permissions.stream().map(SimpleGrantedAuthority::new).toArray(GrantedAuthority[]::new);
    }

    public static GrantedAuthority[] authorities(String... permissions) {
        return authorities(Arrays.asList(permissions));
    }

    public static final SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor authority =
            jwt().authorities(authorities(MANAGER_PERMISSIONS));
    public static final SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor supervisorAuthority =
            jwt().jwt(b -> b.subject(employeeId.toString())).authorities(authorities(SUPERVISOR_PERMISSIONS));
    public static final SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor staffAuthority =
            jwt().jwt(b -> b.subject(employeeId.toString())).authorities(authorities(STAFF_PERMISSIONS));
    public static final SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor staffOtherAuthority =
            jwt().jwt(b -> b.subject(employee1Id.toString())).authorities(authorities(STAFF_PERMISSIONS));
    public static final SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor staffThirdAuthority =
            jwt().jwt(b -> b.subject(employee2Id.toString())).authorities(authorities(STAFF_PERMISSIONS));
}
