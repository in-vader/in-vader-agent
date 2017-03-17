package com.github.invader.agent.interceptors;

import com.google.common.util.concurrent.AtomicDouble;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;

import java.util.Map;
import java.util.concurrent.Callable;

@Slf4j
public abstract class FailInterceptor extends Interceptor {

    private AtomicDouble probability;

    public FailInterceptor() {
        probability = new AtomicDouble(0);
    }

    @Override
    public String getName() {
        return "failure";
    }

    protected Object doIntercept(Callable<?> callable) throws Exception {
        if (isEnabled() && RandomUtils.nextDouble(0, 1) < probability.get()) {
            log.info("Randomly failing");
            throw new RuntimeException("Randomly failing");
        } else {
            return callable.call();
        }
    }

    @Override
    protected void applyConfig(Map<String, Object> config) {
        Object probability = config.get("probability");
        this.probability.set(probability instanceof Integer ? (int) probability : (double) probability);
    }
}
