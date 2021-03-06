package com.github.invader.agent.interceptors;

import org.junit.Test;

import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class PeakCalculatorTest {

    PeakProfile profile = PeakProfile.builder()
            .startTime(OffsetTime.of(17, 0, 0, 0, ZoneOffset.ofHours(0)))
            .endTime(OffsetTime.of(19, 20, 0, 0, ZoneOffset.ofHours(0)))
            .delayMidpoints(Arrays.asList(100, 400, 400, 200, 50)).build();

    @Test
    public void shouldBeBeforeFirstEndpoint() {
        int lag = new PeakCalculator().calculateDelay(profile, OffsetTime.of(16,59,0,0, ZoneOffset.ofHours(0)));
        assertThat(lag).isEqualTo(0);
    }

    @Test
    public void shouldBeInFirstMidpoint() {
        int lag = new PeakCalculator().calculateDelay(profile, OffsetTime.of(17,10,0,0, ZoneOffset.ofHours(0)));
        assertThat(lag).isEqualTo(100);
    }

    @Test
    public void shouldBeInLastMidpoint() {
        int lag = new PeakCalculator().calculateDelay(profile, OffsetTime.of(19,19,0,0, ZoneOffset.ofHours(0)));
        assertThat(lag).isEqualTo(50);
    }

    @Test
    public void shouldBeAfterLastMidpoint() {
        int lag = new PeakCalculator().calculateDelay(profile, OffsetTime.of(19,21,0,0,ZoneOffset.ofHours(0)));
        assertThat(lag).isEqualTo(0);
    }

    @Test
    public void shouldBeAtTheOnlyMidpoint() {

        PeakProfile singleMidpointProfile = PeakProfile.builder()
                .startTime(OffsetTime.of(17, 0, 0, 0, ZoneOffset.ofHours(0)))
                .endTime(OffsetTime.of(19, 20, 0, 0, ZoneOffset.ofHours(0)))
                .delayMidpoints(Arrays.asList(10)).build();

        int lag = new PeakCalculator().calculateDelay(singleMidpointProfile, OffsetTime.of(19,18,0,0,ZoneOffset.ofHours(0)));
        assertThat(lag).isEqualTo(10);
    }


}