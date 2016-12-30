package com.github.invader.agent.config;

import com.github.invader.agent.HttpServletDelayInterceptor;
import com.github.invader.agent.HttpServletFailInterceptor;
import com.github.invader.agent.Interceptor;
import com.github.invader.agent.rest.ConfigurationClient;
import com.github.invader.controller.transport.Config;

import java.util.Optional;

public class InterceptorConfigTask implements Runnable {
    private final AgentConfiguration agentConfiguration;
    private final Interceptor interceptor;
    private final ConfigurationClient configurationClient;

    public InterceptorConfigTask(AgentConfiguration agentConfiguration, Interceptor interceptor) {
        this.agentConfiguration = agentConfiguration;
        this.interceptor = interceptor;
        this.configurationClient = new ConfigurationClient(agentConfiguration.getServer());
    }

    @Override
    public void run() {
        Optional<Config> result = configurationClient.getConfiguration(agentConfiguration.getGroup(),agentConfiguration.getName());

        if(result.isPresent()) {
            final Config config = result.get();

            System.out.println(String.format("Getting configuration for %s", interceptor.getClass().getSimpleName()));

            if(interceptor instanceof HttpServletDelayInterceptor) {
                HttpServletDelayInterceptor httpServletDelayInterceptor = (HttpServletDelayInterceptor) interceptor;

                httpServletDelayInterceptor.setMinDelay(config.getDelay().getMin());
                httpServletDelayInterceptor.setMaxDelay(config.getDelay().getMax());
            } else if(interceptor instanceof HttpServletFailInterceptor) {
                HttpServletFailInterceptor httpServletFailInterceptor = (HttpServletFailInterceptor) interceptor;

                httpServletFailInterceptor.setProbability(config.getFailure().getProbability());
            }
        }
    }
}