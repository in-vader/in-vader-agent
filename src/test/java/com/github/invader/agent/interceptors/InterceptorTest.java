package com.github.invader.agent.interceptors;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class InterceptorTest {

    @Mock(answer = Answers.CALLS_REAL_METHODS)
    private Interceptor interceptor;

    private Map<String, Object> configuration;

    @Test
    public void shouldDisableWhenEmptyConfig() {
        // Given
        givenEmptyConfiguration();

        // When
        whenSettingConfig();

        // Then
        then()
                .isDisabled();
    }

    @Test
    public void shouldEnableWhenNotEmptyConfig() {
        // Given
        givenConfiguration();

        // When
        whenSettingConfig();

        // Then
        then()
                .isEnabled()
                .hasAppliedConfig();
    }

    private void givenConfiguration() {
        configuration = Collections.emptyMap();
    }

    private void givenEmptyConfiguration() {
        configuration = null;
    }

    private void whenSettingConfig() {
        interceptor.setConfig(configuration);
    }

    private InterceptorResultAssert then() {
        return new InterceptorResultAssert();
    }

    private class InterceptorResultAssert {
        public InterceptorResultAssert isDisabled() {
            assertThat(interceptor.isEnabled()).isFalse();
            return this;
        }

        public InterceptorResultAssert isEnabled() {
            assertThat(interceptor.isEnabled()).isTrue();
            return this;
        }

        public InterceptorResultAssert hasAppliedConfig() {
            verify(interceptor).applyConfig(configuration);
            return this;
        }
    }
}