package com.github.invader.agent.interceptors;

import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import net.bytebuddy.matcher.ElementMatcher;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.Callable;

public abstract class Interceptor {

    private boolean enabled;

    public abstract String getName();

    public abstract ElementMatcher<? super TypeDescription> getTypeMatcher();

    public abstract ElementMatcher<? super MethodDescription> getMethodMatcher();

    @RuntimeType
    public abstract Object intercept(@AllArguments Object[] allArguments, @Origin Method method, @SuperCall Callable<?> callable) throws Exception;

    public void setConfig(Map<String, Object> config) {
        if (config == null) {
            setEnabled(false);
        } else {
            applyConfig(config);
            setEnabled(true);
        }
    }

    protected abstract void applyConfig(Map<String, Object> config);

    public boolean isEnabled() {
        return enabled;
    }

    private void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

}
