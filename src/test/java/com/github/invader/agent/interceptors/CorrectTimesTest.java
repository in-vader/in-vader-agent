package com.github.invader.agent.interceptors;

import com.github.invader.agent.interceptors.validation.JavaxValidatorFactory;
import org.junit.Test;

import javax.validation.Validator;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Jacek on 2017-07-02.
 */
public class CorrectTimesTest {

    Validator validator = JavaxValidatorFactory.createValidator();

    @Test
    public void shouldFailWhenStartEndTimeOutOfOrder() throws ClassNotFoundException {

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
