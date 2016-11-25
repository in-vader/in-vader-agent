package com.github.invader.agent;

import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import net.bytebuddy.matcher.ElementMatcher;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

public interface Interceptor {

    ElementMatcher<? super TypeDescription> getTypeMatcher();

    ElementMatcher<? super MethodDescription> getMethodMatcher();

    @RuntimeType
    Object intercept(@AllArguments Object[] allArguments, @Origin Method method, @SuperCall Callable<?> callable) throws Exception;
}
