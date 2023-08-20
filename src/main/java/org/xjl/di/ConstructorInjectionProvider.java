package org.xjl.di;

import jakarta.inject.Inject;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

class ConstructorInjectionProvider<T> implements ContextConfig.ComponentProvider<T> {
    private final Constructor<T> injectConstructor;

    public ConstructorInjectionProvider(Class<?> component) {
        this.injectConstructor = (Constructor<T>) getInjectConstructor(component);
    }

    private <Type> Constructor<Type>
    getInjectConstructor(Class<Type> implementation) {
        List<Constructor<?>> injectConstructors = stream(implementation.getConstructors()).filter(c -> c.isAnnotationPresent(Inject.class))
                .collect(Collectors.toList());
        if (injectConstructors.size() > 1) throw new IllegalComponentExceptionException();

        return (Constructor<Type>) injectConstructors.stream().findFirst().orElseGet(() -> {
            try {
                return implementation.getConstructor();
            } catch (NoSuchMethodException e) {
                throw new IllegalComponentExceptionException();
            }
        });
    }

    @Override
    public T get(Context context) {
        try {
            Object[] dependencies = stream(injectConstructor.getParameters())
                    .map((p) -> {
                        Class<?> typeClass = p.getType();
                        return context.get(typeClass).get();
                    })
                    .toArray(Object[]::new);
            return injectConstructor.newInstance(dependencies);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Class<?>> getDependencies() {
        return stream(injectConstructor.getParameters()).map(Parameter::getType).collect(Collectors.toList());
    }
}
