package com.ficsolution.roster.validator;

import com.ficsolution.roster.annotation.ValidPassword;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {
    private static final int MIN_LENGTH = 12;

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null) {
            return true;
        }

        return password.length() >= MIN_LENGTH;
    }
}
