package com.ficsolution.roster.util;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;

import java.util.UUID;

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
    public static final SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor authority = jwt().authorities(new SimpleGrantedAuthority("ROLE_MANAGER"));
    public static final SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor supervisorAuthority =
            jwt().jwt(b -> b.subject(employeeId.toString())).authorities(new SimpleGrantedAuthority("ROLE_SUPERVISOR"));
    public static final SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor staffAuthority =
            jwt().jwt(b -> b.subject(employeeId.toString())).authorities(new SimpleGrantedAuthority("ROLE_STAFF"));
    public static final SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor staffOtherAuthority =
            jwt().jwt(b -> b.subject(employee1Id.toString())).authorities(new SimpleGrantedAuthority("ROLE_STAFF"));
    public static final SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor staffThirdAuthority =
            jwt().jwt(b -> b.subject(employee2Id.toString())).authorities(new SimpleGrantedAuthority("ROLE_STAFF"));
}
