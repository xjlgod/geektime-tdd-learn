package org.xjl.di;

import jakarta.inject.Inject;

public class AnotherDependencyDependedOnComponent implements AnotherDependency {
    private Component component;

    @Inject
    public AnotherDependencyDependedOnComponent(Component component) {
        this.component = component;
    }
}
