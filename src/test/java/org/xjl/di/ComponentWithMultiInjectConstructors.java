package org.xjl.di;

import jakarta.inject.Inject;

public class ComponentWithMultiInjectConstructors implements Component {
    @Inject
    public ComponentWithMultiInjectConstructors(String name, double value) {
    }

    @Inject
    public ComponentWithMultiInjectConstructors(String name) {
    }
}
