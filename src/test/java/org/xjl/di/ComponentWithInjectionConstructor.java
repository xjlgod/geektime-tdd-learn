package org.xjl.di;

import jakarta.inject.Inject;

public class ComponentWithInjectionConstructor implements Component {
    private Dependency dependency;

    @Inject
    public ComponentWithInjectionConstructor(Dependency dependency) {
        this.dependency = dependency;
    }

    public Dependency getDependcy() {
        return dependency;
    }

    public void setDependcy(Dependency dependency) {
        this.dependency = dependency;
    }
}
