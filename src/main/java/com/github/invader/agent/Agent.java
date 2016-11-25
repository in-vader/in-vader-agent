package com.github.invader.agent;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.utility.JavaModule;

import java.lang.instrument.Instrumentation;
import java.util.Arrays;

public class Agent {
    public static void premain(String args, Instrumentation instrumentation) {
        Arrays.asList(new HttpServletFailInterceptor(), new HttpServletDelayInterceptor())
                .stream()
                .forEach(i -> {
                    new AgentBuilder.Default()
                            .with(new LoggingListener())
                            .type(i.getTypeMatcher())
                            .transform((builder, type, classLoader) -> builder.method(i.getMethodMatcher())
                                    .intercept(MethodDelegation.to(i))
                            ).installOn(instrumentation);
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
