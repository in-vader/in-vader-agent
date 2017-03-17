package com.github.invader.agent;

import com.github.invader.agent.config.AgentConfiguration;
import com.github.invader.agent.config.AgentConfigurationParser;
import com.github.invader.agent.config.InterceptorConfigTask;
import com.github.invader.agent.interceptors.amqp.RabbitMqDelayInterceptor;
import com.github.invader.agent.interceptors.amqp.RabbitMqFailInterceptor;
import com.github.invader.agent.interceptors.jms.JmsDelayInterceptor;
import com.github.invader.agent.interceptors.jms.JmsFailInterceptor;
import com.github.invader.agent.interceptors.servlet.HttpServletDelayInterceptor;
import com.github.invader.agent.interceptors.servlet.HttpServletFailInterceptor;
import com.github.invader.agent.interceptors.Interceptor;
import com.github.invader.agent.logging.LoggingConfigurator;
import com.github.invader.agent.rest.ConfigurationClient;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.utility.JavaModule;

import java.lang.instrument.Instrumentation;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
public class Agent {

    public static void premain(String args, Instrumentation instrumentation) {
        try {
            AgentConfiguration agentConfiguration = new AgentConfigurationParser().parse();

            LoggingConfigurator loggingConfigurator = new LoggingConfigurator();
            loggingConfigurator.configure(agentConfiguration);

            Interceptor[] interceptors = new Interceptor[] { new HttpServletFailInterceptor(),
                    new HttpServletDelayInterceptor(),
                    new JmsFailInterceptor(),
                    new JmsDelayInterceptor(),
                    new RabbitMqFailInterceptor(),
                    new RabbitMqDelayInterceptor() };

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
        } catch (Throwable t) {
            System.err.println(MessageFormat.format("Error starting the in-vader agent: {0}", new Object[] { t }));
            t.printStackTrace();
        }
    }

    public static class LoggingListener implements AgentBuilder.Listener {

        @Override
        public void onTransformation(TypeDescription typeDescription, ClassLoader classLoader, JavaModule module, DynamicType dynamicType) {
            log.info("Transformed - {}, type = {}", typeDescription, dynamicType);
        }

        @Override
        public void onIgnored(TypeDescription typeDescription, ClassLoader classLoader, JavaModule module) {
        }

        @Override
        public void onError(String typeName, ClassLoader classLoader, JavaModule module, Throwable throwable) {
            log.error("Error - {}", typeName, throwable);
        }

        @Override
        public void onComplete(String typeName, ClassLoader classLoader, JavaModule module) {
        }
    }
}