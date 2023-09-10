package org.xjl.di;

import jakarta.inject.Inject;

public class InjectMethodWithDependency {
    Dependency dependency;

    @Inject
    public void install(Dependency dependency) {
        this.dependency = dependency;
    }

}
