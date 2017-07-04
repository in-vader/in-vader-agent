package com.github.invader.agent.interceptors.constraints;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by Jacek on 2017-07-02.
 */
@Target({ FIELD, METHOD, PARAMETER, TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = NonEmptyString.NonEmptyStringValidator.class)
public @interface NonEmptyString {

    String message() default "";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
    class NonEmptyStringValidator implements ConstraintValidator<NonEmptyString, String> {

        @Override
        public void initialize(NonEmptyString correctTimes) {}

        @Override
        public boolean isValid(String string, ConstraintValidatorContext constraintValidatorContext) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            boolean correct = string != null && !string.isEmpty();
            if (!correct) {
                constraintValidatorContext.buildConstraintViolationWithTemplate(
                        "String cannot be null or empty"
                ).addConstraintViolation();
            }
            return correct;
        }
    }
}
