package com.github.invader.agent.interceptors;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.concurrent.Callable;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DelayInterceptorTest {

    @Spy
    @InjectMocks
    private DelayInterceptor delayInterceptor;

    @Mock
    private Consumer<Long> sleeper;

    @Mock
    private BiFunction<Integer, Integer, Long> delayGenerator;

    @Mock
    private Callable callable;

    @Test
    public void shouldSleepWhenEnabled() throws Exception {
        // Given
        givenEnabled(true);
        givenConfiguration();
        givenGeneratedDelay();

        // When
        whenIntercepting();

        // Then
        then()
                .hasSlept()
                .hasCalledInterceptedMethod();
    }

    @Test
    public void shouldNotSleepWhenDisabled() throws Exception {
        // Given
        givenEnabled(false);

        // When
        whenIntercepting();

        // Then
        then()
                .hasNotSlept()
                .hasCalledInterceptedMethod();
    }

    private void givenConfiguration() {
        delayInterceptor.applyConfig(ImmutableMap.of("min", 100, "max", 200));
    }

    private void givenEnabled(boolean enabled) {
        when(delayInterceptor.isEnabled()).thenReturn(enabled);
    }

    private void givenGeneratedDelay() {
        when(delayGenerator.apply(anyInt(), anyInt())).thenReturn(100L);
    }

    private void whenIntercepting() throws Exception {
        delayInterceptor.intercept(callable);
    }

    private DelayInterceptorResultAssert then() {
        return new DelayInterceptorResultAssert();
    }

    private class DelayInterceptorResultAssert {
        public DelayInterceptorResultAssert hasSlept() {
            verify(sleeper).accept(anyLong());
            return this;
        }

        public DelayInterceptorResultAssert hasNotSlept() {
            verify(sleeper, never()).accept(anyLong());
            return this;
        }

        public DelayInterceptorResultAssert hasCalledInterceptedMethod() throws Exception {
            verify(callable).call();
            return this;
        }
    }
}