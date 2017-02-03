package com.github.invader.agent.config;

import com.github.invader.agent.Interceptor;
import com.github.invader.agent.rest.ConfigurationClient;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

@Slf4j
public class InterceptorConfigTask implements Runnable {
    private final AgentConfiguration agentConfiguration;
    private final Interceptor[] interceptors;
    private final ConfigurationClient configurationClient;

    public InterceptorConfigTask(AgentConfiguration agentConfiguration, Interceptor[] interceptors, ConfigurationClient configurationClient) {
        this.agentConfiguration = agentConfiguration;
        this.interceptors = interceptors;
        this.configurationClient = configurationClient;
    }

    @Override
    public void run() {
        Map<String, Map> config = Collections.emptyMap();
        try {
            log.debug("Fetching configuration for agent.");
            config = configurationClient.getConfiguration(agentConfiguration.getGroup(), agentConfiguration.getName());
            log.debug("Retrieved configuration for agent: {}", config);
        } catch (Exception e) {
            log.error("Error while fetching configuration for agent.", e);
        }

        applyConfig(config);
    }

    private void applyConfig(Map<String, Map> config) {
        Arrays.stream(interceptors)
                .forEach(interceptor -> interceptor.setConfig(config.get(interceptor.getName())));
    }
}