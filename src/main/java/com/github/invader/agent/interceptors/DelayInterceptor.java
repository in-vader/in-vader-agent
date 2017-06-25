package com.github.invader.agent.interceptors;

import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import org.apache.commons.lang3.RandomUtils;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Consumer;

@Slf4j
public class DelayInterceptor extends Interceptor {

    private AtomicInteger minDelay;
    private AtomicInteger maxDelay;
    private Consumer<Long> sleeper;
    private BiFunction<Integer, Integer, Long> delayGenerator;

    public DelayInterceptor(Consumer<Long> sleeper, BiFunction<Integer, Integer, Long> delayGenerator) {
        this.sleeper = sleeper;
        this.delayGenerator = delayGenerator;
        this.minDelay = new AtomicInteger(0);
        this.maxDelay = new AtomicInteger(0);
    }

    public DelayInterceptor() {
        this(delay -> {
                    try {
                        Thread.sleep(delay);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                },
                (min, max) -> RandomUtils.nextLong(min, max));
    }

    @Override
    public String getName() {
        return "delay";
    }

    @RuntimeType
    public Object intercept(@SuperCall Callable<?> callable) throws Exception {
        // TODO: refactor the isEnabled check into the Interceptor base class (investigate why interceptors don't work on inherited methods)
        if (isEnabled()) {
            long delay = delayGenerator.apply(minDelay.get(), maxDelay.get());
            if (delay > 0) {
                log.info("Sleeping for {} ms", delay);
                sleeper.accept(delay);
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
