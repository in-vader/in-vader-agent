package com.github.invader.agent.interceptors.binding;

import com.github.invader.agent.interceptors.Interceptor;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.implementation.MethodDelegation;

import java.lang.instrument.Instrumentation;
import java.util.Arrays;

public class InterceptorBinder {

    private final AgentBuilder.Listener listener;
    private final Interceptor[] interceptors;
    private final Instrumentation instrumentation;

    public InterceptorBinder(AgentBuilder.Listener listener, Interceptor[] interceptors, Instrumentation instrumentation) {
        this.listener = listener;
        this.interceptors = interceptors;
        this.instrumentation = instrumentation;
    }

    public void bind(InterceptorBinding binding) {
        // TODO: simplify the code below to bind all interceptors with a single builder (i.e. chain transform calls)
        Arrays.stream(interceptors)
                .forEach(interceptor -> new AgentBuilder.Default()
                        .with(listener)
                        .type(binding.getTypeMatcher())
                        .transform((builder, type, classLoader, module) -> builder.method(binding.getMethodMatcher())
                                .intercept(MethodDelegation.to(interceptor))
                        ).installOn(instrumentation));
    }
}
