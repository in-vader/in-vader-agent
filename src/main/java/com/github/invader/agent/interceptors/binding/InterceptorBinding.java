package com.github.invader.agent.interceptors.binding;

import lombok.Data;
import net.bytebuddy.description.NamedElement;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

@Data
public class InterceptorBinding {

    private String name;
    private String className;
    private boolean includeSubclasses;
    private String interfaceName;
    private List<Method> methods;

    public ElementMatcher getTypeMatcher() {
        if (StringUtils.isNotBlank(className)) {
            ElementMatcher.Junction<NamedElement> matcher = ElementMatchers.named(className);
            return includeSubclasses ? matcher.or(ElementMatchers.hasSuperType(ElementMatchers.named(className))) : matcher;
        } else if (StringUtils.isNotBlank(interfaceName)) {
            return
                    ElementMatchers
                            .hasSuperType(ElementMatchers.named(interfaceName))
                            .and(ElementMatchers.not(ElementMatchers.isInterface()));
        }

        throw new IllegalArgumentException("className or interfaceName must be present");
    }

    public ElementMatcher getMethodMatcher() {
        return
                methods.stream()
                        .map(m -> createMethodMatcher(m))
                        .reduce((e1, e2) -> e1.or(e2))
                        .get();
    }

    private ElementMatcher.Junction<MethodDescription> createMethodMatcher(Method method) {
        ElementMatcher.Junction<MethodDescription> matcher = ElementMatchers.named(method.name);

        if (method.parameters == null) {
            return matcher;
        } else if (method.parameters.isEmpty()) {
            return matcher.and(ElementMatchers.takesArguments(0));
        } else {
            for (int i = 0; i < method.parameters.size(); i++) {
                matcher = matcher.and(ElementMatchers.takesArgument(i, ElementMatchers.named(method.parameters.get(i))));
            }

            return matcher;
        }
    }

    @Data
    public static class Method {

        private String name;
        private List<String> parameters;
    }
}
