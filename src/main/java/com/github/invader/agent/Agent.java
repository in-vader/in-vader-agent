package com.github.invader.agent;

import com.github.invader.agent.config.AgentConfiguration;
import com.github.invader.agent.config.AgentConfigurationParser;
import com.github.invader.agent.config.InterceptorConfigTask;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.utility.JavaModule;

import java.lang.instrument.Instrumentation;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public class Agent {
    public static void premain(String args, Instrumentation instrumentation) {
        AgentConfiguration agentConfiguration = new AgentConfigurationParser().parse();

        Stream.of(new HttpServletFailInterceptor(), new HttpServletDelayInterceptor())
                .forEach(interceptor -> {
                    new AgentBuilder.Default()
                            .with(new LoggingListener())
                            .type(interceptor.getTypeMatcher())
                            .transform((builder, type, classLoader) -> builder.method(interceptor.getMethodMatcher())
                                    .intercept(MethodDelegation.to(interceptor))
                            ).installOn(instrumentation);

                    Executors.newScheduledThreadPool(5).scheduleWithFixedDelay(
                            new InterceptorConfigTask(agentConfiguration, interceptor), 0, 10, TimeUnit.SECONDS);
                });
    }

    public static class LoggingListener implements AgentBuilder.Listener {

        @Override
        public void onTransformation(TypeDescription typeDescription, ClassLoader classLoader, JavaModule module, DynamicType dynamicType) {
            System.out.println("Transformed - " + typeDescription + ", type = " + dynamicType);
        }

        @Override
        public void onIgnored(TypeDescription typeDescription, ClassLoader classLoader, JavaModule module) {
        }

        @Override
        public void onError(String typeName, ClassLoader classLoader, JavaModule module, Throwable throwable) {
            System.out.println("Error - " + typeName + ", " + throwable.getMessage());
            throwable.printStackTrace();
        }

        @Override
        public void onComplete(String typeName, ClassLoader classLoader, JavaModule module) {
        }
    }
}