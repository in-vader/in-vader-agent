package com.github.invader.agent;

import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class HttpServletDelayInterceptor extends Interceptor {

    private AtomicInteger minDelay;
    private AtomicInteger maxDelay;

    HttpServletDelayInterceptor() {
        minDelay = new AtomicInteger(0);
        maxDelay = new AtomicInteger(0);
    }

    @Override
    public String getName() {
        return "delay";
    }

    @Override
    public ElementMatcher<? super TypeDescription> getTypeMatcher() {
        return ElementMatchers.named("javax.servlet.http.HttpServlet");
    }

    @Override
    public ElementMatcher<? super MethodDescription> getMethodMatcher() {
        return ElementMatchers.named("service").and(ElementMatchers.isPublic());
    }

    @Override
    @RuntimeType
    public Object intercept(@AllArguments Object[] allArguments, @Origin Method method, @SuperCall Callable<?> callable) throws Exception {
        if (isEnabled()) {
            long delay = RandomUtils.nextLong(minDelay.get(), maxDelay.get());
            if (delay > 0) {
                // TODO: add more details about request that is being delayed (i.e. method, path)
                log.info("Sleeping for {} ms", delay);
                Thread.sleep(delay);
            }
        }

        return callable.call();
    }

    @Override
    protected void applyConfig(Map<String, Object> config) {
        minDelay.set((Integer) config.get("min"));
        maxDelay.set((Integer) config.get("max"));
    }
}