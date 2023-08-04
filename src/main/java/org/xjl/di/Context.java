package org.xjl.di;

import jakarta.inject.Provider;

import java.util.HashMap;
import java.util.Map;

public class Context {
    private Map<Class<?>, Provider<?>> components = new HashMap<>();

    public <ComponentType> void bind(Class<ComponentType> componentClass, ComponentType instance) {
        components.put(componentClass, (Provider<ComponentType>) () -> instance);
    }

    public <ComponentType, ComponentImplementation extends ComponentType>
    void bind(Class<ComponentType> type, Class<ComponentImplementation> implementation) {
        components.put(type, (Provider<ComponentType>) () -> {
            try {
                return (ComponentType) implementation.getConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public <ComponentType> ComponentType get(Class<ComponentType> typeClass) {
        return (ComponentType) components.get(typeClass).get();
    }
}
