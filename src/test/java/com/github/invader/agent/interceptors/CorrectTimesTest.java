package com.github.invader.agent.interceptors;

import com.github.invader.agent.interceptors.validation.JavaxValidatorFactory;
import org.junit.Test;

import javax.validation.Validator;
import java.time.LocalTime;
import java.time.OffsetTime;
import java.time.ZoneOffset;
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
                .startTime(OffsetTime.of(12,0,0,0, ZoneOffset.UTC))
                .endTime(OffsetTime.of(11,0,0,0, ZoneOffset.UTC))
                .delayMidpoints(Arrays.asList(100))
                .build();

        assertThat(validator.validate(profile))
                .extracting("message")
                .containsExactly("Start time (12:00Z) must be before end time (11:00Z)");
    }

    @Test
    public void shouldFailWhenStartEndTimeOutOfOrderBecauseOfTimezones() throws ClassNotFoundException {

        PeakProfile profile = PeakProfile
                .builder()
                .startTime(OffsetTime.of(12,0,0,0, ZoneOffset.ofHours(-3)))
                .endTime(OffsetTime.of(13,0,0,0, ZoneOffset.ofHours(2)))
                .delayMidpoints(Arrays.asList(100))
                .build();

        assertThat(validator.validate(profile))
                .extracting("message")
                .containsExactly("Start time (12:00-03:00) must be before end time (13:00+02:00)");
        //Noon in Brasil (-03:00) is effectively *later* than 13:00 in Romania (+02:00)
    }

    @Test
    public void shouldNotFailWhenStartEndTimeInOrder() {

        PeakProfile profile = PeakProfile
                .builder()
                .startTime(OffsetTime.of(12,0,0,0, ZoneOffset.UTC))
                .endTime(OffsetTime.of(14,0,0,0, ZoneOffset.UTC))
                .delayMidpoints(Arrays.asList(100))
                .build();

        assertThat(validator.validate(profile)).isEmpty();
    }

    @Test
    public void shouldFailWhenNoMidpoints() {

        PeakProfile profile = PeakProfile
                .builder()
                .startTime(OffsetTime.of(12,0,0,0, ZoneOffset.UTC))
                .endTime(OffsetTime.of(14,0,0,0, ZoneOffset.UTC))
                .delayMidpoints(new ArrayList<>())
                .build();

        assertThat(validator.validate(profile))
                .extracting("message")
                .containsOnly("Please provide at least 1 midpoint");
    }

}
