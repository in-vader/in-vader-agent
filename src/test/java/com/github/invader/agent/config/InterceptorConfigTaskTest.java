package com.github.invader.agent.config;

import com.github.invader.agent.Interceptor;
import com.github.invader.agent.rest.ConfigurationClient;
import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class InterceptorConfigTaskTest {

    private InterceptorConfigTask task;

    @Mock
    private AgentConfiguration agentConfiguration;

    @Mock
    private Interceptor interceptor;

    @Mock
    private ConfigurationClient configurationClient;

    @Before
    public void setUp() {
        task = new InterceptorConfigTask(agentConfiguration, new Interceptor[] { interceptor }, configurationClient);

        when(agentConfiguration.getGroup()).thenReturn("testGroup");
        when(agentConfiguration.getName()).thenReturn("testName");
        when(interceptor.getName()).thenReturn("failure");
    }

    @Test
    public void shouldUpdateInterceptorWithNewConfiguration() {
        // given
        Map<String, Object> failureConfig = Collections.emptyMap();
        when(configurationClient.getConfiguration(anyString(), anyString())).thenReturn(ImmutableMap.of("failure", failureConfig));

        // when
        task.run();

        // then
        verify(interceptor).setConfig(failureConfig);
    }

    @Test
    public void shouldUpdateInterceptorWithEmptyConfigurationWhenClientFails() {
        // given
        when(configurationClient.getConfiguration(anyString(), anyString())).thenThrow(new RuntimeException());

        // when
        task.run();

        // then
        verify(interceptor).setConfig(null);
    }
}