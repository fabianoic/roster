package com.ficsolution.roster.web.dto.employee;

import com.ficsolution.roster.annotation.ValidPassword;
import jakarta.validation.constraints.NotBlank;

public record ChangePasswordRequest(
        @NotBlank String oldPassword,
        @NotBlank @ValidPassword String newPassword
) {
}
