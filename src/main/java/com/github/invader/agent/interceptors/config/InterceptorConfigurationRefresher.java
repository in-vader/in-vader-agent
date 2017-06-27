package com.github.invader.agent.interceptors.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.invader.agent.config.AgentConfiguration;
import com.github.invader.agent.interceptors.Interceptor;
import com.github.invader.agent.rest.ConfigurationClient;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class InterceptorConfigurationRefresher {

    private final AgentConfiguration agentConfiguration;
    private final Interceptor[] interceptors;
    private ScheduledExecutorService executor;

    public InterceptorConfigurationRefresher(AgentConfiguration agentConfiguration, Interceptor[] interceptors) {
        this.agentConfiguration = agentConfiguration;
        this.interceptors = interceptors;
    }

    public void start() {
        executor = Executors.newScheduledThreadPool(1);
        executor.scheduleWithFixedDelay(new RefresherRunnable(agentConfiguration, interceptors, FileConfigurationClient.connect("demo-config.json")),
                0, 10, TimeUnit.SECONDS);
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
                config = configurationClient.getConfiguration(agentConfiguration.getGroup(), agentConfiguration.getName());
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

    // TODO move me and refactor me
    public static class FileConfigurationClient implements ConfigurationClient {
        private final Map<String, Map> fileConfigMap;

        private FileConfigurationClient(Map<String, Map> fileConfigMap) {
            this.fileConfigMap = fileConfigMap;
        }

        @Override
        public Map<String, Map> getConfiguration(String group, String app) {
            return fileConfigMap;
        }

        static ConfigurationClient connect(String configFile) {
            final File config = new File(configFile);

            final Map<String, Map> agentConfig;
            try {
                agentConfig = new ObjectMapper().readValue(config, new TypeReference<Map<String,Object>>(){});
            } catch (IOException e) {
                throw new RuntimeException(String.format("Can't read file %s.", config.getAbsoluteFile()));
            }

            return new FileConfigurationClient(agentConfig);
        }
    }
}