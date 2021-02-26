package com.arogut.homex.data.validation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.validation.ConstraintValidatorContext;

@ExtendWith(MockitoExtension.class)
class PastValidatorTest {

    @Mock
    private ConstraintValidatorContext context;

    @Test
    void shouldReturnFalseWhenFutureTimestamp() {
        PastValidator validator = new PastValidator();

        Assertions.assertFalse(validator.isValid(System.currentTimeMillis() + 200000, context));
        Assertions.assertFalse(validator.isValid(null, context));
    }
}
