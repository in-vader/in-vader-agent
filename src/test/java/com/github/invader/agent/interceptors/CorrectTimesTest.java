package com.github.invader.agent.interceptors;

import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Jacek on 2017-07-02.
 */
public class CorrectTimesTest {

    @Test
    public void shouldFailWhenStartEndTimeOutOfOrder() {

        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

        PeakProfile profile = PeakProfile
                .builder()
                .startTime(LocalTime.NOON)
                .endTime(LocalTime.MIDNIGHT)
                .delayMidpoints(Arrays.asList(100))
                .build();

        assertThat(validator.validate(profile))
                .extracting("message")
                .containsExactly("Start time (12:00) must be before end time (00:00)");
    }

    @Test
    public void shouldNotFailWhenStartEndTimeInOrder() {

        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

        PeakProfile profile = PeakProfile
                .builder()
                .startTime(LocalTime.MIDNIGHT)
                .endTime(LocalTime.NOON)
                .delayMidpoints(Arrays.asList(100))
                .build();

        assertThat(validator.validate(profile)).isEmpty();
    }

    @Test
    public void shouldFailWhenNoMidpoints() {

        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

        PeakProfile profile = PeakProfile
                .builder()
                .startTime(LocalTime.MIDNIGHT)
                .endTime(LocalTime.NOON)
                .delayMidpoints(new ArrayList<>())
                .build();

        assertThat(validator.validate(profile))
                .extracting("message")
                .containsOnly("Please provide at least 1 midpoint");
    }

}
