package com.github.invader.agent.interceptors;

import com.github.invader.agent.interceptors.constraints.UnparseableValueException;
import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.*;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Jacek on 2017-07-01.
 */
@RunWith(MockitoJUnitRunner.class)
public class PeakInterceptorTest {

    @InjectMocks
    PeakInterceptor peakInterceptor = new PeakInterceptor();

    @Mock
    PeakCalculator peakCalculator;

    @Mock
    Consumer<Long> sleeper;

    @Mock
    Callable<?> callable;

    @Test
    public void shouldDelayIfCalculatorReturnsPositiveValue() throws Exception {

        givenConfigurationWithStartEndTimeAndMidpoints("15:00:00-05:00", "15:15:00-05:00", 10, 20, 30, 20, 10);
        givenPeakCalculatorReturnedDelayOf(10);

        //when
        peakInterceptor.intercept(callable);

        //then
        assertTrue(peakInterceptor.isEnabled());
        verify(sleeper).accept(10L);
        verify(callable).call();
    }

    @Test
    public void shouldNotDelayIfCalculatorReturnsZero() throws Exception {

        givenConfigurationWithStartEndTimeAndMidpoints("15:00:00+01:00", "15:15:00+01:00", 10, 20, 50, 20, 10);
        givenPeakCalculatorReturnedDelayOf(0);

        //when
        peakInterceptor.intercept(callable);

        //then
        assertTrue(peakInterceptor.isEnabled());
        verify(sleeper, never()).accept(anyLong());
        verify(callable).call();
    }

    @Test
    public void shouldBeDisabledIfNoConfiguration() throws Exception {

        givenNoConfiguration();

        //when
        peakInterceptor.intercept(callable);

        //then
        assertFalse(peakInterceptor.isEnabled());
        verify(sleeper, never()).accept(anyLong());
        verify(callable).call();
    }

    @Test
    public void shouldFailIfCorruptEndTimeFormat() throws Exception {

        givenConfigurationWithStartEndTimeAndMidpoints("15:00:00", "00:00", 100, 200, 500, 200, 100);

        //when
        peakInterceptor.intercept(callable);

        //then
        assertFalse(peakInterceptor.isEnabled());
        verify(sleeper, never()).accept(anyLong());
        verify(callable).call();
    }

    @Test
    public void shouldFailIfCorruptStartTimeFormat() throws Exception {

        givenConfigurationWithStartEndTimeAndMidpoints("0x", "15:00:00", 100, 200, 500, 200, 100);

        //when
        peakInterceptor.intercept(callable);

        //then
        assertFalse(peakInterceptor.isEnabled());
        verify(sleeper, never()).accept(anyLong());
        verify(callable).call();
    }

    @Test
    public void shouldFailIfStartTimeAfterEnd() throws Exception {

        givenConfigurationWithStartEndTimeAndMidpoints("15:15:00", "15:00:00", 100, 200, 500, 200, 100);

        //when
        peakInterceptor.intercept(callable);

        //then
        assertFalse(peakInterceptor.isEnabled());
        verify(sleeper, never()).accept(anyLong());
        verify(callable).call();
    }

    @Test
    public void shouldFailIfNoMidpoints() throws Exception {

        givenConfigurationWithStartEndTimeAndMidpoints("15:00:00", "15:15:00");

        //when
        peakInterceptor.intercept(callable);

        //then
        assertFalse(peakInterceptor.isEnabled());
        verify(sleeper, never()).accept(anyLong());
        verify(callable).call();
    }

    private void givenPeakCalculatorReturnedDelayOf(Integer delay) {
        when(peakCalculator.calculateDelay(any(PeakProfile.class), any(OffsetTime.class))).thenReturn(delay);
    }

    private void givenConfigurationWithStartEndTimeAndMidpoints(String start, String end, Integer ... midpoints) {
        peakInterceptor.setConfig(
                ImmutableMap.of(
                        "startTime", start,
                        "endTime", end,
                        "delayMidpoints", Arrays.asList(midpoints)));
    }

    private void givenNoConfiguration() {
        peakInterceptor.setConfig(null);
    }

}