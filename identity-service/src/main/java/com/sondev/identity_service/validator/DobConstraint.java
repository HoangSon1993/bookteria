package com.sondev.identity_service.validator;

import static java.lang.annotation.ElementType.FIELD;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({FIELD})
@Retention(RetentionPolicy.RUNTIME) // annotation được xử lý lúc nào.
// @Repeatable(List.class)
// @Documented
@Constraint(validatedBy = {DobValidator.class})
public @interface DobConstraint {
    String message() default "Invaid date of birth";

    int min();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
