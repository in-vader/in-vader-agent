package com.github.invader.agent.interceptors;

import com.google.common.util.concurrent.AtomicDouble;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import org.apache.commons.lang3.RandomUtils;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Function;

@Slf4j
public class FailInterceptor extends Interceptor {

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

    @RuntimeType
    public Object intercept(@SuperCall Callable<?> callable) throws Exception {
        // TODO: refactor the isEnabled check into the Interceptor base class (investigate why interceptors don't work on inherited methods)
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
