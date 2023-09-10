package org.xjl.di;

import jakarta.inject.Inject;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Arrays.stream;

class ConstructorInjectionProvider<T> implements ContextConfig.ComponentProvider<T> {
    private final Constructor<T> injectConstructor;
    private List<Field> injectedFields;
    private List<Method> injectedMethods;

    public ConstructorInjectionProvider(Class<T> component) {
        this.injectConstructor = getInjectConstructor(component);
        this.injectedFields = getInjectedFields(component);
        this.injectedMethods = getInjectedMethods(component);
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
            for (Method method : injectedMethods) {
                method.invoke(instance, stream(method.getParameterTypes())
                        .map(t -> context.get(t).get()).toArray(Object[]::new));
            }
            return instance;
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Class<?>> getDependencies() {
        return Stream.concat(Stream.concat(stream(injectConstructor.getParameters()).map(Parameter::getType),
                injectedFields.stream().map(Field::getType)),
                injectedMethods.stream().flatMap(m -> stream(m.getParameterTypes()))).collect(Collectors.toList());
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

    private static <Type> List<Method> getInjectedMethods(Class<Type> component) {
        List<Method> injectedMethods = new ArrayList<>();
        Class<?> current = component;
        while(current != Object.class) {
            injectedMethods.addAll(stream(current.getDeclaredMethods())
                    .filter(method -> method.isAnnotationPresent(Inject.class))
                    .filter(m -> injectedMethods.stream().noneMatch(o -> o.getName()
                            .equals(m.getName()) && Arrays.equals(o.getParameterTypes(), m.getParameterTypes())))
                    .filter(m -> stream(component.getDeclaredMethods()).filter(m1 -> !m1.isAnnotationPresent(Inject.class))
                            .noneMatch(o -> o.getName()
                                    .equals(m.getName()) && Arrays.equals(o.getParameterTypes(), m.getParameterTypes())))
                    .collect(Collectors.toList()));
            current = current.getSuperclass();
        }
        Collections.reverse(injectedMethods);
        return injectedMethods;
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
