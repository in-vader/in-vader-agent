package com.github.invader.agent.interceptors.binding;

import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class InterceptorBindingTest {

    private InterceptorBinding interceptorBinding = new InterceptorBinding();
    private ElementMatcher typeMatcher;
    private ElementMatcher methodMatcher;

    @Test
    public void shouldCreateTypeMatcherForInterface() {
        // Given
        givenInterceptorBinding()
                .withInterfaceName(TestInterface.class.getName());

        // When
        whenCreatingTypeMatcher();

        // Then
        thenTypeMatcher()
                .doesMatch(TestClass.class)
                .doesNotMatch(OtherTestClass.class);
    }

    @Test
    public void shouldCreateTypeMatcherForClass() {
        // Given
        givenInterceptorBinding()
                .withClassName(TestClass.class.getName());

        // When
        whenCreatingTypeMatcher();

        // Then
        thenTypeMatcher()
                .doesMatch(TestClass.class)
                .doesNotMatch(OtherTestClass.class);
    }

    @Test
    public void shouldCreateTypeMatcherForSubclass() {
        // Given
        givenInterceptorBinding()
                .withClassName(OtherTestClass.class.getName())
                .withIncludeSubclasses(true);

        // When
        whenCreatingTypeMatcher();

        // Then
        thenTypeMatcher()
                .doesMatch(TestClass.class)
                .doesMatch(OtherTestClass.class);
    }

    @Test
    public void shouldCreateExactMethodMatcher() throws NoSuchMethodException {
        // Given
        givenInterceptorBinding()
                .withMethod("test", Integer.class);

        // When
        whenCreatingMethodMatcher();

        // Then
        thenMethodMatcher()
                .doesMatch(TestClass.class.getMethod("test", Integer.class))
                .doesNotMatch(TestClass.class.getMethod("test", Double.class));
    }

    @Test
    public void shouldCreateNotExactMethodMatcher() throws NoSuchMethodException {
        // Given
        givenInterceptorBinding()
                .withMethod("test", null);

        // When
        whenCreatingMethodMatcher();

        // Then
        thenMethodMatcher()
                .doesMatch(TestClass.class.getMethod("test", Integer.class))
                .doesMatch(TestClass.class.getMethod("test", Double.class));
    }

    @Test
    public void shouldCreateExactMethodMatcherForParameterlessMethod() throws NoSuchMethodException {
        // Given
        givenInterceptorBinding()
                .withMethod("test");

        // When
        whenCreatingMethodMatcher();

        // Then
        thenMethodMatcher()
                .doesMatch(TestClass.class.getMethod("test"))
                .doesNotMatch(TestClass.class.getMethod("test", Integer.class))
                .doesNotMatch(TestClass.class.getMethod("test", Double.class));
    }

    private MethodMatcherResultAssert thenMethodMatcher() {
        return new MethodMatcherResultAssert();
    }

    private void whenCreatingMethodMatcher() {
        methodMatcher = interceptorBinding.getMethodMatcher();
    }

    private void whenCreatingTypeMatcher() {
        typeMatcher = interceptorBinding.getTypeMatcher();
    }

    private TypeMatcherResultAssert thenTypeMatcher() {
        return new TypeMatcherResultAssert();
    }

    private InterceptorBindingAssembler givenInterceptorBinding() {
        return new InterceptorBindingAssembler();
    }

    private class InterceptorBindingAssembler {
        public InterceptorBindingAssembler withInterfaceName(String interfaceName) {
            interceptorBinding.setInterfaceName(interfaceName);
            return this;
        }

        public InterceptorBindingAssembler withClassName(String className) {
            interceptorBinding.setClassName(className);
            return this;
        }

        public InterceptorBindingAssembler withIncludeSubclasses(boolean includeSubclasses) {
            interceptorBinding.setIncludeSubclasses(includeSubclasses);
            return this;
        }

        public InterceptorBindingAssembler withMethod(String name, Class<?>...parameterTypes) {
            if (interceptorBinding.getMethods() == null) {
                interceptorBinding.setMethods(new ArrayList<>());
            }
            InterceptorBinding.Method method = new InterceptorBinding.Method();
            method.setName(name);
            method.setParameters(parameterTypes == null ?
                    null :
                    Arrays.asList(parameterTypes).stream().map(p -> p.getName()).collect(Collectors.toList()));
            interceptorBinding.getMethods().add(method);
            return this;
        }
    }

    private class TypeMatcherResultAssert {
        public TypeMatcherResultAssert doesMatch(Class<?> clazz) {
            assertThat(typeMatcher.matches(new TypeDescription.ForLoadedType(clazz))).isTrue();
            return this;
        }

        public TypeMatcherResultAssert doesNotMatch(Class<?> clazz) {
            assertThat(typeMatcher.matches(new TypeDescription.ForLoadedType(clazz))).isFalse();
            return this;
        }
    }

    private class MethodMatcherResultAssert {
        public MethodMatcherResultAssert doesMatch(Method method) {
            assertThat(methodMatcher.matches(new MethodDescription.ForLoadedMethod(method))).isTrue();
            return this;
        }

        public MethodMatcherResultAssert doesNotMatch(Method method) {
            assertThat(methodMatcher.matches(new MethodDescription.ForLoadedMethod(method))).isFalse();
            return this;
        }
    }

    public interface TestInterface {
    }

    public class TestClass extends OtherTestClass implements TestInterface {
        public void test() {
        }

        public void test(Integer par) {
        }

        public void test(Double par){
        }
    }

    public class OtherTestClass {
    }
}