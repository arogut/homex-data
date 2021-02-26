package com.arogut.homex.data.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PastValidator implements ConstraintValidator<Past, Long> {

    @Override
    public boolean isValid(Long value, ConstraintValidatorContext context) {
        return value != null && value < System.currentTimeMillis();
    }
}
