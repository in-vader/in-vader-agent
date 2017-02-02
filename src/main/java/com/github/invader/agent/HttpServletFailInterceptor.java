package com.github.invader.agent;

import com.google.common.util.concurrent.AtomicDouble;
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

public class HttpServletFailInterceptor extends Interceptor {

    private static final Logger LOG = LoggerFactory.getLogger(HttpServletFailInterceptor.class);
    private AtomicDouble probability;

    HttpServletFailInterceptor() {
        probability = new AtomicDouble(0);
    }

    @Override
    public String getName() {
        return "failure";
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
        if (isEnabled() && RandomUtils.nextDouble(0, 1) < probability.get()) {
            // TODO: add more details about request that is being delayed (i.e. method, path)
            LOG.info("Randomly failing");
            throw new RuntimeException("Randomly failing");
        } else {
            return callable.call();
        }
    }

    @Override
    protected void applyConfig(Map<String, Object> config) {
        probability.set((Double) config.get("probability"));
    }
}