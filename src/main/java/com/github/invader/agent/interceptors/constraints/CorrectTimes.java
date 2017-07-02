package com.github.invader.agent.interceptors.constraints;

import com.github.invader.agent.interceptors.PeakProfile;

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
@Constraint(validatedBy = CorrectTimes.CorrectTimesValidator.class)
public @interface CorrectTimes {

    String message() default "";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
    class CorrectTimesValidator implements ConstraintValidator<CorrectTimes, PeakProfile> {

        @Override
        public void initialize(CorrectTimes correctTimes) {}

        @Override
        public boolean isValid(PeakProfile peakProfile, ConstraintValidatorContext constraintValidatorContext) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            boolean areTimesCorrect = peakProfile.getStartTime().isBefore(peakProfile.getEndTime());
            if (!areTimesCorrect) {
                constraintValidatorContext.buildConstraintViolationWithTemplate(
                        "Start time ("
                                +peakProfile.getStartTime()
                                +") must be before end time ("
                                +peakProfile.getEndTime()
                                +")").addConstraintViolation();
            }
            return areTimesCorrect;
        }
    }
}
