package com.github.invader.agent.interceptors;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Consumer;

@Slf4j
public abstract class DelayInterceptor extends Interceptor {

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

    protected Object doIntercept(Callable<?> callable) throws Exception {
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
