package com.github.invader.agent.interceptors.jms;

import com.github.invader.agent.interceptors.FailInterceptor;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

public class JmsDelayInterceptor extends FailInterceptor {

    @Override
    public ElementMatcher<? super TypeDescription> getTypeMatcher() {
        return ElementMatchers.hasSuperType(ElementMatchers.named("javax.jms.MessageListener"))
                .and(ElementMatchers.not(ElementMatchers.isInterface()))
                .and(ElementMatchers.not(ElementMatchers.isAbstract()));
    }

    @Override
    public ElementMatcher<? super MethodDescription> getMethodMatcher() {
        return ElementMatchers.named("onMessage").and(ElementMatchers.isPublic());
    }

    @Override
    @RuntimeType
    public Object intercept(@AllArguments Object[] allArguments, @Origin Method method, @SuperCall Callable<?> callable) throws Exception {
        return doIntercept(callable);
    }
}