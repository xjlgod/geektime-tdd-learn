package org.xjl.di;

import java.lang.reflect.Constructor;
import java.util.*;
import java.util.List;

import static java.util.Arrays.stream;

public class ContextConfig{
    private final Map<Class<?>, ComponentProvider<?>> providers = new HashMap<>();

    public <ComponentType> void bind(Class<ComponentType> componentClass, ComponentType instance) {
        providers.put(componentClass, new ComponentProvider<Object>() {
            @Override
            public Object get(Context context) {
                return instance;
            }

            @Override
            public List<Class<?>> getDependencies() {
                return Collections.emptyList();
            }
        });
    }

    public <Type, Implementation extends Type>
    void bind(Class<Type> type, Class<Implementation> implementation) {
        providers.put(type, new ConstructorInjectionProvider<>(implementation));
    }

    public Context getContext() {
        // check dependencies
        for (Class<?> component : providers.keySet()) {
            checkDependencies(component, new Stack<>());
        }

        return new Context() {
            @Override
            public <Type> Optional<Type> get(Class<Type> typeClass) {
                return Optional.ofNullable(providers.get(typeClass)).map(provider -> (Type) provider.get(this));
            }
        };
    }

    private void checkDependencies(Class<?> component, Stack<Class<?>> visiting) {
        for(Class<?> dependency : providers.get(component).getDependencies()) {
            if (!providers.containsKey(dependency)) throw new DependencyNotFoundException(dependency, component);
            if (visiting.contains(dependency)) throw new CyclicDependenciesFoundException(visiting);
            visiting.push(dependency);
            checkDependencies(dependency, visiting);
            visiting.pop();
        }
    }

    interface ComponentProvider<T> {
        T get(Context context);
        List<Class<?>> getDependencies();
    }

}
