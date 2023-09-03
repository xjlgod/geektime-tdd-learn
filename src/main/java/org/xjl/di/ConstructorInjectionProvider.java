package org.xjl.di;

import jakarta.inject.Inject;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Arrays.stream;

class ConstructorInjectionProvider<T> implements ContextConfig.ComponentProvider<T> {
    private final Constructor<T> injectConstructor;
    private List<Field> injectedFields;

    public ConstructorInjectionProvider(Class<T> component) {
        this.injectConstructor = getInjectConstructor(component);
        this.injectedFields = getInjectedFields(component);
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
            T instance = injectConstructor.newInstance(dependencies);
            for (Field field : injectedFields) {
                field.set(instance, context.get(field.getType()).get());
            }
            return instance;
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Class<?>> getDependencies() {
        return Stream.concat(stream(injectConstructor.getParameters()).map(Parameter::getType),
                injectedFields.stream().map(Field::getType)).collect(Collectors.toList());
    }

    private static <Type> List<Field> getInjectedFields(Class<Type> component) {
        List<Field> injectedFields = new ArrayList<>();
        Class<?> current = component;
        while(current != Object.class) {
            injectedFields.addAll(stream(current.getDeclaredFields())
                    .filter(field -> field.isAnnotationPresent(Inject.class)).collect(Collectors.toList()));
            current = current.getSuperclass();
        }
        return injectedFields;
    }

    private static <Type> Constructor<Type>
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
}
