package com.ficsolution.roster.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import static com.ficsolution.roster.security.Permissions.*;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthenticationConverter converter) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/login", "/v3/api-docs/**", "/swagger-ui/**").permitAll()

                        // Roles and permissions management
                        .requestMatchers("/roles/**", "/permissions/**").hasAuthority(ROLE_MANAGE)

                        // Stores
                        .requestMatchers(HttpMethod.GET, "/stores", "/stores/*").hasAuthority(STORE_READ)
                        .requestMatchers(HttpMethod.POST, "/stores").hasAuthority(STORE_WRITE)
                        .requestMatchers(HttpMethod.PUT, "/stores/*").hasAuthority(STORE_WRITE)
                        .requestMatchers(HttpMethod.DELETE, "/stores/*").hasAuthority(STORE_WRITE)

                        // Employees: "_SELF" gates the URL, ownership vs "_ANY" is checked in the controller
                        .requestMatchers(HttpMethod.POST, "/employees").hasAuthority(EMPLOYEE_WRITE)
                        .requestMatchers(HttpMethod.PUT, "/employees/*/change-status").hasAuthority(EMPLOYEE_WRITE)
                        .requestMatchers(HttpMethod.PUT, "/employees/*/change-password").hasAnyAuthority(EMPLOYEE_PASSWORD_SELF, EMPLOYEE_PASSWORD_ANY)
                        .requestMatchers(HttpMethod.PUT, "/employees/*").hasAuthority(EMPLOYEE_WRITE)
                        .requestMatchers(HttpMethod.GET, "/employees/*").hasAnyAuthority(EMPLOYEE_READ_SELF, EMPLOYEE_READ_ANY)
                        .requestMatchers(HttpMethod.GET, "/employees").hasAuthority(EMPLOYEE_READ_ANY)

                        // Shifts
                        .requestMatchers(HttpMethod.POST, "/shifts").hasAuthority(SHIFT_WRITE)
                        .requestMatchers(HttpMethod.PUT, "/shifts/*").hasAuthority(SHIFT_WRITE)
                        .requestMatchers(HttpMethod.GET, "/shifts", "/shifts/*").hasAuthority(SHIFT_READ)

                        // Shift swap requests
                        .requestMatchers(HttpMethod.POST, "/shifts/*/swap-requests").hasAnyAuthority(SWAP_REQUEST_SELF, SWAP_REQUEST_ANY)
                        .requestMatchers(HttpMethod.PUT, "/swap-requests/*").hasAnyAuthority(SWAP_REQUEST_SELF, SWAP_REQUEST_ANY)
                        .requestMatchers(HttpMethod.GET, "/swap-requests/*").hasAnyAuthority(SWAP_REQUEST_SELF, SWAP_REQUEST_ANY)
                        .requestMatchers(HttpMethod.DELETE, "/swap-requests/*").hasAnyAuthority(SWAP_REQUEST_SELF, SWAP_REQUEST_ANY)

                        // Time off requests
                        .requestMatchers(HttpMethod.POST, "/time-off-requests").hasAnyAuthority(TIME_OFF_SELF, TIME_OFF_ANY)
                        .requestMatchers(HttpMethod.GET, "/time-off-requests").hasAnyAuthority(TIME_OFF_SELF, TIME_OFF_ANY)
                        .requestMatchers(HttpMethod.GET, "/time-off-requests/*").hasAnyAuthority(TIME_OFF_SELF, TIME_OFF_ANY)
                        .requestMatchers(HttpMethod.PUT, "/time-off-requests/*").hasAuthority(TIME_OFF_REVIEW)
                        .requestMatchers(HttpMethod.DELETE, "/time-off-requests/*").hasAnyAuthority(TIME_OFF_SELF, TIME_OFF_ANY)

                        // Availabilities
                        .requestMatchers(HttpMethod.POST, "/availabilities").hasAnyAuthority(AVAILABILITY_SELF, AVAILABILITY_ANY)
                        .requestMatchers(HttpMethod.GET, "/availabilities").hasAnyAuthority(AVAILABILITY_SELF, AVAILABILITY_ANY)
                        .requestMatchers(HttpMethod.GET, "/availabilities/*").hasAnyAuthority(AVAILABILITY_SELF, AVAILABILITY_ANY)
                        .requestMatchers(HttpMethod.PUT, "/availabilities/*").hasAnyAuthority(AVAILABILITY_SELF, AVAILABILITY_ANY)
                        .requestMatchers(HttpMethod.DELETE, "/availabilities/*").hasAnyAuthority(AVAILABILITY_SELF, AVAILABILITY_ANY)

                        .anyRequest().hasAuthority(ROLE_MANAGE))
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(converter)));

        return http.build();
    }
}
