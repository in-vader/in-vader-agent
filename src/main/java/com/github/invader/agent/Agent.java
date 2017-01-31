package com.github.invader.agent;

import com.github.invader.agent.config.AgentConfiguration;
import com.github.invader.agent.config.AgentConfigurationParser;
import com.github.invader.agent.config.InterceptorConfigTask;
import com.github.invader.agent.rest.ConfigurationClient;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.utility.JavaModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.instrument.Instrumentation;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Agent {

    private static final Logger LOG = LoggerFactory.getLogger(Agent.class);

    public static void premain(String args, Instrumentation instrumentation) {
        AgentConfiguration agentConfiguration = new AgentConfigurationParser().parse();

        Interceptor[] interceptors = new Interceptor[] { new HttpServletFailInterceptor(), new HttpServletDelayInterceptor() };

        Arrays.stream(interceptors)
                .forEach(interceptor -> new AgentBuilder.Default()
                        .with(new LoggingListener())
                        .type(interceptor.getTypeMatcher())
                        .transform((builder, type, classLoader) -> builder.method(interceptor.getMethodMatcher())
                                .intercept(MethodDelegation.to(interceptor))
                        ).installOn(instrumentation));


        Executors.newScheduledThreadPool(1)
                .scheduleWithFixedDelay(new InterceptorConfigTask(agentConfiguration, interceptors, ConfigurationClient.connect(agentConfiguration.getServer())),
                        0, 10, TimeUnit.SECONDS);
    }

    public static class LoggingListener implements AgentBuilder.Listener {

        @Override
        public void onTransformation(TypeDescription typeDescription, ClassLoader classLoader, JavaModule module, DynamicType dynamicType) {
            LOG.info("Transformed - {}, type = {}", typeDescription, dynamicType);
        }

        @Override
        public void onIgnored(TypeDescription typeDescription, ClassLoader classLoader, JavaModule module) {
        }

        @Override
        public void onError(String typeName, ClassLoader classLoader, JavaModule module, Throwable throwable) {
            LOG.error("Error - {}", typeName, throwable);
        }

        @Override
        public void onComplete(String typeName, ClassLoader classLoader, JavaModule module) {
        }
    }
}