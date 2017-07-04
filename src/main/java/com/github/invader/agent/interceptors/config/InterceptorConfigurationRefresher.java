package com.github.invader.agent.interceptors.config;

import com.github.invader.agent.config.AgentConfiguration;
import com.github.invader.agent.config.client.ConfigurationClientFactory;
import com.github.invader.agent.interceptors.Interceptor;
import com.github.invader.agent.config.client.ConfigurationClient;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class InterceptorConfigurationRefresher {

    public static final int DELAY_SECONDS = 10;
    private final AgentConfiguration agentConfiguration;
    private final Interceptor[] interceptors;
    private ScheduledExecutorService executor;

    public InterceptorConfigurationRefresher(AgentConfiguration agentConfiguration, Interceptor[] interceptors) {
        this.agentConfiguration = agentConfiguration;
        this.interceptors = interceptors;
    }

    public void start() {
        executor = Executors.newScheduledThreadPool(1);
        executor.scheduleWithFixedDelay(
                new RefresherRunnable(agentConfiguration, interceptors,
                        ConfigurationClientFactory.createClient(
                                agentConfiguration.getConfig().getSource(),
                                agentConfiguration.getGroup(),
                                agentConfiguration.getName())),
                0, DELAY_SECONDS, TimeUnit.SECONDS);
    }

    public void stop() {
        executor.shutdown();
    }

    @Slf4j
    public static class RefresherRunnable implements Runnable {
        private final AgentConfiguration agentConfiguration;
        private final Interceptor[] interceptors;
        private final ConfigurationClient configurationClient;

        public RefresherRunnable(AgentConfiguration agentConfiguration, Interceptor[] interceptors, ConfigurationClient configurationClient) {
            this.agentConfiguration = agentConfiguration;
            this.interceptors = interceptors;
            this.configurationClient = configurationClient;
        }

        @Override
        public void run() {
            Map<String, Map> config = Collections.emptyMap();
            try {
                log.debug("Fetching configuration for agent.");
                config = configurationClient.getConfiguration();
                log.debug("Retrieved configuration for agent: {}", config);
            } catch (Exception e) {
                log.error("Error while fetching configuration for agent.", e);
            }

            applyConfig(config);
        }

        private void applyConfig(Map<String, Map> config) {
            Arrays.stream(interceptors)
                    .forEach(interceptor -> applyInterceptorConfig(config, interceptor));

            log.info("Refreshed configs");
        }

        private void applyInterceptorConfig(Map<String, Map> config, Interceptor interceptor) {
            try {
                interceptor.setConfig(config.get(interceptor.getName()));
            } catch (Exception e) {
                log.error("Failed to apply configuration to interceptor {}", interceptor.getClass().getName(), e);
            }
        }
    }
}
