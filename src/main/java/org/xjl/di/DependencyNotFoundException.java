package org.xjl.di;

public class DependencyNotFoundException extends RuntimeException {
    private Class<?> dependency;
    private Class<?> component;

    public Class<?> getComponent() {
        return component;
    }

    public void setComponent(Class<?> component) {
        this.component = component;
    }

    public DependencyNotFoundException(Class<?> dependency, Class<?> component) {
        super();
        this.dependency = dependency;
        this.component = component;
    }

    public Class<?> getDependency() {
        return dependency;
    }

    public void setDependency(Class<?> dependency) {
        this.dependency = dependency;
    }
}
