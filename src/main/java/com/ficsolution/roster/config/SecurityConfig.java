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

                        // Roles: only MANAGER
                        .requestMatchers("/roles/**").hasRole("MANAGER")

                        // Stores: read for everyone, write only MANAGER
                        .requestMatchers(HttpMethod.GET, "/stores", "/stores/*").hasAnyRole("MANAGER", "SUPERVISOR", "STAFF")
                        .requestMatchers(HttpMethod.POST, "/stores").hasRole("MANAGER")
                        .requestMatchers(HttpMethod.PUT, "/stores/*").hasRole("MANAGER")
                        .requestMatchers(HttpMethod.DELETE, "/stores/*").hasRole("MANAGER")

                        // Employees
                        .requestMatchers(HttpMethod.POST, "/employees").hasRole("MANAGER")
                        .requestMatchers(HttpMethod.PUT, "/employees/*/change-status").hasRole("MANAGER")
                        .requestMatchers(HttpMethod.PUT, "/employees/*/change-password").hasAnyRole("MANAGER", "SUPERVISOR", "STAFF")
                        .requestMatchers(HttpMethod.PUT, "/employees/*").hasRole("MANAGER")
                        .requestMatchers(HttpMethod.GET, "/employees/*").hasAnyRole("MANAGER", "SUPERVISOR", "STAFF")
                        .requestMatchers(HttpMethod.GET, "/employees").hasAnyRole("MANAGER", "SUPERVISOR")

                        // Shifts: write MANAGER/SUPERVISOR, read shared by everyone
                        .requestMatchers(HttpMethod.POST, "/shifts").hasAnyRole("MANAGER", "SUPERVISOR")
                        .requestMatchers(HttpMethod.PUT, "/shifts/*").hasAnyRole("MANAGER", "SUPERVISOR")
                        .requestMatchers(HttpMethod.GET, "/shifts", "/shifts/*").hasAnyRole("MANAGER", "SUPERVISOR", "STAFF")

                        // Shift swap requests: gate allows everyone, ownership is checked in the controller
                        .requestMatchers(HttpMethod.POST, "/shifts/*/swap-requests").hasAnyRole("MANAGER", "SUPERVISOR", "STAFF")
                        .requestMatchers(HttpMethod.PUT, "/swap-requests/*").hasAnyRole("MANAGER", "SUPERVISOR", "STAFF")
                        .requestMatchers(HttpMethod.GET, "/swap-requests/*").hasAnyRole("MANAGER", "SUPERVISOR", "STAFF")
                        .requestMatchers(HttpMethod.DELETE, "/swap-requests/*").hasAnyRole("MANAGER", "SUPERVISOR", "STAFF")

                        // Time off requests
                        .requestMatchers(HttpMethod.POST, "/time-off-requests").hasAnyRole("MANAGER", "SUPERVISOR", "STAFF")
                        .requestMatchers(HttpMethod.GET, "/time-off-requests").hasAnyRole("MANAGER", "SUPERVISOR", "STAFF")
                        .requestMatchers(HttpMethod.GET, "/time-off-requests/*").hasAnyRole("MANAGER", "SUPERVISOR", "STAFF")
                        .requestMatchers(HttpMethod.PUT, "/time-off-requests/*").hasAnyRole("MANAGER", "SUPERVISOR")
                        .requestMatchers(HttpMethod.DELETE, "/time-off-requests/*").hasAnyRole("MANAGER", "SUPERVISOR", "STAFF")

                        // Availabilities
                        .requestMatchers(HttpMethod.POST, "/availabilities").hasAnyRole("MANAGER", "SUPERVISOR", "STAFF")
                        .requestMatchers(HttpMethod.GET, "/availabilities").hasAnyRole("MANAGER", "SUPERVISOR", "STAFF")
                        .requestMatchers(HttpMethod.GET, "/availabilities/*").hasAnyRole("MANAGER", "SUPERVISOR", "STAFF")
                        .requestMatchers(HttpMethod.PUT, "/availabilities/*").hasAnyRole("MANAGER", "SUPERVISOR", "STAFF")
                        .requestMatchers(HttpMethod.DELETE, "/availabilities/*").hasAnyRole("MANAGER", "SUPERVISOR", "STAFF")

                        .anyRequest().hasRole("MANAGER"))
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(converter)));

        return http.build();
    }
}
