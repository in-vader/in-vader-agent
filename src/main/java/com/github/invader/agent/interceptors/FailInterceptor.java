package com.github.invader.agent.interceptors;

import com.google.common.util.concurrent.AtomicDouble;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Function;

@Slf4j
public abstract class FailInterceptor extends Interceptor {

    private AtomicDouble probability;
    private Function<Double, Boolean> shouldFail;

    public FailInterceptor(Function<Double, Boolean> shouldFail) {
        this.shouldFail = shouldFail;
        this.probability = new AtomicDouble(0);
    }

    public FailInterceptor() {
        this(p -> RandomUtils.nextDouble(0, 1) < p);
    }

    @Override
    public String getName() {
        return "failure";
    }

    protected Object doIntercept(Callable<?> callable) throws Exception {
        if (isEnabled() && shouldFail.apply(probability.get())) {
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
