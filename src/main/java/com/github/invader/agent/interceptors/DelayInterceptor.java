package com.github.invader.agent.interceptors;

import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import org.apache.commons.lang3.RandomUtils;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public abstract class DelayInterceptor extends Interceptor {

    private AtomicInteger minDelay;
    private AtomicInteger maxDelay;

    public DelayInterceptor() {
        minDelay = new AtomicInteger(0);
        maxDelay = new AtomicInteger(0);
    }

    @Override
    public String getName() {
        return "delay";
    }

    protected Object doIntercept(Callable<?> callable) throws Exception {
        if (isEnabled()) {
            long delay = RandomUtils.nextLong(minDelay.get(), maxDelay.get());
            if (delay > 0) {
                log.info("Sleeping for {} ms", delay);
                Thread.sleep(delay);
            }
        }

        return callable.call();
    }

    @Override
    protected void applyConfig(Map<String, Object> config) {
        minDelay.set((int) config.get("min"));
        maxDelay.set((int) config.get("max"));
    }
}
