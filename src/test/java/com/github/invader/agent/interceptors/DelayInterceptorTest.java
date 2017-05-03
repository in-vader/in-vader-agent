package com.github.invader.agent.interceptors;

import com.google.common.collect.ImmutableMap;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DelayInterceptorTest {

    @Spy
    @InjectMocks
    private DelayInterceptor delayInterceptor = new DelayInterceptor() {
        @Override
        public ElementMatcher<? super TypeDescription> getTypeMatcher() {
            return null;
        }

        @Override
        public ElementMatcher<? super MethodDescription> getMethodMatcher() {
            return null;
        }

        @Override
        public Object intercept(Object[] allArguments, Method method, Callable<?> callable) throws Exception {
            return null;
        }
    };

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
        delayInterceptor.doIntercept(callable);
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