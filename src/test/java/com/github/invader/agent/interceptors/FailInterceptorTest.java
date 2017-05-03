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
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class FailInterceptorTest {

    @Spy
    @InjectMocks
    private FailInterceptor failInterceptor = new FailInterceptor() {
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
    private Function<Double, Boolean> failureGenerator;

    @Mock
    private Callable callable;

    private Exception exception;

    @Test
    public void shouldFailWhenEnabled() throws Exception {
        // Given
        givenEnabled(true);
        givenConfiguration();
        givenShouldFail(true);

        // When
        whenIntercepting();

        // Then
        then()
                .hasThrownException()
                .hasNotCalledInterceptedMethod();
    }

    @Test
    public void shouldNotFailWhenEnabledAndProbabilityNotReached() throws Exception {
        // Given
        givenEnabled(true);
        givenConfiguration();
        givenShouldFail(false);

        // When
        whenIntercepting();

        // Then
        then()
                .hasNotThrownException()
                .hasCalledInterceptedMethod();
    }

    @Test
    public void shouldNotFailWhenDisabled() throws Exception {
        // Given
        givenEnabled(false);

        // When
        whenIntercepting();

        // Then
        then()
                .hasNotThrownException()
                .hasCalledInterceptedMethod();
    }

    private void givenConfiguration() {
        failInterceptor.applyConfig(ImmutableMap.of("probability", 0.5));
    }

    private void givenEnabled(boolean enabled) {
        when(failInterceptor.isEnabled()).thenReturn(enabled);
    }

    private void givenShouldFail(boolean shouldFail) {
        when(failureGenerator.apply(anyDouble())).thenReturn(shouldFail);
    }

    private void whenIntercepting() {
        try {
            failInterceptor.doIntercept(callable);
        } catch (Exception e) {
            exception = e;
        }
    }

    private FailInterceptorResultAssert then() {
        return new FailInterceptorResultAssert();
    }

    private class FailInterceptorResultAssert {
        public FailInterceptorResultAssert hasThrownException() {
            assertThat(exception).isNotNull();
            return this;
        }

        public FailInterceptorResultAssert hasNotThrownException() {
            assertThat(exception).isNull();
            return this;
        }

        public FailInterceptorResultAssert hasCalledInterceptedMethod() throws Exception {
            verify(callable).call();
            return this;
        }

        public FailInterceptorResultAssert hasNotCalledInterceptedMethod() throws Exception {
            verify(callable, never()).call();
            return this;
        }
    }
}