package com.github.invader.agent;

import com.github.invader.agent.config.AgentConfiguration;
import com.github.invader.agent.config.AgentConfigurationLoader;
import com.github.invader.agent.interceptors.config.InterceptorConfigurationRefresher;
import com.github.invader.agent.interceptors.*;
import com.github.invader.agent.interceptors.binding.InterceptorBinder;
import com.github.invader.agent.interceptors.binding.InterceptorBinding;
import com.github.invader.agent.interceptors.binding.InterceptorBindingLoader;
import com.github.invader.agent.logging.LoggingConfigurator;
import com.github.invader.agent.logging.LoggingListener;
import lombok.extern.slf4j.Slf4j;

import java.lang.instrument.Instrumentation;
import java.text.MessageFormat;

@Slf4j
public class Agent {

    public static void premain(String args, Instrumentation instrumentation) {
        try {
            AgentConfiguration agentConfiguration = new AgentConfigurationLoader().load();

            new LoggingConfigurator(agentConfiguration).configure();

            Interceptor[] interceptors = new Interceptor[]{new DelayInterceptor(), new FailInterceptor()};

            InterceptorBinder binder = new InterceptorBinder(new LoggingListener(), interceptors, instrumentation);
            for (InterceptorBinding binding : new InterceptorBindingLoader(agentConfiguration).loadBindings()) {
                binder.bind(binding);
            }

            new InterceptorConfigurationRefresher(agentConfiguration, interceptors).start();
        } catch (Throwable t) {
            System.err.println(MessageFormat.format("Error starting the in-vader agent: {0}", t));
            t.printStackTrace();
        }
    }
}